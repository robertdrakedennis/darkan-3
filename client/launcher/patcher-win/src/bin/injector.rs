//! darkan_injector — cross-process DLL injector for rs2client.exe
//!
//! Usage:
//!   darkan_injector.exe <path-to-rs2client.exe> [args...]
//!
//! Flow (CreateRemoteThread + LoadLibraryW — the textbook safe injector):
//!   1. Locate darkan_patcher.dll on disk.
//!   2. CreateProcessW(rs2client.exe, ..., CREATE_SUSPENDED) — the main thread
//!      is created but suspended at the entry point.
//!   3. Resolve LoadLibraryW in *our* kernel32. On x64 Windows the kernel32
//!      base is fixed per session, so the same VA is valid in the target's
//!      address space — no need to enumerate the target's modules.
//!   4. VirtualAllocEx(MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE) in the target
//!      for the wide DLL path.
//!   5. WriteProcessMemory the UTF-16-encoded DLL path.
//!   6. CreateRemoteThread(target, lpStartAddress=LoadLibraryW, lpParameter=path).
//!   7. WaitForSingleObject on the remote thread; GetExitCodeThread — nonzero
//!      means LoadLibraryW returned a valid HMODULE (truncated to DWORD).
//!   8. VirtualFreeEx the path buffer.
//!   9. ResumeThread(main thread).
//!  10. CloseHandle on everything.
//!
//! Env vars (`DARKAN_RSA_MODULUS`, `DARKAN_JS5_RSA_MODULUS`, `DARKAN_HTTP_PORT`)
//! are inherited automatically by CreateProcessW — we don't override the env
//! block, so the patcher DLL sees whatever the injector saw.

#![cfg(windows)]

use std::env;
use std::ffi::OsStr;
use std::iter::once;
use std::mem::size_of;
use std::os::windows::ffi::OsStrExt;
use std::path::{Path, PathBuf};
use std::ptr;

use anyhow::{anyhow, bail, Context, Result};
use windows_sys::Win32::Foundation::{CloseHandle, FALSE, HANDLE, INVALID_HANDLE_VALUE};
use windows_sys::Win32::System::Diagnostics::Debug::WriteProcessMemory;
use windows_sys::Win32::System::LibraryLoader::{GetModuleHandleW, GetProcAddress};
use windows_sys::Win32::System::Memory::{
    VirtualAllocEx, VirtualFreeEx, MEM_COMMIT, MEM_RELEASE, MEM_RESERVE, PAGE_READWRITE,
};
use windows_sys::Win32::System::Threading::{
    CreateProcessW, CreateRemoteThread, GetExitCodeThread, ResumeThread, WaitForSingleObject,
    CREATE_SUSPENDED, INFINITE, LPTHREAD_START_ROUTINE, PROCESS_INFORMATION, STARTUPINFOW,
};

const DLL_NAME: &str = "darkan_patcher.dll";

fn main() {
    if let Err(e) = run() {
        eprintln!("[darkan-injector] FATAL: {:#}", e);
        std::process::exit(1);
    }
}

fn run() -> Result<()> {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        print_usage(&args[0]);
        bail!("missing required <path-to-rs2client.exe> argument");
    }

    let exe_path = PathBuf::from(&args[1]);
    if !exe_path.is_file() {
        bail!("target executable does not exist: {}", exe_path.display());
    }
    let child_args: Vec<String> = args.iter().skip(2).cloned().collect();

    let dll_path = locate_dll().context("failed to locate darkan_patcher.dll")?;
    println!("[darkan-injector] DLL    : {}", dll_path.display());
    println!("[darkan-injector] target : {}", exe_path.display());
    if !child_args.is_empty() {
        println!("[darkan-injector] args   : {:?}", child_args);
    }

    // Spawn the target suspended.
    let proc_info = spawn_suspended(&exe_path, &child_args)
        .context("CreateProcessW(CREATE_SUSPENDED) failed")?;
    println!(
        "[darkan-injector] spawned: pid={}, tid={}",
        proc_info.dwProcessId, proc_info.dwThreadId
    );

    // Drive the injection inside a guard so that, on any error path, we always
    // ResumeThread (so we don't leave a zombie suspended process around) and
    // CloseHandle on the OS objects.
    let inject_result = inject_dll(proc_info.hProcess, &dll_path);

    // Resume regardless of inject_result — even if injection failed, the user
    // probably wants the target to keep running (it'll just be un-patched).
    let resume_rc = unsafe { ResumeThread(proc_info.hThread) };
    if resume_rc == u32::MAX {
        eprintln!("[darkan-injector] WARNING: ResumeThread returned -1 (last error not checked)");
    }

    unsafe {
        CloseHandle(proc_info.hThread);
        CloseHandle(proc_info.hProcess);
    }

    inject_result.context("DLL injection failed")?;
    println!("[darkan-injector] done — target running with patcher loaded.");
    Ok(())
}

