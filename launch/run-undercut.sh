#!/usr/bin/env bash
#
# darkan-3-undercut — ALL-IN-ONE LAUNCHER
# ========================================
# One entry point that drives BOTH mechanisms against the same rs2client process:
#   1. Darkan-3 patch + launch  — LD_PRELOAD libdarkan_patcher.so, point the client at the
#                                 local Darkan-3 lobby/world (RSA + server-URL patch).
#   2. Undercut engine inject   — GDB-dlopen libundercutbootstrap.so once the client is up
#                                 (funchook hooks, ImGui overlay, in-process MCP :7882, and the
#                                 TcpIn network sniffer — which REPLACES the deprecated proxy).
#
# Usage:
#   launch/run-undercut.sh                 # patch + launch + inject  (default)
#   launch/run-undercut.sh --no-engine     # patch + launch only (server testing, no injection)
#   launch/run-undercut.sh --no-patch      # inject into an already-running rs2client only
#
# Env knobs:
#   INJECT_DELAY=<sec>   seconds to wait after the client process appears before injecting (default 8)
#   CONFIG_URI=<url>     passed through to run-client.sh
#
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ENGINE_DIR="$PROJECT_DIR/engine"
ENGINE_SO="$ENGINE_DIR/build/libs/libundercutbootstrap.so"
INJECT_DELAY="${INJECT_DELAY:-8}"

DO_PATCH=1
DO_ENGINE=1
for arg in "$@"; do
    case "$arg" in
        --no-patch)  DO_PATCH=0 ;;
        --no-engine) DO_ENGINE=0 ;;
        -h|--help)   sed -n '2,20p' "$0"; exit 0 ;;
        *) echo "Unknown option: $arg" >&2; exit 2 ;;
    esac
done

log() { echo -e "\033[1;36m[undercut-launch]\033[0m $*"; }
err() { echo -e "\033[1;31m[undercut-launch] ERROR:\033[0m $*" >&2; }

# --- locate a live rs2client pid that is NOT already injected -----------------------------------
find_client_pid() {
    local newest_pid="" newest_start=0
    for pid_dir in /proc/[0-9]*; do
        local pid; pid=$(basename "$pid_dir")
        grep -qa 'rs2client' "$pid_dir/cmdline" 2>/dev/null || continue
        [[ -r "/proc/$pid/maps" ]] || continue
        # skip if our bootstrap is already mapped in
        grep -qa 'libundercutbootstrap.so' "/proc/$pid/maps" 2>/dev/null && continue
        local start; start=$(stat -c %Y "/proc/$pid" 2>/dev/null || echo 0)
        if (( start >= newest_start )); then newest_start=$start; newest_pid=$pid; fi
    done
    [[ -n "$newest_pid" ]] && echo "$newest_pid"
}

inject_engine() {
    local pid="$1"
    if [[ ! -f "$ENGINE_SO" ]]; then
        err "engine bootstrap not built: $ENGINE_SO"
        err "build it first:  ./gradlew :engine:buildNativeBootstrap   (and ./gradlew :engine:build for the jar)"
        return 1
    fi
    [[ -d "/proc/$pid" ]] || { err "process $pid is gone"; return 1; }

    : "${JAVA_HOME:?JAVA_HOME must be set to a JDK 25 home for engine injection}"
    export UNDERCUT_HOME_DIR="${UNDERCUT_HOME_DIR:-$(realpath "$ENGINE_DIR/build/libs")}"

    log "injecting $ENGINE_SO into rs2client pid $pid (UNDERCUT_HOME_DIR=$UNDERCUT_HOME_DIR)"
    # Lift RLIMIT_CORE so a JVM crash actually dumps a core (best effort).
    sudo prlimit --pid "$pid" --core=unlimited:unlimited 2>/dev/null \
        || log "warn: prlimit failed; no core dump on crash"
    sudo gdb -p "$pid" -batch \
        -ex "call (int) setenv(\"JAVA_HOME\", \"$JAVA_HOME\", 1)" \
        -ex "call (int) setenv(\"UNDERCUT_HOME_DIR\", \"$UNDERCUT_HOME_DIR\", 1)" \
        -ex "call (void*) dlopen(\"$ENGINE_SO\", 4362)" \
        -ex "call (char*) dlerror()" \
        -ex detach -ex quit
}

# --- 1. patch + launch the client (background) --------------------------------------------------
CLIENT_BG_PID=""
if (( DO_PATCH )); then
    log "patch + launch via run-client.sh (LD_PRELOAD darkan patcher)"
    ( cd "$PROJECT_DIR" && ./run-client.sh ) &
    CLIENT_BG_PID=$!
else
    log "--no-patch: skipping launch; will inject into an already-running rs2client"
fi

# --- 2. wait for the client, then inject the engine ---------------------------------------------
if (( DO_ENGINE )); then
    log "waiting for rs2client process..."
    pid=""
    for _ in $(seq 1 120); do
        pid="$(find_client_pid || true)"
        [[ -n "$pid" ]] && break
        # if we launched the client and it already died, bail
        if (( DO_PATCH )) && ! kill -0 "$CLIENT_BG_PID" 2>/dev/null && [[ -z "$pid" ]]; then
            err "client process exited before rs2client appeared"; exit 1
        fi
        sleep 1
    done
    [[ -z "$pid" ]] && { err "timed out waiting for rs2client"; exit 1; }

    log "rs2client up (pid $pid); waiting ${INJECT_DELAY}s for it to initialize before injecting"
    sleep "$INJECT_DELAY"
    inject_engine "$pid" && log "engine injected — overlay + MCP (:7882) + TcpIn sniffer active" \
        || { err "engine injection failed"; }
else
    log "--no-engine: client launched without engine injection"
fi

# Keep the foreground tied to the client when we launched it.
if [[ -n "$CLIENT_BG_PID" ]]; then
    wait "$CLIENT_BG_PID" 2>/dev/null || true
fi
