#!/usr/bin/env bash
# Launch the macOS RuneScape wrapper/client against the local Darkan server.
# Usage: ./run-client-mac.sh
#
# macOS analogue of run-client.sh (Linux). Emulates the launcher's custom mode:
# - Sets working directory / HOME to ~/.darkan3-mac
# - Creates preferences.cfg there
# - Applies the DYLD_INSERT_LIBRARIES patcher (libdarkan_patcher.dylib)
# - Spawns RuneScape.app's executable directly (NOT via `open`/LaunchServices,
#   which strips DYLD_* env) with `--configURI`
#
# Env vars (override on the command line or via .env at repo root):
#   HOST                  server host           (default: localhost)
#   PORT                  server port           (default: 8829)
#   WRAPPER_BINARY        path to RuneScape app executable
#   CLIENT_BINARY         path to rs2client served by ConfigServer
#   DARKAN_RSA_MODULUS    login RSA modulus hex (forwarded to the patcher)
#   DARKAN_JS5_RSA_MODULUS JS5 RSA modulus hex  (forwarded to the patcher)
#   DARKAN_HTTP_PORT      HTTP port to patch in (P3)
#   DARKAN_PROXY_MODE     "1" for proxy mode    (skips JS5 + HTTP-port patches)
#   MAC_CLIENT_MODE       wrapper | direct       (default: wrapper)
#   SCHEME                direct mode only: rs-launch | rs-launchs

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

HOST="${HOST:-localhost}"
PORT="${PORT:-8829}"
SCHEME="${SCHEME:-rs-launch}"
MAC_CLIENT_MODE="${MAC_CLIENT_MODE:-wrapper}"

# Canonical mac slots.
WRAPPER_BINARY="${WRAPPER_BINARY:-/Users/robert/darkan-3/macos/RuneScape.app/Contents/MacOS/RuneScape}"
CLIENT_BINARY="${CLIENT_BINARY:-/Users/robert/darkan-3/macos/Jagex/launcher/rs2client}"

# The adhoc-signed x86_64 patcher dylib produced by patcher-mac/build-mac.sh.
PATCHER_DYLIB="${PATCHER_DYLIB:-$PROJECT_DIR/client/launcher/patcher-mac/target/x86_64-apple-darwin/release/libdarkan_patcher.dylib}"

DARKAN_DIR="${DARKAN_DIR:-$HOME/.darkan3-mac}"

# Optional recorder dylib — set DARKAN_RECORD=1 to also insert the socket recorder
# (alongside the patcher) and capture client I/O to $RECORD_DIR. Default off.
RECORDER_DYLIB="${RECORDER_DYLIB:-$PROJECT_DIR/client/launcher/recorder-mac/target/x86_64-apple-darwin/release/libdarkan_recorder.dylib}"
if [[ "${DARKAN_RECORD:-0}" == "1" && -f "$RECORDER_DYLIB" ]]; then
    INSERT_LIBS="$RECORDER_DYLIB:$PATCHER_DYLIB"
    RECORD_DIR="${DARKAN_RECORD_DIR:-$DARKAN_DIR/recorder}"
    mkdir -p "$RECORD_DIR"
else
    INSERT_LIBS="$PATCHER_DYLIB"
fi

if [[ "$MAC_CLIENT_MODE" == "direct" ]]; then
    CONFIG_URI="${CONFIG_URI:-${SCHEME}://${HOST}:${PORT}/jav_config.ws}"
    TARGET_BINARY="$CLIENT_BINARY"
    TARGET_ARGS=("$CONFIG_URI")
elif [[ "$MAC_CLIENT_MODE" == "wrapper" ]]; then
    CONFIG_URI="${CONFIG_URI:-http://${HOST}:${PORT}/jav_config.ws}"
    TARGET_BINARY="$WRAPPER_BINARY"
    TARGET_ARGS=("--configURI" "$CONFIG_URI")
else
    echo "ERROR: MAC_CLIENT_MODE must be 'wrapper' or 'direct' (got '$MAC_CLIENT_MODE')" >&2
    exit 1
fi

if [[ ! -x "$TARGET_BINARY" ]]; then
    echo "ERROR: macOS target binary not found / not executable at:" >&2
    echo "       $TARGET_BINARY" >&2
    echo "       Set \$WRAPPER_BINARY or \$CLIENT_BINARY as needed." >&2
    exit 1
fi

if [[ ! -f "$PATCHER_DYLIB" ]]; then
    echo "ERROR: Patcher dylib not found at $PATCHER_DYLIB" >&2
    echo "Build it: cd client/launcher/patcher-mac && ./build-mac.sh" >&2
    exit 1