fn print_usage(prog: &str) {
    eprintln!("Usage: {} <path-to-rs2client.exe> [args...]", prog);
    eprintln!();
    eprintln!("Environment passed through to the patched process:");
    eprintln!("  DARKAN_RSA_MODULUS       login RSA modulus hex (required to enable patching)");
    eprintln!("  DARKAN_JS5_RSA_MODULUS   JS5 master-index RSA modulus hex");
    eprintln!("  DARKAN_HTTP_PORT         HTTP JS5 content port (default 80)");
    eprintln!();
    eprintln!("DLL search order:");
    eprintln!("  1. <injector_dir>\\{}", DLL_NAME);
    eprintln!("  2. %APPDATA%\\Darkan3\\{}", DLL_NAME);
    eprintln!("  3. %LOCALAPPDATA%\\Darkan3\\{}", DLL_NAME);
}

/// Hard-coded DLL search path:
///   1. Same directory as the running injector .exe
///   2. %APPDATA%\Darkan3\darkan_patcher.dll
///   3. %LOCALAPPDATA%\Darkan3\darkan_patcher.dll
fn locate_dll() -> Result<PathBuf> {
    let mut candidates: Vec<PathBuf> = Vec::new();

    if let Ok(injector_exe) = env::current_exe() {
        if let Some(dir) = injector_exe.parent() {
            candidates.push(dir.join(DLL_NAME));
        }
    }
    if let Ok(appdata) = env::var("APPDATA") {
        candidates.push(PathBuf::from(appdata).join("Darkan3").join(DLL_NAME));
    }
    if let Ok(local) = env::var("LOCALAPPDATA") {
        candidates.push(PathBuf::from(local).join("Darkan3").join(DLL_NAME));
    }

    for cand in &candidates {
        if cand.is_file() {
            return Ok(cand.canonicalize().unwrap_or_else(|_| cand.clone()));
        }
    }
    Err(anyhow!(
        "{} not found in any of:\n  {}",
        DLL_NAME,
        candidates
            .iter()
            .map(|p| p.display().to_string())
            .collect::<Vec<_>>()
            .join("\n  ")
    ))
}

/// CreateProcessW(target, "target arg1 arg2 ...", CREATE_SUSPENDED). The
/// command line must be a mutable buffer per the Win32 contract.
fn spawn_suspended(exe: &Path, args: &[String]) -> Result<PROCESS_INFORMATION> {
    // Build a "C:\path with spaces\exe.exe" arg0 arg1 ... command line. argv[0]
    // gets quoted; subsequent args are passed through verbatim (the caller is
    // responsible for any escaping). rs2client is currently invoked with no
    // user-supplied args, so verbatim passthrough is sufficient.
    let mut cmdline = String::new();
    cmdline.push('"');
    cmdline.push_str(&exe.to_string_lossy());
    cmdline.push('"');
    for a in args {
        cmdline.push(' ');
        cmdline.push_str(a);
    }
    let mut cmdline_w = to_wide(&cmdline);

    let mut startup: STARTUPINFOW = unsafe { std::mem::zeroed() };
    startup.cb = size_of::<STARTUPINFOW>() as u32;
    let mut proc_info: PROCESS_INFORMATION = unsafe { std::mem::zeroed() };

    // lpApplicationName=NULL forces CreateProcessW to parse the command line
    // (and resolve the exe via the quoted first token). This matches what
    // every standard-library spawner does and avoids a class of "wrong
    // executable" bugs when paths contain spaces.
    let ok = unsafe {
        CreateProcessW(
            ptr::null(),
            cmdline_w.as_mut_ptr(),
            ptr::null(),
            ptr::null(),
            FALSE,
            CREATE_SUSPENDED,
            ptr::null(),
            ptr::null(),
            &mut startup,
            &mut proc_info,
        )
    };
    if ok == FALSE {
        bail!(
            "CreateProcessW failed for {} (last_os_error={})",
            exe.display(),
            std::io::Error::last_os_error()
        );
    }
    Ok(proc_info)
}

