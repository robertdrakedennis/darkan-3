#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cstdarg>
#include <cstdint>
#include <dirent.h>
#include <dlfcn.h>
#include <pthread.h>
#include <unistd.h>
#include <funchook.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <signal.h>
#include <time.h>
#include <atomic>
#include <ucontext.h>
#include <execinfo.h>
#include <pwd.h>

// Resolve the real user home from the passwd db, NOT $HOME — the launcher
// (bolt) overrides $HOME to its own data dir, which is where crash logs were
// silently landing and why they appeared "missing".
static const char *resolve_real_home() {
    struct passwd *pw = getpwuid(getuid());
    if (pw && pw->pw_dir && pw->pw_dir[0]) return pw->pw_dir;
    const char *h = std::getenv("HOME");
    return h ? h : "/tmp";
}

FILE *log_file = nullptr;
JavaVM *jvm = nullptr;

typedef jint (*JNI_CreateJavaVM_t)(JavaVM **, void **, void *);

// Forward declaration so the forensics block below can use it.
void log_message(const char *format, ...) __attribute__((format(printf, 1, 2)));

// ============================================================================
// Crash forensics: breadcrumb ring buffer + signal handler.
//
// Breadcrumbs are written from Kotlin via JNI (see breadcrumb_register_natives
// below). The ring is fixed-size static memory in this .so's data segment so
// the SIGSEGV signal handler can read it without touching heap or locks.
// ============================================================================

struct Breadcrumb {
    std::atomic<uint64_t> seq;   // sequence # when written (0 = empty slot)
    uint64_t tid;
    int64_t addr;
    char tag[96];
    char pad[8];                  // pad to 128 bytes for cache-line cleanliness
};

static constexpr size_t BREADCRUMB_COUNT = 64;
static volatile Breadcrumb breadcrumbs[BREADCRUMB_COUNT];
static std::atomic<uint64_t> breadcrumb_seq{1};   // start at 1; 0 means empty

static char crash_log_path[512];   // populated in on_load()

// Signal-safe writers (avoid stdio in handler).
static void sigsafe_write(int fd, const char *s, size_t n) {
    while (n > 0) {
        ssize_t r = write(fd, s, n);
        if (r < 0) return;
        s += r; n -= r;
    }
}
static void sigsafe_str(int fd, const char *s) { sigsafe_write(fd, s, std::strlen(s)); }
static void sigsafe_dec(int fd, uint64_t v) {
    char buf[24]; int i = 23;
    buf[i] = 0;
    if (v == 0) { buf[--i] = '0'; sigsafe_str(fd, &buf[i]); return; }
    while (v > 0 && i > 0) { buf[--i] = (char)('0' + (v % 10)); v /= 10; }
    sigsafe_str(fd, &buf[i]);
}
static void sigsafe_hex(int fd, uint64_t v) {
    static const char hex[] = "0123456789abcdef";
    char buf[20]; int i = 19;
    buf[i] = 0;
    if (v == 0) { buf[--i] = '0'; sigsafe_str(fd, &buf[i]); return; }
    while (v > 0 && i > 0) { buf[--i] = hex[v & 0xf]; v >>= 4; }
    sigsafe_str(fd, &buf[i]);
}

static struct sigaction prev_sigsegv;
static struct sigaction prev_sigbus;
static struct sigaction prev_sigabrt;
static struct sigaction prev_sigfpe;
static struct sigaction prev_sigill;
static struct sigaction prev_sigterm;
static struct sigaction prev_sighup;
static struct sigaction prev_sigquit;
static std::atomic<int> crash_handler_reentry{0};

