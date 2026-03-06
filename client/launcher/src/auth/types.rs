use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct AuthTokens {
    pub access_token: String,
    pub id_token: String,
    pub refresh_token: String,
    pub sub: String,
    pub expiry: u64,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct User {
    pub id: Option<String>,
    #[serde(rename = "userId")]
    pub user_id: String,
    #[serde(rename = "displayName")]
    pub display_name: String,
    #[serde(default)]
    pub suffix: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Account {
    #[serde(rename = "accountId")]
    pub account_id: String,
    #[serde(rename = "displayName")]
    pub display_name: String,
    #[serde(rename = "userHash")]
    pub user_hash: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Session {
    pub user: User,
    pub accounts: Vec<Account>,
    pub tokens: AuthTokens,
    pub session_id: String,
}

#[derive(Debug, Deserialize)]
pub struct TokenResponse {
    pub access_token: String,
    pub id_token: String,
    pub refresh_token: String,
    pub expires_in: u64,
}

#[derive(Debug, Deserialize)]
pub struct SessionResponse {
    #[serde(rename = "sessionId")]
    pub session_id: String,
}
