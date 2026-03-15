#!/usr/bin/env bash
# Launch the NXT client under GDB to capture crash details.
# We attach GDB directly to rs2client after it's launched by rs3linux.
# Usage: ./run-client-gdb.sh

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
CONFIG_URI="${CONFIG_URI:-http://localhost:8829/jav_config.ws}"
CLIENT_BINARY="$PROJECT_DIR/data/client/rs3linux"
PATCHER_SO="$PROJECT_DIR/client/launcher/patcher/target/release/libdarkan_patcher.so"
DARKAN_DIR="$HOME/.darkan3"

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

# Set up ~/.darkan3
mkdir -p "$DARKAN_DIR"
cp -f "$PROJECT_DIR/client/preferences.cfg" "$DARKAN_DIR/preferences.cfg"

# Clear stale cache data
if [[ -d "$DARKAN_DIR/Jagex/RuneScape" ]]; then
    echo "Clearing stale cache at $DARKAN_DIR/Jagex/RuneScape/"
    rm -rf "$DARKAN_DIR/Jagex/RuneScape"
fi

# Launch the client normally in the background
cd "$DARKAN_DIR"
env \
    HOME="$DARKAN_DIR" \
    LD_PRELOAD="$PATCHER_SO" \
    SDL_VIDEODRIVER=x11 \
    SDL_VIDEO_X11_WMCLASS=RuneScape \
    "$CLIENT_BINARY" --configURI "$CONFIG_URI" &>/dev/null &
LAUNCHER_PID=$!
echo "Launcher PID: $LAUNCHER_PID"

# Wait for rs2client to appear
echo "Waiting for rs2client process..."
RS2CLIENT_PID=""
for i in $(seq 1 30); do
    RS2CLIENT_PID=$(pgrep -f "rs2client" 2>/dev/null | head -1 || true)
    if [[ -n "$RS2CLIENT_PID" ]]; then
        break
    fi
    sleep 1
done

if [[ -z "$RS2CLIENT_PID" ]]; then
    echo "ERROR: rs2client process not found after 30 seconds" >&2
    kill $LAUNCHER_PID 2>/dev/null || true
    exit 1
fi

echo "Found rs2client PID: $RS2CLIENT_PID"
# Give it a moment to initialize
sleep 2

GDB_COMMANDS=$(mktemp /tmp/gdb_cmds_XXXXXX)
cat > "$GDB_COMMANDS" <<'GDBEOF'
set pagination off
set confirm off
set print thread-events off

handle SIGSEGV stop print
handle SIGPIPE nostop noprint

continue

# When it stops on SIGSEGV:
echo \n=== CRASH THREAD ===\n
thread
echo \n=== CRASH REGISTERS ===\n
info registers
echo \n=== CRASH BACKTRACE ===\n
bt 30
echo \n=== CRASH INSTRUCTION ===\n
x/10i $rip-10
echo \n=== STACK AROUND RSP ===\n
x/16gx $rsp
echo \n=== RBP FRAME ===\n
x/16gx $rbp

echo \n=== DONE ===\n
quit
GDBEOF

echo "Attaching GDB to rs2client (PID $RS2CLIENT_PID)..."
gdb -batch -x "$GDB_COMMANDS" -p "$RS2CLIENT_PID" 2>&1 | tee /tmp/darkan_gdb_output.txt || true

echo "GDB output saved to /tmp/darkan_gdb_output.txt"
rm -f "$GDB_COMMANDS"

# Clean up
kill $LAUNCHER_PID 2>/dev/null || true
kill $RS2CLIENT_PID 2>/dev/null || true
