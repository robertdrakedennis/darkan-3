#!/bin/bash
# Bundles the latest crash artifacts into one tarball for easy attachment to
# bug reports / further analysis. Run after a SIGSEGV.
#
# Inputs (auto-detected):
#   - ~/.undercut/logs/hs_err_pid*.log           (HotSpot fatal-error log)
#   - ~/.undercut/logs/last-crash.txt            (our pre-JVM SIGSEGV handler output)
#   - ~/.undercut/logs/undercut-*.log            (latest engine log)
#   - /tmp/undercut_log_<pid>.txt                (native bootstrap stdout)
#   - /tmp/undercut_stderr_<pid>.txt             (native bootstrap stderr)
#   - latest coredumpctl entry for rs2client      (gdb info threads/bt output)
#
# Output: ~/.undercut/crashes/<timestamp>.tar.gz

set -euo pipefail

OUT_DIR="${HOME}/.undercut/crashes"
LOGS_DIR="${HOME}/.undercut/logs"
TS="$(date +%Y-%m-%d_%H-%M-%S)"
STAGE="$(mktemp -d -t undercut-crash-XXXXXX)"
trap 'rm -rf "${STAGE}"' EXIT

mkdir -p "${OUT_DIR}"

# 1. Latest hs_err_pidXXXX.log (most recent by mtime)
if compgen -G "${LOGS_DIR}/hs_err_pid*.log" > /dev/null; then
    cp "$(ls -t "${LOGS_DIR}"/hs_err_pid*.log | head -1)" "${STAGE}/hs_err.log"
else
    echo "(no hs_err_pid*.log found in ${LOGS_DIR})" > "${STAGE}/hs_err.log"
fi

# 2. Our pre-JVM SIGSEGV handler output
if [[ -f "${LOGS_DIR}/last-crash.txt" ]]; then
    cp "${LOGS_DIR}/last-crash.txt" "${STAGE}/last-crash.txt"
else
    echo "(no last-crash.txt — pre-JVM handler may not have written one)" > "${STAGE}/last-crash.txt"
fi

# 3. Latest engine log
if compgen -G "${LOGS_DIR}/undercut-*.log" > /dev/null; then
    cp "$(ls -t "${LOGS_DIR}"/undercut-*.log | head -1)" "${STAGE}/undercut.log"
fi

# 4. Native bootstrap stdout/stderr — pick the most recent matching PID from the
#    hs_err file (if any), else just the newest /tmp/undercut_log_*.txt.
PID_FROM_HSERR=""
if [[ -f "${STAGE}/hs_err.log" ]]; then
    PID_FROM_HSERR="$(grep -oP 'Process pid \K[0-9]+' "${STAGE}/hs_err.log" | head -1 || true)"
fi
if [[ -z "${PID_FROM_HSERR}" ]]; then
    PID_FROM_HSERR="$(ls -t /tmp/undercut_log_*.txt 2>/dev/null | head -1 | grep -oP '_\K[0-9]+(?=\.txt)' || true)"
fi
if [[ -n "${PID_FROM_HSERR}" ]]; then
    [[ -f "/tmp/undercut_log_${PID_FROM_HSERR}.txt"    ]] && cp "/tmp/undercut_log_${PID_FROM_HSERR}.txt"    "${STAGE}/bootstrap_stdout.txt"
    [[ -f "/tmp/undercut_stderr_${PID_FROM_HSERR}.txt" ]] && cp "/tmp/undercut_stderr_${PID_FROM_HSERR}.txt" "${STAGE}/bootstrap_stderr.txt"
    echo "${PID_FROM_HSERR}" > "${STAGE}/crashed_pid.txt"
fi

# 5. coredumpctl backtrace (latest SIGSEGV in rs2client)
{
    echo "=== coredumpctl list (rs2client, last 5) ==="
    coredumpctl list --no-pager 2>&1 | grep rs2client | tail -5 || true
    echo
    LATEST_PID="$(coredumpctl list --no-pager 2>&1 | grep rs2client | tail -1 | awk '{print $5}')"
    if [[ -n "${LATEST_PID}" ]]; then
        echo "=== coredumpctl info ${LATEST_PID} ==="
        coredumpctl info "${LATEST_PID}" --no-pager 2>&1 | head -50 || true
        echo
        echo "=== thread apply all bt 25 (${LATEST_PID}) ==="
        echo "thread apply all bt 25" | timeout 30 coredumpctl debug --debugger=gdb "${LATEST_PID}" 2>&1 | grep -E "^(#|Thread|Program)" | head -200 || true
    fi
} > "${STAGE}/coredump_bt.txt" 2>&1

# 6. System info
{
    echo "=== Date ==="
    date
    echo
    echo "=== uname ==="
    uname -a
    echo
    echo "=== Java version ==="
    java -version 2>&1 || true
    echo
    echo "=== Recent core sizes ==="
    coredumpctl list --no-pager 2>&1 | tail -5 || true
} > "${STAGE}/sysinfo.txt"

# 7. Bundle
OUT="${OUT_DIR}/${TS}.tar.gz"
tar czf "${OUT}" -C "${STAGE}" .

echo "Crash bundle written: ${OUT}"
echo "Contents:"
tar tzf "${OUT}" | sed 's/^/  /'
