#!/usr/bin/env bash
# Launch the NXT client against the local Darkan server.
# Usage: ./run-client.sh
#
# Emulates the launcher's custom mode:
# - Sets working directory to ~/.darkan3
# - Creates preferences.cfg there
# - Applies LD_PRELOAD patches

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_URI="${CONFIG_URI:-http://localhost:8829/jav_config.ws}"
CLIENT_BINARY="$PROJECT_DIR/data/client/linux/rs3linux"
PATCHER_SO="$PROJECT_DIR/data/client/linux/libdarkan_patcher.so"
DARKAN_DIR="$HOME/.darkan3"

if [[ ! -f "$CLIENT_BINARY" ]]; then
    echo "ERROR: Client binary not found at $CLIENT_BINARY" >&2
    exit 1
fi

if [[ ! -f "$PATCHER_SO" ]]; then
    echo "ERROR: Patcher not found at $PATCHER_SO" >&2
    echo "Build it: cd client/launcher/patcher && cargo build --release" >&2
    exit 1
fi

# Export patcher env vars from .env
if [[ -f "$PROJECT_DIR/.env" ]]; then
    while IFS= read -r line; do
        [[ "$line" =~ ^[[:space:]]*# ]] && continue
        [[ -z "${line// }" ]] && continue
        export "$line"
    done < "$PROJECT_DIR/.env"
fi

# Set up ~/.darkan3 exactly like the launcher's custom mode
mkdir -p "$DARKAN_DIR"
cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"

# Clear stale cache data (NXT client creates Jagex/RuneScape/ under cache_folder)
# Disabled to allow cache accumulation across runs — re-enable if cache corruption suspected
#if [[ -d "$DARKAN_DIR/Jagex/RuneScape" ]]; then
#    echo "Clearing stale cache at $DARKAN_DIR/Jagex/RuneScape/"
#    rm -rf "$DARKAN_DIR/Jagex/RuneScape"
#fi

cd "$DARKAN_DIR"

env \
    HOME="$DARKAN_DIR" \
    LD_PRELOAD="$PATCHER_SO" \
    SDL_VIDEODRIVER=x11 \
    SDL_VIDEO_X11_WMCLASS=RuneScape \
    "$CLIENT_BINARY" --configURI "$CONFIG_URI" 2>&1 || true

EC=${PIPESTATUS[0]:-$?}
if [[ $EC -gt 128 ]]; then
    echo "Client killed by signal $((EC - 128)) ($(kill -l $((EC - 128)) 2>/dev/null || echo '?'))"
elif [[ $EC -ne 0 ]]; then
    echo "Client exited with code $EC"
fi