static void crash_signal_handler(int sig, siginfo_t *info, void *ctx) {
    // Select the matching saved-prior handler based on signal number.
    struct sigaction *prev = nullptr;
    switch (sig) {
        case SIGSEGV: prev = &prev_sigsegv; break;
        case SIGBUS:  prev = &prev_sigbus; break;
        case SIGABRT: prev = &prev_sigabrt; break;
        case SIGFPE:  prev = &prev_sigfpe; break;
        case SIGILL:  prev = &prev_sigill; break;
        case SIGTERM: prev = &prev_sigterm; break;
        case SIGHUP:  prev = &prev_sighup; break;
        case SIGQUIT: prev = &prev_sigquit; break;
    }

    // Prevent recursive crashes inside the handler from causing infinite reentry.
    if (crash_handler_reentry.fetch_add(1) > 0) {
        if (prev) {
            if (prev->sa_flags & SA_SIGINFO) {
                if (prev->sa_sigaction) prev->sa_sigaction(sig, info, ctx);
            } else if (prev->sa_handler && prev->sa_handler != SIG_DFL && prev->sa_handler != SIG_IGN) {
                prev->sa_handler(sig);
            }
        }
        return;
    }

    int fd = open(crash_log_path, O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, 0644);
    if (fd >= 0) {
        sigsafe_str(fd, "=== UNDERCUT CRASH FORENSICS ===\nsignal: ");
        sigsafe_dec(fd, sig);
        sigsafe_str(fd, " (");
        sigsafe_str(fd,
            sig == SIGSEGV ? "SIGSEGV" :
            sig == SIGBUS  ? "SIGBUS"  :
            sig == SIGABRT ? "SIGABRT" :
            sig == SIGFPE  ? "SIGFPE"  :
            sig == SIGILL  ? "SIGILL"  :
            sig == SIGTERM ? "SIGTERM (external — launcher / parent process / kill -15)" :
            sig == SIGHUP  ? "SIGHUP (controlling tty closed / external)" :
            sig == SIGQUIT ? "SIGQUIT (external Ctrl-\\ / kill -3)" : "other");
        sigsafe_str(fd, ")\nfault_addr: 0x");
        sigsafe_hex(fd, (uint64_t)(uintptr_t)info->si_addr);
        sigsafe_str(fd, "\nsi_code: ");
        sigsafe_dec(fd, (uint64_t)info->si_code);
        sigsafe_str(fd, "\ntid: ");
        sigsafe_dec(fd, (uint64_t)syscall(SYS_gettid));
        sigsafe_str(fd, "\nip: 0x");
        ucontext_t *uc = (ucontext_t *)ctx;
        sigsafe_hex(fd, (uint64_t)uc->uc_mcontext.gregs[REG_RIP]);
        sigsafe_str(fd, "\nrsp: 0x");
        sigsafe_hex(fd, (uint64_t)uc->uc_mcontext.gregs[REG_RSP]);

        // Breadcrumbs FIRST — they only read static memory + the ucontext and
        // cannot fault, so they always reach disk even if backtrace() (below)
        // re-faults on a corrupted stack. backtrace() walks frames and ELF
        // headers and is the one risky call in this handler, so it goes LAST.
        sigsafe_str(fd, "\n\n=== Last breadcrumbs (newest first) ===\n");

        uint64_t newest = breadcrumb_seq.load(std::memory_order_relaxed);
        uint64_t cutoff = newest > BREADCRUMB_COUNT ? newest - BREADCRUMB_COUNT : 1;
        for (uint64_t s = newest - 1; s + 1 > cutoff; s--) {
            volatile Breadcrumb &b = breadcrumbs[s % BREADCRUMB_COUNT];
            if (b.seq.load(std::memory_order_relaxed) != s) {
                // Slot got overwritten or never written.
                if (s == 0) break;
                continue;
            }
            sigsafe_str(fd, "[seq=");
            sigsafe_dec(fd, s);
            sigsafe_str(fd, " tid=");
            sigsafe_dec(fd, b.tid);
            sigsafe_str(fd, "] ");
            sigsafe_str(fd, (const char *)b.tag);
            if (b.addr != 0) {
                sigsafe_str(fd, " addr=0x");
                sigsafe_hex(fd, (uint64_t)b.addr);
            }
            sigsafe_str(fd, "\n");
            if (s == 0) break;
        }

        // Risky LAST: backtrace() can re-fault on a corrupted stack. Everything
        // above is already flushed (unbuffered write()s) before we attempt it.
        sigsafe_str(fd, "\n=== Native backtrace (may be truncated if it refaults) ===\n");
        void *bt[80];
        int btn = backtrace(bt, 80);
        backtrace_symbols_fd(bt, btn, fd);

        sigsafe_str(fd, "=== END ===\n");
        close(fd);
    }

    // Chain to previous handler (JVM installed it, then we installed ours,
    // and HotSpot's chaining will have called us back as the saved-prior).
    if (prev) {
        if (prev->sa_flags & SA_SIGINFO) {
            if (prev->sa_sigaction) prev->sa_sigaction(sig, info, ctx);
        } else if (prev->sa_handler && prev->sa_handler != SIG_DFL && prev->sa_handler != SIG_IGN) {
            prev->sa_handler(sig);
        }
    }
    // If no previous handler, restore default and re-raise so process dies properly.
    signal(sig, SIG_DFL);
    raise(sig);
}

