#!/usr/bin/env bash
# Launch the NXT client under strace to capture network activity.
# Traces: connect(), sendto(), recvfrom(), write(), read() on sockets
# Output: ./strace-client.log (network syscalls only)
#
# Usage: ./run-client-strace.sh

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_URI="${CONFIG_URI:-http://localhost:8829/jav_config.ws}"
CLIENT_BINARY="$PROJECT_DIR/data/client/linux/rs3linux"
PATCHER_SO="$PROJECT_DIR/data/client/linux/libdarkan_patcher.so"
DARKAN_DIR="$HOME/.darkan3"
STRACE_LOG="$PROJECT_DIR/strace-client.log"

if [[ ! -f "$CLIENT_BINARY" ]]; then
    echo "ERROR: Client binary not found at $CLIENT_BINARY" >&2
    exit 1
fi

if [[ ! -f "$PATCHER_SO" ]]; then
    echo "ERROR: Patcher not found at $PATCHER_SO" >&2
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

mkdir -p "$DARKAN_DIR"
cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"

if [[ -d "$DARKAN_DIR/Jagex/RuneScape" ]]; then
    echo "Clearing stale cache at $DARKAN_DIR/Jagex/RuneScape/"
    rm -rf "$DARKAN_DIR/Jagex/RuneScape"
fi

cd "$DARKAN_DIR"

echo "Tracing network syscalls to: $STRACE_LOG"
echo "Launch the lobby server first: ./gradlew :lobby:run"
echo ""

# -f: follow forks (rs3linux launches rs2client)
# -e trace=network,write,read: capture network + I/O syscalls
# -e signal=all: show signals (crash detection)
# -s 512: show first 512 bytes of data
# -tt: timestamps with microseconds
strace -f -tt -s 512 \
    -e trace=connect,sendto,recvfrom,socket,close,write,read \
    -e signal=all \
    -o "$STRACE_LOG" \
    env \
        HOME="$DARKAN_DIR" \
        LD_PRELOAD="$PATCHER_SO" \
        SDL_VIDEODRIVER=x11 \
        SDL_VIDEO_X11_WMCLASS=RuneScape \
        "$CLIENT_BINARY" --configURI "$CONFIG_URI" 2>&1 || true

EC=${PIPESTATUS[0]:-$?}
echo ""
echo "=== Exit code: $EC ==="
if [[ $EC -gt 128 ]]; then
    echo "Client killed by signal $((EC - 128)) ($(kill -l $((EC - 128)) 2>/dev/null || echo '?'))"
fi

echo ""
echo "=== Network connections (connect calls): ==="
grep -a 'connect(' "$STRACE_LOG" | grep -v 'UNIX\|/var/run\|/tmp' | tail -30 || echo "(none)"

echo ""
echo "=== HTTP-related writes (port 8829): ==="
grep -a -A1 'GET /ms\|POST /ms\|GET /jav_config' "$STRACE_LOG" | tail -20 || echo "(none found)"

echo ""
echo "=== Signals received: ==="
grep -a '--- SIG' "$STRACE_LOG" | tail -10 || echo "(none)"

echo ""
echo "Full trace: $STRACE_LOG"
echo "Analyze with: grep 'connect.*127.0.0.1' $STRACE_LOG"
