#!/usr/bin/env bash
# Launch the macOS RuneScape wrapper/client — either against the local Darkan
# server (default) or, in PRODUCTION mode, against LIVE Jagex with the recorder
# inserted so a real production session can be captured.
#
# Usage:
#   ./run-client-mac.sh                 # local custom mode (patched -> Darkan)
#   ./run-client-mac.sh --production    # LIVE Jagex, pristine client + recorder
#   MAC_CLIENT_MODE=production ./run-client-mac.sh   # same as --production
#
# macOS analogue of run-client.sh (Linux). Local mode emulates the launcher's
# custom mode:
# - Sets working directory / HOME to the local mac client install root
# - Creates preferences.cfg there
# - Applies the DYLD_INSERT_LIBRARIES patcher (libdarkan_patcher.dylib)
# - Spawns RuneScape.app's executable directly (NOT via `open`/LaunchServices,
#   which strips DYLD_* env) with `--configURI`
#
# PRODUCTION mode (the live-Jagex capture path):
# - Forces DARKAN_RECORD=1 and inserts the recorder dylib.
# - The patcher stays inserted but is a HARD NO-OP: no RSA modulus, no proxy
#   mode, no HTTP-port patch are passed, so the on-disk pristine client is left
#   byte-for-byte and talks to LIVE Jagex.
# - Passes NO --configURI: the real RuneScape.app wrapper runs its normal Jagex
#   OAuth, downloads/launches rs2client, and connects to live servers.
# - Tags the capture as server_mode=production (DARKAN_RECORD_MODE=production).
#
# Env vars (override on the command line or via .env at repo root):
#   HOST                  server host           (default: localhost)   [local only]
#   PORT                  server port           (default: 8829)        [local only]
#   WRAPPER_BINARY        path to RuneScape app executable
#   CLIENT_BINARY         path to rs2client served by ConfigServer
#   DARKAN_RSA_MODULUS    login RSA modulus hex (forwarded to the patcher) [local only]
#   DARKAN_JS5_RSA_MODULUS JS5 RSA modulus hex  (forwarded to the patcher) [local only]
#   DARKAN_HTTP_PORT      HTTP port to patch in (P3)                       [local only]
#   DARKAN_WORLD_PORT     local world port; patcher redirects loopback :443 -> here
#                         (default: .env WORLD_PORT / EnvVars.worldPort)   [local only]
#   DARKAN_PROXY_MODE     "1" for proxy mode    (skips JS5 + HTTP-port patches) [local only]
#   MAC_CLIENT_MODE       wrapper | direct | production   (default: wrapper)
#   SCHEME                direct mode only: rs-launch | rs-launchs
#   DARKAN_RECORD         1 to insert the recorder (auto-forced in production)
#   DARKAN_RECORD_DIR     capture output dir (default: $DARKAN_DIR/recorder)

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# --- Flag parsing (only --production today; everything else is env-driven) ----
for arg in "$@"; do
    case "$arg" in
        --production|--prod|--live) MAC_CLIENT_MODE="production" ;;
        -h|--help)
            sed -n '2,40p' "$0"
            exit 0
            ;;
        *)
            echo "ERROR: unknown argument '$arg' (only --production is accepted)" >&2
            exit 1
            ;;
    esac
done

HOST="${HOST:-localhost}"
PORT="${PORT:-8829}"
SCHEME="${SCHEME:-rs-launch}"
MAC_CLIENT_MODE="${MAC_CLIENT_MODE:-wrapper}"

# Canonical mac slots.
WRAPPER_BINARY="${WRAPPER_BINARY:-/Users/robert/darkan-3/macos/RuneScape.app/Contents/MacOS/RuneScape}"
CLIENT_BINARY="${CLIENT_BINARY:-/Users/robert/darkan-3/macos/Jagex/launcher/rs2client}"

# The adhoc-signed x86_64 patcher dylib produced by patcher-mac/build-mac.sh.
PATCHER_DYLIB="${PATCHER_DYLIB:-$PROJECT_DIR/client/launcher/patcher-mac/target/x86_64-apple-darwin/release/libdarkan_patcher.dylib}"

if [[ -z "${DARKAN_DIR:-}" ]]; then
    if [[ "$MAC_CLIENT_MODE" == "direct" ]]; then
        DARKAN_DIR="$(cd "$(dirname "$CLIENT_BINARY")/../.." && pwd)"
    else
        # wrapper + production both spawn the RuneScape.app executable.
        DARKAN_DIR="$(cd "$(dirname "$WRAPPER_BINARY")/../../.." && pwd)"
    fi