static char crash_altstack[256 * 1024];

static void install_crash_handler() {
    // Dedicated signal stack so we can still dump on a stack-overflow crash
    // (the normal stack is unusable then, and SA_ONSTACK needs this installed).
    stack_t ss{};
    ss.ss_sp = crash_altstack;
    ss.ss_size = sizeof(crash_altstack);
    ss.ss_flags = 0;
    sigaltstack(&ss, nullptr);

    // Pre-warm backtrace() so the first (in-handler) call doesn't lazily
    // dlopen libgcc_s — that would be unsafe from a signal handler.
    void *warm[4];
    (void)backtrace(warm, 4);

    struct sigaction sa{};
    sa.sa_sigaction = crash_signal_handler;
    sa.sa_flags = SA_SIGINFO | SA_ONSTACK;
    sigemptyset(&sa.sa_mask);
    sigaction(SIGSEGV, &sa, &prev_sigsegv);
    sigaction(SIGBUS, &sa, &prev_sigbus);
    // SIGABRT covers JVM's os::abort() path (fatal assertion / unhandled fatal),
    // SIGFPE / SIGILL cover the rarer cases. Without these, hard crashes that
    // bypass the SEGV path leave no last-crash.txt at all.
    sigaction(SIGABRT, &sa, &prev_sigabrt);
    sigaction(SIGFPE, &sa, &prev_sigfpe);
    sigaction(SIGILL, &sa, &prev_sigill);
    // Termination signals — these don't generate coredumps and aren't normally
    // "crashes", but the launcher (rs3linux) sends them when it kills the client
    // for session expiry / watchdog reasons. Capturing them tells us *something*
    // killed the client externally, vs the JVM crashing internally.
    sigaction(SIGTERM, &sa, &prev_sigterm);
    sigaction(SIGHUP, &sa, &prev_sighup);
    sigaction(SIGQUIT, &sa, &prev_sigquit);
}

// ============================================================================
// Exit-path forensics. The recurring crash leaves NO signal artifacts (no core,
// no hs_err, our SIGSEGV/SIGBUS handler never runs) yet the process vanishes
// right after a synthetic DoAction. That means the native client is exiting via
// exit()/_exit() (or being SIGKILLed). Signals can't catch that, so we hook the
// libc exit functions to dump the breadcrumb trail before the process leaves.
// ============================================================================
static char exit_log_path[512];   // populated in on_load()
static std::atomic<int> exit_forensics_done{0};

typedef void (*exit_fn_t)(int);
static exit_fn_t real_exit = nullptr;
static exit_fn_t real__exit = nullptr;
static exit_fn_t real__Exit = nullptr;

