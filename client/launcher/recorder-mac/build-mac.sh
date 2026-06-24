#!/usr/bin/env bash
# Build libdarkan_recorder.dylib (macOS), adhoc-sign it, and deploy it next to
# the client / to a known slot so it can be injected via DYLD_INSERT_LIBRARIES.
#
# SIBLING of client/launcher/patcher-mac/build-mac.sh. The recorder is an
# observe-only tap (interposes + one inline seed hook), so unlike the patcher it
# has NO RSA-key markers to verify (review B10: "the recorder's build script
# should NOT blindly copy the RSA-marker preflight"). What we DO verify is the
# same load-bearing invariants: x86_64 arch, valid adhoc signature, and — unique
# to the recorder — which Mach-O section the toolchain actually emitted the
# interpose table into (the review flagged that modern clang uses
# __DATA_CONST,__interpose, so we print it rather than assume).
#
# macOS specifics (same as the patcher):
#   * Target MUST be x86_64-apple-darwin — rs2client is x86_64 under Rosetta; a
#     translated process can only load an x86_64 dylib.
#   * Adhoc-sign (`codesign -s -`) so dyld accepts the inserted dylib. The target
#     rs2client is unsigned/adhoc with no library-validation / hardened-runtime.
#
# Usage:  client/launcher/recorder-mac/build-mac.sh
#         (run from anywhere; all paths derived, no cwd assumptions)

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

# --- Pre-flight: macOS only + toolchain --------------------------------------
[[ "$(uname -s)" == "Darwin" ]] || fail "build-mac.sh must run on macOS (uname is '$(uname -s)')."
command -v cargo    >/dev/null || fail "cargo not found on PATH."
command -v codesign >/dev/null || fail "codesign not found (Xcode command line tools required)."
command -v otool    >/dev/null || fail "otool not found (Xcode command line tools required)."
if ! rustup target list --installed 2>/dev/null | grep -qx "$TARGET"; then
    fail "Rust target '$TARGET' is not installed. Install it: rustup target add $TARGET"
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
green "    built OK (x86_64): $BUILT_DYLIB"

# --- Report which section the interpose table landed in (review B6/B8 note) ---
# Modern clang/rustc may emit __DATA_CONST,__interpose instead of the classic
# __DATA,__interpose. dyld honors both for an INSERTED dylib. We just print what
# we got so the recorder's behavior is documented, not assumed.
bold "==> Interpose section (Mach-O):"
INTERPOSE_SECT="$(otool -l "$BUILT_DYLIB" | awk '
    /sectname __interpose/ {have=1}
    have && /segname/ {print "    " $0; have=0}
')"
if [[ -n "$INTERPOSE_SECT" ]]; then
    echo "$INTERPOSE_SECT"
    # Show the full section line(s) too for the report.
    otool -l "$BUILT_DYLIB" | grep -A1 'sectname __interpose' | sed 's/^/    /'
else
    red  "    WARNING: no __interpose section found — interposes will NOT fire!"
    red  "    (Check the #[link_section] attribute in src/interpose.rs.)"
fi

# --- Adhoc-sign the build artifact --------------------------------------------
bold "==> Adhoc-signing $LIB_NAME"
codesign -s - --force "$BUILT_DYLIB"
codesign -dv "$BUILT_DYLIB" 2>&1 | sed 's/^/    /'
green "    adhoc-signed OK"

# --- Deploy locations ---------------------------------------------------------
# The recorder rides alongside the patcher, so deploy to the same kinds of slots
# the patcher uses + a recorder-specific dev slot. All $HOME-relative.
DEST_PATHS=(
    "$HOME/Library/Application Support/bolt-rs3/$LIB_NAME"
    "$HOME/Library/Application Support/bolt-rs3/Jagex/launcher/$LIB_NAME"
    "$HOME/darkan-3/macos/$LIB_NAME"
    "$HOME/darkan-3/macos/Jagex/launcher/$LIB_NAME"
    "$HOME/projects/darkan3-server/data/client/$LIB_NAME"
)

bold "==> Deploying to ${#DEST_PATHS[@]} locations"
for dest in "${DEST_PATHS[@]}"; do
    if [[ "$dest" == "$BUILT_DYLIB" ]]; then
        echo "    skip (build artifact): $dest"
        continue
    fi
    mkdir -p "$(dirname "$dest")"
    cp -f "$BUILT_DYLIB" "$dest"
    chmod 0755 "$dest"
done

# --- Verify every deployed copy (x86_64 + valid adhoc signature) --------------
bold "==> Verifying deployed copies"
verify_failed=0
for dest in "${DEST_PATHS[@]}"; do
    if [[ ! -f "$dest" ]]; then
        red "    MISSING  $dest"; verify_failed=1; continue
    fi
    is_x86=0; file "$dest" | grep -q 'x86_64' && is_x86=1
    sig_ok=0; codesign -v "$dest" >/dev/null 2>&1 && sig_ok=1
    if [[ "$is_x86" -eq 1 && "$sig_ok" -eq 1 ]]; then
        green "    OK       $dest  (x86_64=$is_x86, signed=$sig_ok)"
    else
        red   "    BAD      $dest  (x86_64=$is_x86 expected 1, signed=$sig_ok expected 1)"; verify_failed=1
    fi
done

echo
[[ "$verify_failed" -eq 0 ]] || fail "one or more deployed recorder copies are missing/unsigned/wrong-arch."

bold "==> Done. $LIB_NAME (x86_64, adhoc-signed) deployed to:"
for dest in "${DEST_PATHS[@]}"; do echo "    ${dest/#$HOME/\~}"; done
green "All ${#DEST_PATHS[@]} copies verified."
echo
bold "Run alongside the client with DARKAN_RECORD=1, e.g.:"
echo "    DARKAN_RECORD=1 \\"
echo "    DYLD_INSERT_LIBRARIES=$BUILT_DYLIB \\"
echo "    <rs2client> 'rs-launch://localhost:8829/jav_config.ws'"
