use anyhow::{anyhow, Context, Result};
use sha2::{Digest, Sha256};
use std::path::Path;

const CONTENT_URL: &str = "https://content.runescape.com/downloads/ubuntu/";
pub const DEFAULT_CONFIG_URI: &str = "https://www.runescape.com/k=5/l=0/jav_config.ws";

/// Official Jagex launcher installer URLs per OS (all verified HTTP 200).
/// The Linux launcher (`rs3linux`) comes from the Debian `.deb` flow above
/// (`fetch_package_info` + `download_deb` + `deb::extract_rs3_binary`); these
/// two cover the Windows and macOS launchers for the per-OS data layout
/// `data/client/{windows,macos}/`.
const WINDOWS_SETUP_URL: &str =
    "https://content.runescape.com/downloads/windows/RuneScape-Setup.exe";
const MACOS_DMG_URL: &str = "https://content.runescape.com/downloads/osx/RuneScape.dmg";

/// Path of the launcher binary INSIDE the macOS `RuneScape.dmg` (HFS volume).
/// Extracted with `7z` and renamed to `rs3mac`.
const MACOS_DMG_LAUNCHER_INNER: &str = "RuneScape/RuneScape.app/Contents/MacOS/RuneScape";

#[allow(dead_code)]
pub struct PackageInfo {
    pub filename: String,
    pub sha256: String,
    pub size: u64,
}

/// Fetch and parse the Packages file to get current RS3 client info
pub async fn fetch_package_info(client: &reqwest::Client) -> Result<PackageInfo> {
    let url = format!("{}dists/trusty/non-free/binary-amd64/Packages", CONTENT_URL);

    let resp = client
        .get(&url)
        .send()
        .await
        .context("Failed to fetch Packages file")?;

    if !resp.status().is_success() {
        return Err(anyhow!("Packages fetch failed: {}", resp.status()));
    }

    let text = resp.text().await.context("Failed to read Packages body")?;

    let mut filename = None;
    let mut sha256 = None;
    let mut size = None;

    for line in text.lines() {
        if let Some((key, value)) = line.split_once(": ") {
            match key {
                "Filename" => filename = Some(value.to_string()),
                "SHA256" => sha256 = Some(value.to_string()),
                "Size" => size = Some(value.parse::<u64>().unwrap_or(0)),
                _ => {}
            }
        }
    }

    Ok(PackageInfo {
        filename: filename.ok_or_else(|| anyhow!("No Filename in Packages"))?,
        sha256: sha256.ok_or_else(|| anyhow!("No SHA256 in Packages"))?,
        size: size.ok_or_else(|| anyhow!("No Size in Packages"))?,
    })
}

/// Check if the installed binary matches the expected hash
pub fn is_up_to_date(hash_path: &Path, expected_hash: &str) -> bool {
    std::fs::read_to_string(hash_path)
        .map(|stored| stored.trim() == expected_hash)
        .unwrap_or(false)
}

/// Download the .deb file. Returns `Bytes` to avoid copying the multi-MB body —
/// `Bytes` is cheaply cloneable and derefs to `&[u8]` for hashing/extraction.
pub async fn download_deb(client: &reqwest::Client, filename: &str) -> Result<bytes::Bytes> {
    let url = format!("{}{}", CONTENT_URL, filename);

    log::info!("Downloading RS3 client from {}", url);

    let resp = client
        .get(&url)
        .send()
        .await
        .context("Failed to download .deb")?;

    if !resp.status().is_success() {
        return Err(anyhow!("Download failed: {}", resp.status()));
    }

    resp.bytes().await.context("Failed to read .deb bytes")
}

/// Verify SHA256 hash of downloaded data
pub fn verify_hash(data: &[u8], expected: &str) -> bool {
    let mut hasher = Sha256::new();
    hasher.update(data);
    let result = format!("{:x}", hasher.finalize());
    result == expected
}

/// Save the hash to disk after successful extraction
pub fn save_hash(hash_path: &Path, hash: &str) -> Result<()> {
    std::fs::write(hash_path, hash).context("Failed to save hash file")
}

/// Fetch jav_config.ws and parse param=N=value lines into key-value pairs
pub async fn fetch_jav_config_params(
    client: &reqwest::Client,
    config_uri: &str,
) -> Result<Vec<(String, String)>> {
    let resp = client
        .get(config_uri)
        .send()
        .await
        .context("Failed to fetch jav_config.ws")?;
    let text = resp
        .text()
        .await
        .context("Failed to read jav_config.ws body")?;
    let mut params = Vec::new();
    for line in text.lines() {
        if let Some(rest) = line.strip_prefix("param=") {
            if let Some((key, value)) = rest.split_once('=') {
                params.push((key.to_string(), value.to_string()));
            }
        }
    }
    Ok(params)
}