static void write_exit_forensics(const char *via, int code) {
    if (exit_forensics_done.fetch_add(1) > 0) return;  // write once
    int fd = open(exit_log_path, O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, 0644);
    if (fd < 0) return;
    sigsafe_str(fd, "=== UNDERCUT EXIT FORENSICS ===\nvia: ");
    sigsafe_str(fd, via);
    sigsafe_str(fd, "\nexit_code: ");
    sigsafe_dec(fd, (uint64_t)(unsigned)code);
    sigsafe_str(fd, "\ntid: ");
    sigsafe_dec(fd, (uint64_t)syscall(SYS_gettid));
    sigsafe_str(fd, "\n\n=== Last breadcrumbs (newest first) ===\n");
    uint64_t newest = breadcrumb_seq.load(std::memory_order_relaxed);
    uint64_t cutoff = newest > BREADCRUMB_COUNT ? newest - BREADCRUMB_COUNT : 1;
    for (uint64_t s = newest - 1; s + 1 > cutoff; s--) {
        volatile Breadcrumb &b = breadcrumbs[s % BREADCRUMB_COUNT];
        if (b.seq.load(std::memory_order_relaxed) != s) { if (s == 0) break; continue; }
        sigsafe_str(fd, "[seq=");
        sigsafe_dec(fd, s);
        sigsafe_str(fd, " tid=");
        sigsafe_dec(fd, b.tid);
        sigsafe_str(fd, "] ");
        sigsafe_str(fd, (const char *)b.tag);
        if (b.addr != 0) { sigsafe_str(fd, " addr=0x"); sigsafe_hex(fd, (uint64_t)b.addr); }
        sigsafe_str(fd, "\n");
        if (s == 0) break;
    }
    sigsafe_str(fd, "\n=== Native backtrace ===\n");
    void *bt[80];
    int btn = backtrace(bt, 80);
    backtrace_symbols_fd(bt, btn, fd);
    sigsafe_str(fd, "=== END ===\n");
    close(fd);
}

static void hook_exit(int code)  { write_exit_forensics("exit()", code);  real_exit(code);  __builtin_unreachable(); }
static void hook__exit(int code) { write_exit_forensics("_exit()", code); real__exit(code); __builtin_unreachable(); }
static void hook__Exit(int code) { write_exit_forensics("_Exit()", code); real__Exit(code); __builtin_unreachable(); }

static void install_one_exit_hook(const char *name, void *replacement, exit_fn_t *saved) {
    void *target = dlsym(RTLD_DEFAULT, name);
    if (!target) { log_message("[FORENSICS] exit hook: %s not found\n", name); return; }
    funchook_t *fh = funchook_create();
    if (!fh) return;
    void *tgt = target;
    if (funchook_prepare(fh, &tgt, replacement) != FUNCHOOK_ERROR_SUCCESS) { funchook_destroy(fh); return; }
    *saved = (exit_fn_t)tgt;
    if (funchook_install(fh, 0) != FUNCHOOK_ERROR_SUCCESS) { funchook_destroy(fh); return; }
    log_message("[FORENSICS] hooked %s for exit forensics.\n", name);
}

static void install_exit_hooks() {
    install_one_exit_hook("exit",  (void *)hook_exit,  &real_exit);
    install_one_exit_hook("_exit", (void *)hook__exit, &real__exit);
    install_one_exit_hook("_Exit", (void *)hook__Exit, &real__Exit);
}

// JNI breadcrumb functions, registered via RegisterNatives after the JVM is up.
static jclass forensics_class_cache = nullptr;

extern "C" JNIEXPORT void JNICALL
breadcrumb_jni(JNIEnv *env, jclass /*cls*/, jstring tag, jlong addr) {
    uint64_t seq = breadcrumb_seq.fetch_add(1, std::memory_order_relaxed);
    volatile Breadcrumb &b = breadcrumbs[seq % BREADCRUMB_COUNT];
    b.tid = (uint64_t)pthread_self();
    b.addr = addr;
    const char *cstr = tag ? env->GetStringUTFChars(tag, nullptr) : "";
    if (cstr) {
        size_t n = std::strlen(cstr);
        if (n >= sizeof(b.tag)) n = sizeof(b.tag) - 1;
        std::memcpy((void *)b.tag, cstr, n);
        ((char *)b.tag)[n] = 0;
        if (tag) env->ReleaseStringUTFChars(tag, cstr);
    } else {
        ((char *)b.tag)[0] = 0;
    }
    // Publish seq last so signal handler sees a fully-written slot.
    b.seq.store(seq, std::memory_order_release);
}

static void register_forensics_natives(JNIEnv *env) {
    jclass cls = env->FindClass("com/undercut/diag/CrashForensics");
    if (!cls) {
        env->ExceptionClear();
        log_message("[FORENSICS] CrashForensics class not found; breadcrumbs disabled.\n");
        return;
    }
    static const JNINativeMethod methods[] = {
        { (char *)"breadcrumbNative", (char *)"(Ljava/lang/String;J)V", (void *)breadcrumb_jni },
    };
    if (env->RegisterNatives(cls, methods, 1) != 0) {
        log_message("[FORENSICS] RegisterNatives failed.\n");
        env->ExceptionClear();
        return;
    }
    forensics_class_cache = (jclass)env->NewGlobalRef(cls);
    log_message("[FORENSICS] breadcrumb JNI registered.\n");
}

