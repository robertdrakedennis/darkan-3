#!/usr/bin/env bash
# Build libdarkan_patcher.dylib (macOS), adhoc-sign it, and deploy it to EVERY
# location the launcher's macOS find_patcher_library
# (client/launcher/src/game/process.rs) can resolve to.
#
# This is the macOS analogue of client/launcher/patcher/build.sh (Linux .so).
#
# Why this script exists (same rationale as the Linux build.sh):
#   The launcher loads the patcher dylib from the first of several candidate
#   locations that exists (next to the launcher exe, next to the client binary,
#   ~/darkan-3/macos/, the dev build dir). If ANY of those holds a stale copy it
#   shadows a fresh build. This script makes "build the patcher" atomically
#   refresh every deploy slot so a stale copy can never win.
#
# macOS specifics vs Linux:
#   * Target MUST be x86_64-apple-darwin. rs2client is x86_64 (it runs under
#     Rosetta on Apple Silicon); a translated process can only load an x86_64
#     dylib. We pass --target explicitly so this works on Apple Silicon hosts.
#   * The dylib must be ADHOC-SIGNED (`codesign -s -`) so dyld accepts it as an
#     inserted library. The target rs2client is unsigned/adhoc and has no
#     library-validation / hardened-runtime, so no re-signing of the target.
#
# Usage:  client/launcher/patcher-mac/build-mac.sh
#         (run from anywhere; all paths are derived, no cwd assumptions)
#
# All destination paths are $HOME-relative — no hardcoded /Users/<user> paths.

set -euo pipefail

LIB_NAME="libdarkan_patcher.dylib"
TARGET="x86_64-apple-darwin"

# --- Locate the patcher-mac crate (this script lives in it) -------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CRATE_DIR="$SCRIPT_DIR"
BUILT_DYLIB="$CRATE_DIR/target/$TARGET/release/$LIB_NAME"

# --- Source-of-truth RSA login-key markers ------------------------------------
# As with the Linux build, rustc lowers the byte-string literals into inline
# immediates, so a grep for the full 32-char literal returns 0 even on a current
# build. The FIRST 8 hex chars land in a single immediate and ARE contiguous, so
# we verify with the 8-char markers:
#   - CURRENT (948) build must contain CURRENT_MARK and must NOT contain OLD_MARK
#   - the full 32-char literal is checked against the SOURCE (authoritative).
CURRENT_PREFIX_FULL="aad4a7804c34bb788d52dbd5f70e5721"  # rev 948 login modulus prefix (in lib.rs)
CURRENT_MARK="aad4a780"                                  # first 8 chars — contiguous in the built dylib
OLD_MARK="8f389edb"                                      # older 947 login prefix start — contiguous in stale builds
SRC_LIB="$CRATE_DIR/src/lib.rs"

red()   { printf '\033[31m%s\033[0m\n' "$*"; }
green() { printf '\033[32m%s\033[0m\n' "$*"; }
bold()  { printf '\033[1m%s\033[0m\n' "$*"; }

fail() { red "ERROR: $*" >&2; exit 1; }

# --- Pre-flight: macOS only + toolchain --------------------------------------
[[ "$(uname -s)" == "Darwin" ]] || fail "build-mac.sh must run on macOS (uname is '$(uname -s)')."
command -v cargo >/dev/null   || fail "cargo not found on PATH."
command -v codesign >/dev/null || fail "codesign not found (Xcode command line tools required)."
if ! rustup target list --installed 2>/dev/null | grep -qx "$TARGET"; then
    fail "Rust target '$TARGET' is not installed. Install it: rustup target add $TARGET"
fi

# --- Pre-flight: confirm the SOURCE is the current build ----------------------
[[ -f "$SRC_LIB" ]] || fail "patcher source not found at $SRC_LIB"
if ! grep -q "$CURRENT_PREFIX_FULL" "$SRC_LIB"; then
    fail "$SRC_LIB does not contain the current 948 login prefix ($CURRENT_PREFIX_FULL).
       The source is not on the expected revision — refusing to build/deploy a stale patcher.
       (If the login key rotated again, update CURRENT_* in this script and lib.rs together.)"
fi
if grep -q "$OLD_MARK" "$SRC_LIB"; then
    fail "$SRC_LIB still references the OLD 947 login prefix ($OLD_MARK...).
       Update lib.rs to the current key before deploying."
fi

# --- Build --------------------------------------------------------------------
bold "==> Building $LIB_NAME (cargo build --release --target $TARGET)"
echo "    crate: $CRATE_DIR"
cargo build --release --target "$TARGET" --manifest-path "$CRATE_DIR/Cargo.toml"

[[ -f "$BUILT_DYLIB" ]] || fail "expected build artifact missing: $BUILT_DYLIB"

# --- Confirm architecture (must be x86_64 to load into the Rosetta client) ----
if ! file "$BUILT_DYLIB" | grep -q 'x86_64'; then
    fail "freshly built $BUILT_DYLIB is not x86_64 — it cannot load into the Rosetta rs2client.
       file says: $(file "$BUILT_DYLIB")"
fi

