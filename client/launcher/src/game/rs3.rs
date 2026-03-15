use anyhow::{anyhow, Context, Result};
use sha2::{Digest, Sha256};
use std::path::Path;

const CONTENT_URL: &str = "https://content.runescape.com/downloads/ubuntu/";
pub const DEFAULT_CONFIG_URI: &str = "https://www.runescape.com/k=5/l=0/jav_config.ws";

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

/// Download the .deb file
pub async fn download_deb(client: &reqwest::Client, filename: &str) -> Result<Vec<u8>> {
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

    resp.bytes()
        .await
        .map(|b| b.to_vec())
        .context("Failed to read .deb bytes")
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

