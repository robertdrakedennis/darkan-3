#!/usr/bin/env bash
# Build + adhoc-sign the macOS packet recorder dylib (libdarkan_recorder.dylib).
#
# Mirrors client/launcher/patcher-mac/build-mac.sh: x86_64-apple-darwin release
# build, then an adhoc codesign so dyld will accept the dylib when it is inserted
# via DYLD_INSERT_LIBRARIES (an unsigned dylib is refused by the hardened
# runtime). The recorder is inserted ALONGSIDE the patcher (run-client-mac.sh
# prepends it to DYLD_INSERT_LIBRARIES when DARKAN_RECORD=1).
#
# Usage:  client/launcher/recorder-mac/build-mac.sh
#         (run from anywhere; all paths are derived from this script's location)

set -euo pipefail

LIB_NAME="libdarkan_recorder.dylib"
TARGET="x86_64-apple-darwin"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CRATE_DIR="$SCRIPT_DIR"
BUILT_DYLIB="$CRATE_DIR/target/$TARGET/release/$LIB_NAME"

red()   { printf '\033[31m%s\033[0m\n' "$*"; }
green() { printf '\033[32m%s\033[0m\n' "$*"; }
bold()  { printf '\033[1m%s\033[0m\n' "$*"; }
fail()  { red "ERROR: $*" >&2; exit 1; }

# --- Toolchain pre-flight -----------------------------------------------------
command -v cargo >/dev/null 2>&1 || fail "cargo not found in PATH"
if ! rustup target list --installed 2>/dev/null | grep -q "^${TARGET}$"; then
    bold "==> Installing rust target $TARGET"
    rustup target add "$TARGET" || fail "could not add target $TARGET"
fi

# --- Build --------------------------------------------------------------------
bold "==> Building $LIB_NAME ($TARGET, release)"
echo "    crate: $CRATE_DIR"
cargo build --release --target "$TARGET" --manifest-path "$CRATE_DIR/Cargo.toml"

[[ -f "$BUILT_DYLIB" ]] || fail "expected build artifact missing: $BUILT_DYLIB"

# --- Verify architecture ------------------------------------------------------
if ! file "$BUILT_DYLIB" | grep -q "x86_64"; then
    fail "built dylib is not x86_64: $(file "$BUILT_DYLIB")"
fi
green "    built OK: $BUILT_DYLIB"

# --- Adhoc sign ---------------------------------------------------------------
# dyld refuses to insert an unsigned dylib under the hardened runtime; an adhoc
# signature ( -s - ) is sufficient for DYLD_INSERT_LIBRARIES into our own client.
bold "==> Adhoc-signing $LIB_NAME"
codesign --force --sign - "$BUILT_DYLIB" || fail "codesign failed"
if codesign -v "$BUILT_DYLIB" >/dev/null 2>&1; then
    green "    signed OK (adhoc)"
else
    fail "codesign verification failed for $BUILT_DYLIB"
fi

# --- Summary ------------------------------------------------------------------
bold "==> Done."
echo "    artifact: $BUILT_DYLIB"
echo
echo "    Use it via run-client-mac.sh:"
echo "      DARKAN_RECORD=1 ./run-client-mac.sh"
echo "    (it prepends this dylib to DYLD_INSERT_LIBRARIES and sets DARKAN_RECORD_DIR)."