# --- Sanity-check the freshly built artifact before fanning it out ------------
if [[ "$(grep -ac "$CURRENT_MARK" "$BUILT_DYLIB" || true)" -lt 1 ]]; then
    fail "freshly built $BUILT_DYLIB does not contain current marker '$CURRENT_MARK' — build is wrong."
fi
if [[ "$(grep -ac "$OLD_MARK" "$BUILT_DYLIB" || true)" -ne 0 ]]; then
    fail "freshly built $BUILT_DYLIB still contains OLD marker '$OLD_MARK' — build is stale/wrong."
fi
green "    built OK (x86_64): $BUILT_DYLIB"

# --- Adhoc-sign the build artifact --------------------------------------------
# dyld will only honor DYLD_INSERT_LIBRARIES for a dylib it accepts. An adhoc
# signature (`-s -`) is sufficient for an unsigned/adhoc target without library
# validation. --force replaces any existing signature.
bold "==> Adhoc-signing $LIB_NAME"
codesign -s - --force "$BUILT_DYLIB"
codesign -dv "$BUILT_DYLIB" 2>&1 | sed 's/^/    /'
green "    adhoc-signed OK"

# --- Canonical deploy locations (every slot find_patcher_library can hit) ------
# Kept in lockstep with client/launcher/src/game/process.rs::find_patcher_library
# (macOS arm) and run-client-mac.sh. ALL $HOME-relative.
DEST_PATHS=(
    # Launcher data-dir root. macOS ProjectDirs("","","bolt-rs3").data_dir()
    # => ~/Library/Application Support/bolt-rs3. find_patcher_library hits this
    # as "next to client binary" in LIVE mode.
    "$HOME/Library/Application Support/bolt-rs3/$LIB_NAME"
    # Next to the downloaded client binary (rs2client) in the launcher data dir.
    "$HOME/Library/Application Support/bolt-rs3/Jagex/launcher/$LIB_NAME"
    # ~/darkan-3/macos: the macOS custom-mode runtime dir. The canonical client
    # slot is ~/darkan-3/macos/Jagex/launcher/rs2client, so deploy next to it
    # AND at the macos root (find_patcher_library checks both).
    "$HOME/darkan-3/macos/$LIB_NAME"
    "$HOME/darkan-3/macos/Jagex/launcher/$LIB_NAME"
    # Project tree copy (used by run-client-mac.sh when pointed at the repo).
    "$HOME/projects/darkan3-server/data/client/$LIB_NAME"
    # Next to the launcher executable itself (release + debug) — find_patcher_library's
    # first candidate, and the dev "../patcher-mac/target/.../release" candidate.
    "$HOME/projects/darkan3-server/client/launcher/target/release/$LIB_NAME"
    "$HOME/projects/darkan3-server/client/launcher/target/debug/$LIB_NAME"
)

# --- Deploy -------------------------------------------------------------------
# Copies preserve the adhoc signature (cp does not strip it). We re-verify each
# copy's signature below as belt-and-braces.
bold "==> Deploying to ${#DEST_PATHS[@]} locations"
for dest in "${DEST_PATHS[@]}"; do
    # Never clobber the build artifact with itself.
    if [[ "$dest" == "$BUILT_DYLIB" ]]; then
        echo "    skip (build artifact): $dest"
        continue
    fi
    mkdir -p "$(dirname "$dest")"
    cp -f "$BUILT_DYLIB" "$dest"
    chmod 0755 "$dest"
done

# --- Verify every deployed copy -----------------------------------------------
# Each deployed dylib MUST: be x86_64, embed the current 948 marker exactly once,
# NOT embed the old 947 marker, and carry a valid (adhoc) signature.
bold "==> Verifying deployed copies"
verify_failed=0
for dest in "${DEST_PATHS[@]}"; do
    if [[ ! -f "$dest" ]]; then
        red "    MISSING  $dest"
        verify_failed=1
        continue
    fi
    have_cur="$(grep -ac "$CURRENT_MARK" "$dest" || true)"
    have_old="$(grep -ac "$OLD_MARK" "$dest" || true)"
    is_x86=0; file "$dest" | grep -q 'x86_64' && is_x86=1
    sig_ok=0; codesign -v "$dest" >/dev/null 2>&1 && sig_ok=1
    if [[ "$have_cur" -ge 1 && "$have_old" -eq 0 && "$is_x86" -eq 1 && "$sig_ok" -eq 1 ]]; then
        green "    OK       $dest  (948=$have_cur, 947=$have_old, x86_64=$is_x86, signed=$sig_ok)"
    else
        red   "    BAD      $dest  (948=$have_cur expected>=1, 947=$have_old expected 0, x86_64=$is_x86 expected 1, signed=$sig_ok expected 1)"
        verify_failed=1
    fi
done

echo
if [[ "$verify_failed" -ne 0 ]]; then
    fail "one or more deployed patcher copies are missing/stale/unsigned/wrong-arch (see BAD/MISSING above)."
fi

# --- Summary ------------------------------------------------------------------
bold "==> Done. $LIB_NAME (rev 948, marker $CURRENT_MARK, x86_64, adhoc-signed) deployed to:"
for dest in "${DEST_PATHS[@]}"; do
    echo "    ${dest/#$HOME/\~}"
done
green "All ${#DEST_PATHS[@]} copies verified current. No stale 947 copies remain."