/// Extract RSA modulus from param=99 if present
pub fn extract_rsa_modulus(params: &[(String, String)]) -> Option<String> {
    params.iter().find(|(k, _)| k == "99").map(|(_, v)| v.clone())
}

/// Extract JS5 RSA modulus from param=100 if present
pub fn extract_js5_modulus(params: &[(String, String)]) -> Option<String> {
    params.iter().find(|(k, _)| k == "100").map(|(_, v)| v.clone())
}

// ---------------------------------------------------------------------------
// Cross-platform Jagex launcher (`rs3*`) acquisition into the per-OS layout.
//
// Layout target:
//   data/client/windows/rs3windows.exe   (from RuneScape-Setup.exe — Inno Setup)
//   data/client/macos/rs3mac             (from RuneScape.dmg — HFS/.app bundle)
//   data/client/linux/rs3linux           (from the Debian .deb — see flow above)
//
// Extraction shells out to host CLI tools (no pure-Rust Inno/HFS readers exist
// that we want to vendor). Each path is GATED on the required tool being on
// PATH and fails gracefully with an actionable message if it is missing.
//
// The Windows/macOS acquisition helpers are only *reached* on their respective
// host OS (via `acquire_host_launcher`'s cfg branches). On a Linux build they
// compile (so the code is always type-checked) but are inert, so the whole
// block lives in the `launcher_acq` submodule below, which suppresses the
// dead-code lint on non-Windows/non-macOS hosts and re-exports its public API.
// ---------------------------------------------------------------------------

// `acquire_host_launcher` is the cross-platform entry point callers use; the
// per-OS `acquire_{windows,macos}_launcher` and `download_to_file` helpers stay
// internal to the submodule (reached only via the host cfg branches).
pub use launcher_acq::acquire_host_launcher;