/// Force the target process to call `LoadLibraryW(dll_path)` via
/// CreateRemoteThread. Returns Ok if the remote LoadLibraryW returned a
/// nonzero HMODULE (truncated to a DWORD by GetExitCodeThread).
fn inject_dll(h_process: HANDLE, dll_path: &Path) -> Result<()> {
    if h_process.is_null() || h_process == INVALID_HANDLE_VALUE {
        bail!("invalid target process handle");
    }

    // Resolve LoadLibraryW from kernel32 in *our* process. The Win32 loader
    // maps kernel32.dll at the same base in every process within a session
    // (x64 + KASLR-but-not-per-process), so the VA is portable across the
    // process boundary.
    let kernel32 = unsafe { GetModuleHandleW(to_wide("kernel32.dll").as_ptr()) };
    if kernel32.is_null() {
        bail!("GetModuleHandleW(\"kernel32.dll\") returned null");
    }
    let load_library_w = unsafe {
        GetProcAddress(
            kernel32,
            b"LoadLibraryW\0".as_ptr() as *const u8,
        )
    };
    let load_library_w = match load_library_w {
        Some(addr) => addr as usize,
        None => bail!("GetProcAddress(\"LoadLibraryW\") returned null"),
    };
    println!("[darkan-injector] LoadLibraryW @ 0x{:x}", load_library_w);

    // Write the DLL path (UTF-16, NUL-terminated) into the target.
    // `to_wide` takes `AsRef<OsStr>`. `Path` impls `AsRef<OsStr>` directly, so
    // we can skip the to_string_lossy round-trip (which also returns Cow, not OsStr).
    let dll_path_wide: Vec<u16> = to_wide(dll_path);
    let path_bytes_len = dll_path_wide.len() * size_of::<u16>();

    let remote_buf = unsafe {
        VirtualAllocEx(
            h_process,
            ptr::null(),
            path_bytes_len,
            MEM_COMMIT | MEM_RESERVE,
            PAGE_READWRITE,
        )
    };
    if remote_buf.is_null() {
        bail!(
            "VirtualAllocEx({} bytes) failed: {}",
            path_bytes_len,
            std::io::Error::last_os_error()
        );
    }

    let mut bytes_written: usize = 0;
    let write_ok = unsafe {
        WriteProcessMemory(
            h_process,
            remote_buf,
            dll_path_wide.as_ptr() as *const _,
            path_bytes_len,
            &mut bytes_written,
        )
    };
    if write_ok == FALSE || bytes_written != path_bytes_len {
        let err = std::io::Error::last_os_error();
        unsafe { VirtualFreeEx(h_process, remote_buf, 0, MEM_RELEASE) };
        bail!(
            "WriteProcessMemory wrote {}/{} bytes: {}",
            bytes_written,
            path_bytes_len,
            err
        );
    }

    // SAFETY: LoadLibraryW has the signature `HMODULE WINAPI LoadLibraryW(LPCWSTR)`,
    // which matches LPTHREAD_START_ROUTINE's `DWORD (*)(LPVOID)` for the calling
    // convention purposes the loader cares about (Win64 fastcall, single
    // pointer argument, integer return). This is the canonical injection
    // recipe used in every textbook DLL injector since Windows NT 4.
    let start_routine: LPTHREAD_START_ROUTINE = Some(unsafe {
        std::mem::transmute::<usize, unsafe extern "system" fn(*mut std::ffi::c_void) -> u32>(
            load_library_w,
        )
    });

    let h_thread = unsafe {
        CreateRemoteThread(
            h_process,
            ptr::null(),
            0,
            start_routine,
            remote_buf,
            0,
            ptr::null_mut(),
        )
    };
    if h_thread.is_null() {
        let err = std::io::Error::last_os_error();
        unsafe { VirtualFreeEx(h_process, remote_buf, 0, MEM_RELEASE) };
        bail!("CreateRemoteThread failed: {}", err);
    }

    // Wait for LoadLibraryW to return. The remote thread exits as soon as
    // LoadLibraryW does — that's the entire body of the thread.
    let wait_rc = unsafe { WaitForSingleObject(h_thread, INFINITE) };
    if wait_rc != 0 {
        // WAIT_OBJECT_0 = 0; anything else (WAIT_TIMEOUT/WAIT_FAILED) is a
        // bug, but we still try to read the exit code below.
        eprintln!(
            "[darkan-injector] WARNING: WaitForSingleObject returned 0x{:x} (expected 0)",
            wait_rc
        );
    }

    let mut exit_code: u32 = 0;
    let got_exit = unsafe { GetExitCodeThread(h_thread, &mut exit_code) };
    unsafe {
        CloseHandle(h_thread);
        VirtualFreeEx(h_process, remote_buf, 0, MEM_RELEASE);
    }

    if got_exit == FALSE {
        bail!(
            "GetExitCodeThread failed: {}",
            std::io::Error::last_os_error()
        );
    }
    // exit_code is the DWORD-truncated HMODULE returned by LoadLibraryW.
    // On x64 a real HMODULE is a pointer; the low 32 bits being nonzero is
    // the conventional signal of "load succeeded". A genuine failure
    // returns 0 (NULL HMODULE).
    if exit_code == 0 {
        bail!("remote LoadLibraryW returned NULL — DLL load failed inside target");
    }
    println!(
        "[darkan-injector] LoadLibraryW returned (low32) 0x{:x} — DLL loaded",
        exit_code
    );
    Ok(())
}

/// UTF-16 encode with a trailing NUL, the form every Win32 *W function wants.
fn to_wide<S: AsRef<OsStr>>(s: S) -> Vec<u16> {
    s.as_ref().encode_wide().chain(once(0u16)).collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn to_wide_terminates_with_nul() {
        let w = to_wide("ok");
        assert_eq!(w.last(), Some(&0u16));
        assert_eq!(w.len(), 3); // 'o', 'k', NUL
    }

    #[test]
    fn locate_dll_fails_gracefully_when_absent() {
        // We can't reliably remove every candidate location, but we can at
        // least exercise the error formatting path.
        let result = locate_dll();
        // Either OK (the dll was deployed) or Err containing every candidate.
        if let Err(e) = result {
            let msg = format!("{}", e);
            assert!(msg.contains(DLL_NAME), "error message should name the DLL");
        }
    }
}