fi

# Warn (don't fail) if the dylib isn't adhoc-signed — dyld may refuse it.
if ! codesign -v "$PATCHER_DYLIB" >/dev/null 2>&1; then
    echo "WARNING: $PATCHER_DYLIB is not validly signed; dyld may refuse to insert it." >&2
    echo "         Re-run patcher-mac/build-mac.sh (it adhoc-signs the dylib)." >&2
fi

# Export patcher env vars from .env (same loader as run-client.sh).
if [[ -f "$PROJECT_DIR/.env" ]]; then
    while IFS= read -r line; do
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        export "$line"
    done < "$PROJECT_DIR/.env"
fi

modulus_hex() {
    local env_name="$1"
    local kt_name="$2"
    local value="${!env_name:-}"
    python3 - "$PROJECT_DIR/core/src/main/kotlin/org/darkan/core/EnvVars.kt" "$kt_name" "$value" <<'PY'
import re
import sys

path, kt_name, value = sys.argv[1:]
if not value:
    text = open(path, encoding="utf-8").read()
    match = re.search(
        r'val\s+' + re.escape(kt_name) +
        r'\s*:\s*String\s*=\s*dotenv\.get\("[^"]+",\s*"([0-9]+)"\)',
        text,
    )
    if not match:
        raise SystemExit(f"missing default for {kt_name} in {path}")
    value = match.group(1)
print(format(int(value, 10), "x"))
PY
}

DARKAN_RSA_MODULUS="${DARKAN_RSA_MODULUS:-$(modulus_hex RSA_LOGIN_MODULUS loginRsaModulus)}"
DARKAN_JS5_RSA_MODULUS="${DARKAN_JS5_RSA_MODULUS:-$(modulus_hex RSA_JS5_MODULUS js5RsaModulus)}"
DARKAN_HTTP_PORT="${DARKAN_HTTP_PORT:-${CONFIG_HTTP_PORT:-$PORT}}"

# Set up the mac data dir exactly like the launcher's custom mode.
mkdir -p "$DARKAN_DIR"
if [[ -f "$PROJECT_DIR/client/preferences.cfg" ]]; then
    cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"
fi

cd "$DARKAN_DIR"

echo "Launching macOS rs2client"
echo "  mode   : $MAC_CLIENT_MODE"
echo "  target : $TARGET_BINARY"
echo "  args   : ${TARGET_ARGS[*]}"
echo "  dylib  : $PATCHER_DYLIB"
echo "  HOME   : $DARKAN_DIR"
echo "  patcher env: DARKAN_RSA_MODULUS=${DARKAN_RSA_MODULUS:+<set>}${DARKAN_RSA_MODULUS:-<unset>} \
DARKAN_JS5_RSA_MODULUS=${DARKAN_JS5_RSA_MODULUS:+<set>}${DARKAN_JS5_RSA_MODULUS:-<unset>} \
DARKAN_HTTP_PORT=${DARKAN_HTTP_PORT:-<unset>} DARKAN_PROXY_MODE=${DARKAN_PROXY_MODE:-<unset>}"

# Spawn directly with the env set. Do NOT use `open` — LaunchServices strips
# DYLD_* env vars, so the patcher would never load.
#
# We pass through the DARKAN_* vars only if they are set, so an unset modulus
# leaves the patcher a no-op (live mode), matching the launcher's gating.
env \
    HOME="$DARKAN_DIR" \
    DYLD_INSERT_LIBRARIES="$INSERT_LIBS" \
    ${DARKAN_RECORD:+DARKAN_RECORD="$DARKAN_RECORD"} \
    ${RECORD_DIR:+DARKAN_RECORD_DIR="$RECORD_DIR"} \
    ${DARKAN_RSA_MODULUS:+DARKAN_RSA_MODULUS="$DARKAN_RSA_MODULUS"} \
    ${DARKAN_JS5_RSA_MODULUS:+DARKAN_JS5_RSA_MODULUS="$DARKAN_JS5_RSA_MODULUS"} \
    ${DARKAN_HTTP_PORT:+DARKAN_HTTP_PORT="$DARKAN_HTTP_PORT"} \
    ${DARKAN_PROXY_MODE:+DARKAN_PROXY_MODE="$DARKAN_PROXY_MODE"} \
    "$TARGET_BINARY" "${TARGET_ARGS[@]}" 2>&1 || true

EC=${PIPESTATUS[0]:-$?}
if [[ $EC -gt 128 ]]; then
    echo "Client killed by signal $((EC - 128))"
elif [[ $EC -ne 0 ]]; then
    echo "Client exited with code $EC"
fi
