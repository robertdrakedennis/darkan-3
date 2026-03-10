#!/usr/bin/env bash
# Launch the NXT client against the local Darkan server.
# Usage: ./run-client.sh
#
# Reads patcher env vars from .env, applies LD_PRELOAD patches, and
# launches rs3linux pointing at the local config server.

set -euo pipefail
cd "$(dirname "$0")"

CONFIG_URI="${CONFIG_URI:-http://localhost:8829/jav_config.ws}"
CLIENT_BINARY="./data/client/rs3linux"
PATCHER_SO="./libdarkan_patcher.so"

if [[ ! -f "$CLIENT_BINARY" ]]; then
    echo "ERROR: Client binary not found at $CLIENT_BINARY" >&2
    exit 1
fi

if [[ ! -f "$PATCHER_SO" ]]; then
    echo "ERROR: Patcher not found at $PATCHER_SO" >&2
    echo "Build it: cd client/launcher/patcher && cargo build --release && cp target/release/libdarkan_patcher.so ../../.." >&2
    exit 1
fi

# Export patcher env vars from .env
if [[ -f .env ]]; then
    while IFS= read -r line; do
        # Skip comments and blank lines
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        export "$line"
    done < .env
fi

exec env LD_PRELOAD="$(realpath "$PATCHER_SO")" "$CLIENT_BINARY" --configURI "$CONFIG_URI"