fi

# Production mode is a recorded LIVE-Jagex session by definition: force recording.
if [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    DARKAN_RECORD=1
fi

# Optional recorder dylib — set DARKAN_RECORD=1 to also insert the socket recorder
# (alongside the patcher) and capture client I/O to $RECORD_DIR. Default off.
RECORDER_DYLIB="${RECORDER_DYLIB:-$PROJECT_DIR/client/launcher/recorder-mac/target/x86_64-apple-darwin/release/libdarkan_recorder.dylib}"
if [[ "${DARKAN_RECORD:-0}" == "1" ]]; then
    if [[ ! -f "$RECORDER_DYLIB" ]]; then
        echo "ERROR: DARKAN_RECORD=1 but recorder dylib not found at:" >&2
        echo "       $RECORDER_DYLIB" >&2
        echo "Build it: client/launcher/recorder-mac/build-mac.sh" >&2
        exit 1
    fi
    if ! codesign -v "$RECORDER_DYLIB" >/dev/null 2>&1; then
        echo "WARNING: $RECORDER_DYLIB is not validly signed; dyld may refuse to insert it." >&2
        echo "         Re-run recorder-mac/build-mac.sh (it adhoc-signs the dylib)." >&2
    fi
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
elif [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    # LIVE Jagex: launch the real wrapper with NO --configURI so it performs its
    # normal Jagex OAuth + client download/launch against live servers. We leave
    # CONFIG_URI empty on purpose (it must NOT be a localhost URI — the recorder
    # uses DARKAN_CONFIG_URI to classify the session, and empty => production).
    CONFIG_URI=""
    TARGET_BINARY="$WRAPPER_BINARY"
    TARGET_ARGS=()
else
    echo "ERROR: MAC_CLIENT_MODE must be 'wrapper', 'direct', or 'production' (got '$MAC_CLIENT_MODE')" >&2
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
# NOTE: in production we must NOT let a .env DARKAN_RSA_MODULUS leak to the
# patcher (it would redirect the pristine client off live Jagex), so we unset
# the patcher knobs again immediately after the load.
if [[ -f "$PROJECT_DIR/.env" ]]; then
    while IFS= read -r line; do
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        export "$line"
    done < "$PROJECT_DIR/.env"
fi

if [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    # Hard no-op the patcher: clear every redirect knob so the pristine on-disk
    # client talks to LIVE Jagex. The patcher dylib stays inserted but, with no
    # modulus set, its ctor returns immediately (verified in patcher-mac/lib.rs).
    unset DARKAN_RSA_MODULUS DARKAN_JS5_RSA_MODULUS DARKAN_HTTP_PORT DARKAN_WORLD_PORT DARKAN_PROXY_MODE
else
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

    # Decimal-int counterpart of modulus_hex: resolve an integer EnvVars default
    # (e.g. worldPort) when the env var is unset. Used for DARKAN_WORLD_PORT.
    int_default() {
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
        r'\s*:\s*Int\s*=\s*dotenv\.get\("[^"]+",\s*"([0-9]+)"\)',
        text,
    )
    if not match:
        raise SystemExit(f"missing default for {kt_name} in {path}")
    value = match.group(1)
print(int(value, 10))
PY
    }

    DARKAN_RSA_MODULUS="${DARKAN_RSA_MODULUS:-$(modulus_hex RSA_LOGIN_MODULUS loginRsaModulus)}"
    DARKAN_JS5_RSA_MODULUS="${DARKAN_JS5_RSA_MODULUS:-$(modulus_hex RSA_JS5_MODULUS js5RsaModulus)}"
    DARKAN_HTTP_PORT="${DARKAN_HTTP_PORT:-${CONFIG_HTTP_PORT:-$PORT}}"
    # World-server port the patcher redirects the client's loopback :443 world
    # connect to. Mirrors DARKAN_HTTP_PORT plumbing: prefer an explicit env /
    # .env WORLD_PORT, else fall back to EnvVars.worldPort's Kotlin default.
    DARKAN_WORLD_PORT="${DARKAN_WORLD_PORT:-$(int_default WORLD_PORT worldPort)}"
fi

# Set up the mac data dir. In LOCAL mode we override HOME to the darkan install
# root and drop our preferences.cfg there (the launcher's custom mode). In
# PRODUCTION we leave the user's real HOME untouched so the wrapper's normal
# Jagex data dir (~/Jagex) is used for OAuth + client download.
if [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    RUN_HOME="$HOME"
    mkdir -p "$DARKAN_DIR"
else
    RUN_HOME="$DARKAN_DIR"
    mkdir -p "$DARKAN_DIR"
    if [[ -f "$PROJECT_DIR/client/preferences.cfg" ]]; then
        cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"
    fi
fi

cd "$DARKAN_DIR"

# The recorder classifies the session from DARKAN_RECORD_MODE (explicit) and
# DARKAN_CONFIG_URI (inference). production => empty configURI; local => the URI
# we hand the client.
if [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    DARKAN_RECORD_MODE="production"
else
    DARKAN_RECORD_MODE="local"
fi
DARKAN_CONFIG_URI="$CONFIG_URI"

echo "Launching macOS rs2client"
echo "  mode   : $MAC_CLIENT_MODE (record_mode=$DARKAN_RECORD_MODE)"
echo "  target : $TARGET_BINARY"
echo "  args   : ${TARGET_ARGS[*]:-<none>}"
echo "  insert : $INSERT_LIBS"
echo "  HOME   : $RUN_HOME"
echo "  record : DARKAN_RECORD=${DARKAN_RECORD:-0} DARKAN_RECORD_DIR=${RECORD_DIR:-<unset>}"
echo "  patcher env: DARKAN_RSA_MODULUS=${DARKAN_RSA_MODULUS:+<set>}${DARKAN_RSA_MODULUS:-<unset>} \
DARKAN_JS5_RSA_MODULUS=${DARKAN_JS5_RSA_MODULUS:+<set>}${DARKAN_JS5_RSA_MODULUS:-<unset>} \
DARKAN_HTTP_PORT=${DARKAN_HTTP_PORT:-<unset>} DARKAN_WORLD_PORT=${DARKAN_WORLD_PORT:-<unset>} \
DARKAN_PROXY_MODE=${DARKAN_PROXY_MODE:-<unset>}"
if [[ "$MAC_CLIENT_MODE" == "production" ]]; then
    echo "  NOTE   : LIVE Jagex — patcher is a no-op, no --configURI, recorder capturing."
fi

# Spawn directly with the env set. Do NOT use `open` — LaunchServices strips
# DYLD_* env vars, so neither the recorder nor the patcher would load.
#
# We pass through the DARKAN_* patcher vars only if they are set, so an unset
# modulus leaves the patcher a no-op (live mode), matching the launcher's gating.
#
# We deliberately DO NOT set DYLD_FORCE_FLAT_NAMESPACE here. The recorder's
# connect()/close() taps use the __DATA,__interpose table, which dyld honours
# under the client's two-level namespace WITHOUT a forced flat namespace; and
# both dylibs' ctors fire reliably without it (the local flow proves this). In
# production the host is the real RuneScape.app wrapper (its own TLS/networking),
# and forcing a flat namespace on it risks symbol collisions that could break
# its normal OAuth/download — so we leave the namespace alone.
#
# `${TARGET_ARGS[@]:-}` guards the empty-array case (production passes no args)
# under `set -u` on bash 3.2 (macOS system bash).
set +e
env \
    HOME="$RUN_HOME" \
    DYLD_INSERT_LIBRARIES="$INSERT_LIBS" \
    ${DARKAN_RECORD:+DARKAN_RECORD="$DARKAN_RECORD"} \
    ${RECORD_DIR:+DARKAN_RECORD_DIR="$RECORD_DIR"} \
    DARKAN_RECORD_MODE="$DARKAN_RECORD_MODE" \
    ${DARKAN_CONFIG_URI:+DARKAN_CONFIG_URI="$DARKAN_CONFIG_URI"} \
    ${DARKAN_RSA_MODULUS:+DARKAN_RSA_MODULUS="$DARKAN_RSA_MODULUS"} \
    ${DARKAN_JS5_RSA_MODULUS:+DARKAN_JS5_RSA_MODULUS="$DARKAN_JS5_RSA_MODULUS"} \
    ${DARKAN_HTTP_PORT:+DARKAN_HTTP_PORT="$DARKAN_HTTP_PORT"} \
    ${DARKAN_WORLD_PORT:+DARKAN_WORLD_PORT="$DARKAN_WORLD_PORT"} \
    ${DARKAN_PROXY_MODE:+DARKAN_PROXY_MODE="$DARKAN_PROXY_MODE"} \
    "$TARGET_BINARY" ${TARGET_ARGS[@]:+"${TARGET_ARGS[@]}"} 2>&1
EC=$?
set -e

if [[ $EC -gt 128 ]]; then
    echo "Client killed by signal $((EC - 128))"
elif [[ $EC -ne 0 ]]; then
    echo "Client exited with code $EC"
fi