#[cfg_attr(
    all(not(target_os = "windows"), not(target_os = "macos")),
    allow(dead_code)
)]
mod launcher_acq {
    use super::{
        MACOS_DMG_LAUNCHER_INNER, MACOS_DMG_URL, WINDOWS_SETUP_URL,
    };
    use anyhow::{anyhow, Context, Result};
    use std::path::Path;

/// Return the path to a usable `7-Zip` CLI (`7z`, then `7zz`, then `7za`), or
/// `None` if none is on PATH. `7z` reads both HFS (`.dmg`) volumes and many
/// installer formats.
fn find_7z() -> Option<String> {
    for candidate in ["7z", "7zz", "7za"] {
        if which_on_path(candidate) {
            return Some(candidate.to_string());
        }
    }
    None
}

/// Minimal `which`: returns true if `name` resolves on the current `PATH`.
fn which_on_path(name: &str) -> bool {
    let path = match std::env::var_os("PATH") {
        Some(p) => p,
        None => return false,
    };
    std::env::split_paths(&path).any(|dir| {
        let candidate = dir.join(name);
        candidate.is_file()
            || {
                // On Windows, tools usually carry a `.exe` suffix.
                #[cfg(windows)]
                {
                    dir.join(format!("{}.exe", name)).is_file()
                }
                #[cfg(not(windows))]
                {
                    false
                }
            }
    })
}

/// Download a URL to `dest`, returning an error if the status is not success.
/// Shared by the Windows/macOS installer downloads.
pub async fn download_to_file(
    client: &reqwest::Client,
    url: &str,
    dest: &Path,
) -> Result<()> {
    log::info!("Downloading {} -> {}", url, dest.display());
    let resp = client
        .get(url)
        .send()
        .await
        .with_context(|| format!("Failed to download {}", url))?;
    if !resp.status().is_success() {
        return Err(anyhow!("Download of {} failed: {}", url, resp.status()));
    }
    let bytes = resp
        .bytes()
        .await
        .with_context(|| format!("Failed to read body of {}", url))?;
    if let Some(parent) = dest.parent() {
        std::fs::create_dir_all(parent)
            .with_context(|| format!("Failed to create {}", parent.display()))?;
    }
    std::fs::write(dest, &bytes)
        .with_context(|| format!("Failed to write {}", dest.display()))?;
    Ok(())
}

/// Acquire the **macOS** Jagex launcher into `out` (typically
/// `data/client/macos/rs3mac`).
///
/// Pipeline: download `RuneScape.dmg` → `7z e <dmg> <inner-app-binary>` →
/// move the extracted Mach-O to `out` (chmod 0755). The dmg is an HFS+ volume;
/// `7z` reads it natively, so this works on a Linux dev host as well as macOS.
///
/// Gated on `7z`/`7zz`/`7za` being on PATH; returns an actionable error if not.
pub async fn acquire_macos_launcher(client: &reqwest::Client, out: &Path) -> Result<()> {
    let seven_zip = find_7z().ok_or_else(|| {
        anyhow!(
            "Cannot extract RuneScape.dmg: no 7-Zip CLI (7z/7zz/7za) found on PATH. \
             Install p7zip (e.g. `apt install p7zip-full` / `brew install p7zip`) and retry."
        )
    })?;

    let tmp_dir = out
        .parent()
        .map(|p| p.join(".rs3mac.extract.tmp"))
        .ok_or_else(|| anyhow!("output path {} has no parent", out.display()))?;
    let dmg_path = tmp_dir.join("RuneScape.dmg");

    // Best-effort clean of any prior partial extraction.
    let _ = std::fs::remove_dir_all(&tmp_dir);
    std::fs::create_dir_all(&tmp_dir)
        .with_context(|| format!("Failed to create temp dir {}", tmp_dir.display()))?;

    download_to_file(client, MACOS_DMG_URL, &dmg_path).await?;

    // `7z e` flattens the inner path; the extracted file lands as
    // <tmp_dir>/RuneScape (the basename of MACOS_DMG_LAUNCHER_INNER).
    let status = std::process::Command::new(&seven_zip)
        .arg("e")
        .arg("-y")
        .arg(&dmg_path)
        .arg(MACOS_DMG_LAUNCHER_INNER)
        .arg(format!("-o{}", tmp_dir.display()))
        .status()
        .with_context(|| format!("Failed to run {} on RuneScape.dmg", seven_zip))?;
    if !status.success() {
        let _ = std::fs::remove_dir_all(&tmp_dir);
        return Err(anyhow!(
            "{} exited with status {} extracting the macOS launcher from the dmg",
            seven_zip,
            status
        ));
    }

    let extracted = tmp_dir.join("RuneScape");
    if !extracted.is_file() {
        let _ = std::fs::remove_dir_all(&tmp_dir);
        return Err(anyhow!(
            "Expected extracted launcher at {} not found (dmg layout changed?)",
            extracted.display()
        ));
    }

    if let Some(parent) = out.parent() {
        std::fs::create_dir_all(parent)
            .with_context(|| format!("Failed to create {}", parent.display()))?;
    }
    std::fs::rename(&extracted, out)
        .or_else(|_| std::fs::copy(&extracted, out).map(|_| ()))
        .with_context(|| format!("Failed to place macOS launcher at {}", out.display()))?;
    #[cfg(unix)]
    {
        use std::os::unix::fs::PermissionsExt;
        let _ = std::fs::set_permissions(out, std::fs::Permissions::from_mode(0o755));
    }
    let _ = std::fs::remove_dir_all(&tmp_dir);
    log::info!("Installed macOS launcher to {}", out.display());
    Ok(())
}

/// Acquire the **Windows** Jagex launcher into `out` (typically
/// `data/client/windows/rs3windows.exe`).
///
/// Pipeline: download `RuneScape-Setup.exe` (an **Inno Setup** installer) →
/// `innoextract` it → locate the launcher exe in the extracted `{app}` tree →
/// move it to `out`.
///
/// NOTE on tooling: `RuneScape-Setup.exe` is Inno Setup, NOT NSIS. Plain `7z`
/// can only see the PE wrapper, not the inner Inno payload, so we require
/// `innoextract`. If it is absent we fail with an actionable message (the
/// Windows binary can instead be dropped in by the Kotlin client-manager).
pub async fn acquire_windows_launcher(client: &reqwest::Client, out: &Path) -> Result<()> {
    if !which_on_path("innoextract") {
        return Err(anyhow!(
            "Cannot extract RuneScape-Setup.exe: `innoextract` not found on PATH. \
             RuneScape-Setup.exe is an Inno Setup installer (not NSIS), so 7-Zip cannot \
             unpack its payload. Install innoextract (e.g. `apt install innoextract` / \
             `brew install innoextract`) and retry, or place rs3windows.exe in \
             data/client/windows/ manually."
        ));
    }

    let tmp_dir = out
        .parent()
        .map(|p| p.join(".rs3windows.extract.tmp"))
        .ok_or_else(|| anyhow!("output path {} has no parent", out.display()))?;
    let setup_path = tmp_dir.join("RuneScape-Setup.exe");

    let _ = std::fs::remove_dir_all(&tmp_dir);
    std::fs::create_dir_all(&tmp_dir)
        .with_context(|| format!("Failed to create temp dir {}", tmp_dir.display()))?;

    download_to_file(client, WINDOWS_SETUP_URL, &setup_path).await?;

    // innoextract lays files out under <tmp_dir>/app/ (the Inno `{app}` dir).
    // `-e` extracts, `-d` sets the output base.
    let status = std::process::Command::new("innoextract")
        .arg("-e")
        .arg("-d")
        .arg(&tmp_dir)
        .arg(&setup_path)
        .status()
        .context("Failed to run innoextract on RuneScape-Setup.exe")?;
    if !status.success() {
        let _ = std::fs::remove_dir_all(&tmp_dir);
        return Err(anyhow!(
            "innoextract exited with status {} extracting the Windows launcher",
            status
        ));
    }

    // Find the launcher exe in the extracted tree. The Jagex installer ships the
    // launcher as `rs3windows.exe` (historically `RuneScape.exe` / `runescape.exe`);
    // match any of those, case-insensitively, preferring rs3windows.exe.
    let exe = find_windows_launcher_exe(&tmp_dir).ok_or_else(|| {
        anyhow!(
            "No launcher .exe found in the extracted Inno Setup payload under {} \
             (expected rs3windows.exe / RuneScape.exe). The installer layout may have changed.",
            tmp_dir.display()
        )
    })?;

    if let Some(parent) = out.parent() {
        std::fs::create_dir_all(parent)
            .with_context(|| format!("Failed to create {}", parent.display()))?;
    }
    std::fs::rename(&exe, out)
        .or_else(|_| std::fs::copy(&exe, out).map(|_| ()))
        .with_context(|| format!("Failed to place Windows launcher at {}", out.display()))?;
    let _ = std::fs::remove_dir_all(&tmp_dir);
    log::info!("Installed Windows launcher to {}", out.display());
    Ok(())
}

/// Recursively search `root` for the Windows launcher executable, preferring
/// `rs3windows.exe`, then any `*.exe` whose stem looks like the RS launcher.
fn find_windows_launcher_exe(root: &Path) -> Option<std::path::PathBuf> {
    fn walk(dir: &Path, out: &mut Vec<std::path::PathBuf>) {
        let entries = match std::fs::read_dir(dir) {
            Ok(e) => e,
            Err(_) => return,
        };
        for entry in entries.flatten() {
            let p = entry.path();
            if p.is_dir() {
                walk(&p, out);
            } else if p
                .extension()
                .map(|e| e.eq_ignore_ascii_case("exe"))
                .unwrap_or(false)
            {
                out.push(p);
            }
        }
    }
    let mut exes = Vec::new();
    walk(root, &mut exes);

    // Prefer an exact rs3windows.exe match.
    if let Some(p) = exes
        .iter()
        .find(|p| matches_name(p, "rs3windows.exe"))
    {
        return Some(p.clone());
    }
    // Then a RuneScape-launcher-looking exe (not the unins*/setup helper exes).
    exes.into_iter().find(|p| {
        let name = p
            .file_name()
            .map(|n| n.to_string_lossy().to_ascii_lowercase())
            .unwrap_or_default();
        (name.contains("runescape") || name.contains("rs3"))
            && !name.starts_with("unins")
            && !name.contains("setup")
    })
}

fn matches_name(p: &Path, name: &str) -> bool {
    p.file_name()
        .map(|n| n.to_string_lossy().eq_ignore_ascii_case(name))
        .unwrap_or(false)
}

/// Acquire the Jagex launcher for the CURRENT host OS into the canonical
/// per-OS path under `client_root` (`<client_root>/<host-os>/rs3*`).
///
/// On Linux this is a no-op stub: the Linux launcher comes from the `.deb`
/// flow (`fetch_package_info`/`download_deb`/`deb::extract_rs3_binary`) which
/// the live-mode launch path already drives. This helper exists so callers can
/// uniformly request "make sure the host launcher exists" across OSes.
pub async fn acquire_host_launcher(client: &reqwest::Client, client_root: &Path) -> Result<()> {
    #[cfg(target_os = "windows")]
    {
        let out = client_root.join("windows").join("rs3windows.exe");
        if out.is_file() {
            return Ok(());
        }
        return acquire_windows_launcher(client, &out).await;
    }
    #[cfg(target_os = "macos")]
    {
        let out = client_root.join("macos").join("rs3mac");
        if out.is_file() {
            return Ok(());
        }
        return acquire_macos_launcher(client, &out).await;
    }
    #[cfg(all(not(target_os = "windows"), not(target_os = "macos")))]
    {
        // Linux: the .deb flow installs rs3linux into the launcher's data dir.
        let _ = (client, client_root);
        Ok(())
    }
}
} // mod launcher_acq