void log_message(const char *format, ...) {
    va_list args;
    va_start(args, format);
    std::vfprintf(log_file, format, args);
    std::fflush(log_file);
    va_end(args);
}

void find_and_expand_jarfiles(const char *classpath_dir, char *classpath_option, size_t option_size) {
    DIR *dp = opendir(classpath_dir);

    if (dp == nullptr) {
        perror("opendir");
        return;
    }

    std::snprintf(classpath_option, option_size, "-Djava.class.path=");

    struct dirent *entry;
    while ((entry = readdir(dp)) != nullptr) {
        size_t len = std::strlen(entry->d_name);

        if (len > 4 && std::strcmp(entry->d_name + len - 4, ".jar") == 0) {
            std::strncat(classpath_option, classpath_dir, option_size - std::strlen(classpath_option) - 1);
            std::strncat(classpath_option, "/", option_size - std::strlen(classpath_option) - 1);
            std::strncat(classpath_option, entry->d_name, option_size - std::strlen(classpath_option) - 1);
            std::strncat(classpath_option, ":", option_size - std::strlen(classpath_option) - 1);
        }
    }

    closedir(dp);

    if (classpath_option[std::strlen(classpath_option) - 1] == ':')
        classpath_option[std::strlen(classpath_option) - 1] = '\0';
}

unsigned long get_proc_base_addr(pid_t pid) {
    char maps_path[256];
    std::snprintf(maps_path, sizeof(maps_path), "/proc/%d/maps", pid);

    FILE *maps_file = std::fopen(maps_path, "r");
    if (!maps_file) {
        log_message("[ERROR] fopen for maps file failed\n");
        return 0;
    }

    unsigned long base_address = 0;
    char line[256];
    while (std::fgets(line, sizeof(line), maps_file)) {
        if (std::strstr(line, "r-xp") && !std::strstr(line, "vdso")) {
            std::sscanf(line, "%lx", &base_address);
            break;
        }
    }

    std::fclose(maps_file);
    return base_address;
}

bool is_port_available(int port) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0)
        return false;

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;

    int result = bind(sock, (struct sockaddr*)&addr, sizeof(addr));
    close(sock);
    
    return result == 0;
}

int find_available_port(int start_port) {
    for (int port = start_port; port < start_port + 100; port++) {
        if (is_port_available(port))
            return port;
    }
    return start_port;
}

