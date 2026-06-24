#!/usr/bin/env bash
# Launch the macOS NXT client under BOTH the patcher and the recorder against
# our local Darkan server, to validate the recorder pipeline (Phase 1).
#
# This is run-client-mac.sh + the recorder dylib + DARKAN_RECORD=1. The two
# dylibs are colon-separated in DYLD_INSERT_LIBRARIES (the recorder is inert
# unless DARKAN_RECORD=1, the patcher inert unless DARKAN_RSA_MODULUS/proxy).
# We direct-exec (NOT `open`) so DYLD_* survives (LaunchServices strips it).
#
# Env (override on the CLI):
#   HOST/PORT            server host/config-http port (default localhost:8829)
#   CLIENT_BINARY        rs2client path (default: the staging 948-5-mac binary)
#   DARKAN_RSA_MODULUS   login RSA modulus hex (default: our dev login modulus)
#   DARKAN_JS5_RSA_MODULUS JS5 RSA modulus hex  (default: EnvVars default)
#   DARKAN_HTTP_PORT      HTTP port to patch in  (default: PORT)
#   DARKAN_RECORD_DIR    capture output dir   (default: ~/.darkan3-mac/recorder)
#   RUN_SECS             auto-kill after N seconds (default: 25; 0 = run forever)
#   SCHEME               rs-launch | rs-launchs (default: rs-launch)

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

HOST="${HOST:-localhost}"
PORT="${PORT:-8829}"
SCHEME="${SCHEME:-rs-launch}"
RUN_SECS="${RUN_SECS:-25}"

# Default to the unsigned staging binary (the Phase-1 validation target).
CLIENT_BINARY="${CLIENT_BINARY:-/Users/robert/rs-re-staging/rs2client.948-5-mac}"

PATCHER_DYLIB="${PATCHER_DYLIB:-$PROJECT_DIR/client/launcher/patcher-mac/target/x86_64-apple-darwin/release/libdarkan_patcher.dylib}"
RECORDER_DYLIB="${RECORDER_DYLIB:-$PROJECT_DIR/client/launcher/recorder-mac/target/x86_64-apple-darwin/release/libdarkan_recorder.dylib}"

DARKAN_DIR="${DARKAN_DIR:-$HOME/.darkan3-mac}"
RECORD_DIR="${DARKAN_RECORD_DIR:-$DARKAN_DIR/recorder}"

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

CONFIG_URI="${CONFIG_URI:-${SCHEME}://${HOST}:${PORT}/jav_config.ws}"

[[ -x "$CLIENT_BINARY" ]] || { echo "ERROR: client not executable: $CLIENT_BINARY" >&2; exit 1; }
[[ -f "$RECORDER_DYLIB" ]] || { echo "ERROR: recorder dylib missing: $RECORDER_DYLIB (build it: client/launcher/recorder-mac/build-mac.sh)" >&2; exit 1; }
if [[ ! -f "$PATCHER_DYLIB" ]]; then
    echo "WARN: patcher dylib missing ($PATCHER_DYLIB) — running recorder WITHOUT the patcher." >&2
    echo "      Without the patcher the client connects to Jagex's key/host, not ours." >&2
    INSERT="$RECORDER_DYLIB"
else
    INSERT="$RECORDER_DYLIB:$PATCHER_DYLIB"
fi

mkdir -p "$DARKAN_DIR" "$RECORD_DIR"
[[ -f "$PROJECT_DIR/client/preferences.cfg" ]] && cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"
cd "$DARKAN_DIR"

echo "Launching macOS rs2client under recorder + patcher"
echo "  client   : $CLIENT_BINARY"
echo "  argv[1]  : $CONFIG_URI"
echo "  insert   : $INSERT"
echo "  record   : $RECORD_DIR/capture-<pid>.bin"
echo "  run secs : $RUN_SECS (0 = no auto-kill)"
echo "  patcher  : login-rsa=${DARKAN_RSA_MODULUS:+set} js5-rsa=${DARKAN_JS5_RSA_MODULUS:+set} http-port=$DARKAN_HTTP_PORT"
echo

# Direct-exec with env set. Run in background so we can auto-kill after the boot
# phase (the client has no auto-login; it will fetch config + JS5 then idle).
env \
    HOME="$DARKAN_DIR" \
    DARKAN_RECORD=1 \
    DARKAN_RECORD_DIR="$RECORD_DIR" \
    DYLD_INSERT_LIBRARIES="$INSERT" \
    DARKAN_RSA_MODULUS="$DARKAN_RSA_MODULUS" \
    DARKAN_JS5_RSA_MODULUS="$DARKAN_JS5_RSA_MODULUS" \
    DARKAN_HTTP_PORT="$DARKAN_HTTP_PORT" \
    "$CLIENT_BINARY" "$CONFIG_URI" &
CLIENT_PID=$!
echo "client pid=$CLIENT_PID"

if [[ "$RUN_SECS" -gt 0 ]]; then
    # Let the boot/JS5/lobby phase run, then terminate.
    sleep "$RUN_SECS"
    if kill -0 "$CLIENT_PID" 2>/dev/null; then
        echo "Auto-killing client pid=$CLIENT_PID after ${RUN_SECS}s"
        kill -TERM "$CLIENT_PID" 2>/dev/null || true
        sleep 1
        kill -KILL "$CLIENT_PID" 2>/dev/null || true
    fi
    wait "$CLIENT_PID" 2>/dev/null || true
    echo
    echo "Capture files:"
    ls -la "$RECORD_DIR"/capture-*.bin 2>/dev/null | tail -5 || echo "  (none — recorder may not have fired)"
else
    wait "$CLIENT_PID" 2>/dev/null || true
fi
