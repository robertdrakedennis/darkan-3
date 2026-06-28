#!/usr/bin/env bash
# Build + adhoc-sign the macOS RSA patcher dylib (libdarkan_patcher.dylib).
#
# x86_64-apple-darwin release build, then an adhoc codesign so dyld will accept
# the dylib when it is inserted via DYLD_INSERT_LIBRARIES (the hardened runtime
# refuses an unsigned dylib). run-client-mac.sh expects this script to produce
# the signed dylib at target/x86_64-apple-darwin/release/libdarkan_patcher.dylib.
#
# Usage:  client/launcher/patcher-mac/build-mac.sh
#         (run from anywhere; all paths are derived from this script's location)

set -euo pipefail

LIB_NAME="libdarkan_patcher.dylib"
TARGET="x86_64-apple-darwin"

# Source-of-truth RSA login-key marker — the current build's login modulus
# prefix MUST be present in the source (guards against a stale-revision build).
CURRENT_PREFIX_FULL="aad4a7804c34bb788d52dbd5f70e5721"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CRATE_DIR="$SCRIPT_DIR"
SRC_LIB="$CRATE_DIR/src/lib.rs"
BUILT_DYLIB="$CRATE_DIR/target/$TARGET/release/$LIB_NAME"

red()   { printf '\033[31m%s\033[0m\n' "$*"; }
green() { printf '\033[32m%s\033[0m\n' "$*"; }
bold()  { printf '\033[1m%s\033[0m\n' "$*"; }
fail()  { red "ERROR: $*" >&2; exit 1; }

# --- Pre-flight ---------------------------------------------------------------
command -v cargo >/dev/null 2>&1 || fail "cargo not found in PATH"
[[ -f "$SRC_LIB" ]] || fail "patcher source not found at $SRC_LIB"
if ! grep -q "$CURRENT_PREFIX_FULL" "$SRC_LIB"; then
    fail "$SRC_LIB does not contain the current login modulus prefix ($CURRENT_PREFIX_FULL).
       The source is not on the expected revision — refusing to build a stale patcher."
fi
if ! rustup target list --installed 2>/dev/null | grep -q "^${TARGET}$"; then
    bold "==> Installing rust target $TARGET"
    rustup target add "$TARGET" || fail "could not add target $TARGET"
fi

# --- Build --------------------------------------------------------------------
bold "==> Building $LIB_NAME ($TARGET, release)"
echo "    crate: $CRATE_DIR"
cargo build --release --target "$TARGET" --manifest-path "$CRATE_DIR/Cargo.toml"

[[ -f "$BUILT_DYLIB" ]] || fail "expected build artifact missing: $BUILT_DYLIB"
if ! file "$BUILT_DYLIB" | grep -q "x86_64"; then
    fail "built dylib is not x86_64: $(file "$BUILT_DYLIB")"
fi
green "    built OK: $BUILT_DYLIB"

# --- Adhoc sign ---------------------------------------------------------------
bold "==> Adhoc-signing $LIB_NAME"
codesign --force --sign - "$BUILT_DYLIB" || fail "codesign failed"
codesign -v "$BUILT_DYLIB" >/dev/null 2>&1 || fail "codesign verification failed"
green "    signed OK (adhoc)"

bold "==> Done."
echo "    artifact: $BUILT_DYLIB"