void* initialize_undercut(void* base_address) {
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options[16];

    jclass supervisorClass = nullptr;
    jmethodID startMethod = nullptr;
    jstring engineHomeStr = nullptr;

    const char* classpath_dir = std::getenv("UNDERCUT_HOME_DIR");
    if (classpath_dir == nullptr) {
        log_message("Environment variable UNDERCUT_HOME_DIR is not set.\n");
        return nullptr;
    }

    const char* java_home = std::getenv("JAVA_HOME");
    if (java_home == nullptr) {
        log_message("Environment variable JAVA_HOME is not set.\n");
        return nullptr;
    }

    char libjvm[512];
    std::snprintf(libjvm, sizeof(libjvm), "%s/lib/server/libjvm.so", java_home);

    log_message("Initializing Project Undercut JVM bootstrap with classpath at %s\n", classpath_dir);

    // System classpath = the tiny pure-Java supervisor jar ONLY. The engine shadow jar must NOT be
    // here, or the supervisor's child URLClassLoader would parent-delegate to stale classes and a
    // rebuilt engine jar would never take effect on reinject. The supervisor resolves + loads the
    // engine shadow jar from UNDERCUT_HOME_DIR itself.
    char classpath_option[2048];
    std::snprintf(classpath_option, sizeof(classpath_option),
                  "-Djava.class.path=%s/undercut-supervisor.jar", classpath_dir);

    char librarypath_option[512];
    std::snprintf(librarypath_option, sizeof(librarypath_option), "-Djava.library.path=%s", classpath_dir);

    char debug_option[128];
    int debug_port = find_available_port(5005);
    std::snprintf(debug_option, sizeof(debug_option), "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:%d", debug_port);

    // Crash forensics: write hs_err to the known logs directory so collect-crash.sh
    // and the user can find it. Keep MeshProjection and SceneSnapshot.tickSnapshot
    // out of the JIT — these are the unsafe-memory hot paths; with them in the
    // interpreter the hs_err_pid log carries the Kotlin source line of the fault
    // instead of an opaque JIT region address.
    const char *home = resolve_real_home();
    char error_file_option[512];
    std::snprintf(error_file_option, sizeof(error_file_option),
                  "-XX:ErrorFile=%s/.undercut/logs/hs_err_pid%%p.log", home);
    options[0].optionString = const_cast<char*>("--enable-native-access=ALL-UNNAMED");
    options[1].optionString = classpath_option;
    options[2].optionString = librarypath_option;
    options[3].optionString = const_cast<char*>("-verbose:jni");
    options[4].optionString = debug_option;
    options[5].optionString = error_file_option;
    // Lower JIT thresholds so render-hot paths (MeshProjection, SceneSnapshot)
    // hit C2 compilation in ~1-2 s instead of ~15 s after a toggle.
    options[6].optionString = const_cast<char*>("-XX:CompileThresholdScaling=0.2");
    // Force AWT into headless mode so BufferedImage/Graphics2D used by the texture
    // loader doesn't try to open the X11 DISPLAY (the parent rs2client process is
    // EGL/Wayland on most modern setups and X11 init throws AWTError, which then
    // poisons every subsequent render frame with NoClassDefFoundError spam).
    options[7].optionString = const_cast<char*>("-Djava.awt.headless=true");
    // Write a core dump when the JVM signal handler catches a SIGSEGV. Without
    // this the process just dies and we get nothing — the ErrorFile hs_err_pid
    // log alone doesn't capture state from threads outside the JVM's own.
    // Pair with `ulimit -c unlimited` (set in inject.sh) so the kernel actually
    // honours the request.
    options[8].optionString = const_cast<char*>("-XX:+CreateCoredumpOnCrash");
    // Full stack traces for repeated exceptions — without this, after a JIT
    // warm-up the JVM elides stack data on recurring throws, which would make
    // hs_err logs less useful when a crash follows a chain of trapped exceptions.
    options[9].optionString = const_cast<char*>("-XX:-OmitStackTraceInFastThrow");
    //options[N].optionString = const_cast<char*>("-Xint"); //enable to debug native better

    vm_args.version = JNI_VERSION_1_8;
    vm_args.nOptions = 10;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = JNI_TRUE;

    // Load libjsig BEFORE libjvm so its sigaction interposes the JVM's. Without
    // this HotSpot installs its own SIGSEGV/SIGBUS handlers and does NOT chain
    // to ours on a fatal native fault, so our crash forensics never run. With
    // it, our pre-installed handler is invoked for faults the JVM doesn't own.
    char libjsig[512];
    std::snprintf(libjsig, sizeof(libjsig), "%s/lib/libjsig.so", java_home);
    if (dlopen(libjsig, RTLD_NOW | RTLD_GLOBAL))
        log_message("Loaded libjsig (JVM signal chaining enabled).\n");
    else
        log_message("WARN: libjsig not loaded (%s); crash chaining degraded.\n", dlerror());

    log_message("Loading libjvm.so from %s\n", libjvm);

    void *handle = dlopen(libjvm, RTLD_NOW | RTLD_GLOBAL);
    if (!handle) {
        log_message("Failed to load libjvm.so\n");
        return nullptr;
    }
    dlerror();

    JNI_CreateJavaVM_t JNI_CreateJavaVM = reinterpret_cast<JNI_CreateJavaVM_t>(dlsym(handle, "JNI_CreateJavaVM"));
    const char *dlsym_error = dlerror();
    if (dlsym_error) {
        log_message("Failed to load symbol JNI_CreateJavaVM: %s\n", dlsym_error);
        dlclose(handle);
        return nullptr;
    }

    jint res = JNI_CreateJavaVM(&jvm, reinterpret_cast<void**>(&env), &vm_args);
    if (res != JNI_OK) {
        log_message("Failed to create JVM %d\n", res);
        return nullptr;
    }

    log_message("Successfully created JVM (%d). Finding Object class to verify success...\n", res);

    jclass objCls = env->FindClass("java/lang/Object");

    log_message("Successfully found Object class (%p). Registering forensics natives...\n", static_cast<void*>(objCls));

    register_forensics_natives(env);

    log_message("Finding Supervisor class...\n");

    supervisorClass = env->FindClass("com/undercut/supervisor/Supervisor");
    if (supervisorClass == nullptr) {
        log_message("Failed to find class com.undercut.supervisor.Supervisor\n");
        goto destroy;
    }

    log_message("Found Supervisor class (%p). Finding start method...\n", static_cast<void*>(supervisorClass));

    startMethod = env->GetStaticMethodID(supervisorClass, "start", "(JLjava/lang/String;)V");
    if (startMethod == nullptr) {
        log_message("Failed to find method start in class Supervisor\n");
        goto destroy;
    }

    log_message("Found start method (%p). Calling Supervisor.start with base %p, home %s\n",
                reinterpret_cast<void*>(startMethod), base_address, classpath_dir);

    engineHomeStr = env->NewStringUTF(classpath_dir);
    env->CallStaticVoidMethod(supervisorClass, startMethod, reinterpret_cast<jlong>(base_address), engineHomeStr);
    if (env->ExceptionOccurred()) {
        log_message("Exception occurred calling Supervisor.start:\n");
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    while (true) {
        usleep(10000);
    }

    return nullptr;

destroy:
    jvm->DestroyJavaVM();
    return nullptr;
}

// Forward declarations for SDL hook functions
extern "C" void Undercut_InitializeSDLHook();
extern "C" void Undercut_CleanupSDLHook();

extern "C" __attribute__((constructor)) void on_load() {
    pid_t current_pid = getpid();

    char log_path[256];
    std::snprintf(log_path, sizeof(log_path), "/tmp/undercut_log_%d.txt", current_pid);

    log_file = std::fopen(log_path, "a");
    if (log_file == nullptr) {
        log_file = stderr;
        std::printf("Failed to access logfile at %s\n", log_path);
    } else {
        char stderr_path[256];
        std::snprintf(stderr_path, sizeof(stderr_path), "/tmp/undercut_stderr_%d.txt", current_pid);
        std::freopen(stderr_path, "a", stderr);
    }

    // Resolve crash forensics output path. The directory may not exist on first
    // run — best-effort mkdir, ignore EEXIST.
    const char *home = resolve_real_home();
    char crash_dir[400];
    std::snprintf(crash_dir, sizeof(crash_dir), "%s/.undercut/logs", home);
    mkdir(crash_dir, 0755);  // ignore errors (probably already exists)
    std::snprintf(crash_log_path, sizeof(crash_log_path), "%s/last-crash.txt", crash_dir);
    std::snprintf(exit_log_path, sizeof(exit_log_path), "%s/last-exit.txt", crash_dir);

    // Install the signal handler BEFORE the JVM starts so HotSpot picks our
    // handler up as its "previous" handler and chains to us on fatal SEGV.
    install_crash_handler();
    log_message("[FORENSICS] crash handler installed; will write to %s\n", crash_log_path);

    // Catch non-signal process exits (exit/_exit/_Exit) — the recurring crash
    // leaves no signal artifacts, so it's an exit path, not a fault.
    install_exit_hooks();
    log_message("[FORENSICS] exit hooks installed; will write to %s\n", exit_log_path);

    // Initialize SDL_PollEvent hook early
    Undercut_InitializeSDLHook();

    unsigned long base_address = get_proc_base_addr(current_pid);

    log_message("Shared object loaded. Starting JVM into PID: %d at base address %p...\n", current_pid, reinterpret_cast<void*>(base_address));

    pthread_t thread;
    if (pthread_create(&thread, nullptr, initialize_undercut, reinterpret_cast<void*>(base_address)) != 0) {
        log_message("Failed to create thread\n");
        return;
    }
    pthread_detach(thread);
}

extern "C" __attribute__((destructor))
void on_unload(void) {
    log_message("Shared library unloaded!\n");
    
    // Cleanup SDL hook
    Undercut_CleanupSDLHook();
}