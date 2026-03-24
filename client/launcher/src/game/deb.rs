use anyhow::{anyhow, Context, Result};
use std::io::{Cursor, Read};
use std::path::Path;
use tar::Archive as TarArchive;
use xz2::read::XzDecoder;

const GAME_BINARY_PATH: &str = "./usr/share/games/runescape-launcher/runescape";

/// Extract the RS3 binary from a .deb file (ar > data.tar.xz > game binary)
pub fn extract_rs3_binary(deb_bytes: &[u8], output: &Path) -> Result<()> {
    let cursor = Cursor::new(deb_bytes);
    let mut archive = ar::Archive::new(cursor);

    // Find data.tar.xz in the .deb (ar format)
    let mut data_tar_xz = None;
    while let Some(entry) = archive.next_entry() {
        let mut entry = entry.context("Failed to read ar entry")?;
        let name = std::str::from_utf8(entry.header().identifier())
            .context("Invalid ar entry name")?
            .to_string();

        if name == "data.tar.xz" || name.starts_with("data.tar.xz") {
            let mut buf = Vec::new();
            entry
                .read_to_end(&mut buf)
                .context("Failed to read data.tar.xz from .deb")?;
            data_tar_xz = Some(buf);
            break;
        }
    }

    let data_tar_xz =
        data_tar_xz.ok_or_else(|| anyhow!("No data.tar.xz found in .deb file"))?;

    // Decompress xz, then read tar
    let xz_reader = XzDecoder::new(Cursor::new(data_tar_xz));
    let mut tar = TarArchive::new(xz_reader);

    for entry in tar.entries().context("Failed to read tar entries")? {
        let mut entry = entry.context("Failed to read tar entry")?;
        let path = entry
            .path()
            .context("Failed to read tar entry path")?
            .to_string_lossy()
            .to_string();

        if path == GAME_BINARY_PATH || path == &GAME_BINARY_PATH[2..] {
            // Strip leading "./"
            if let Some(parent) = output.parent() {
                std::fs::create_dir_all(parent)?;
            }

            let mut buf = Vec::new();
            entry
                .read_to_end(&mut buf)
                .context("Failed to read game binary from tar")?;

            // Unlink first to avoid ETXTBSY if a previous instance is still running.
            if output.exists() {
                let _ = std::fs::remove_file(output);
            }
            std::fs::write(output, &buf).context("Failed to write game binary")?;

            #[cfg(unix)]
            {
                use std::os::unix::fs::PermissionsExt;
                std::fs::set_permissions(output, std::fs::Permissions::from_mode(0o755))?;
            }

            log::info!("Extracted RS3 binary to {}", output.display());
            return Ok(());
        }
    }

    Err(anyhow!(
        "Game binary '{}' not found in .deb archive",
        GAME_BINARY_PATH
    ))
}
