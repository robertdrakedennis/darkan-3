# Project Undercut Engine

Project Undercut is a reverse engineering and scripting tool for the Linux RuneScape NXT client. It injects into the running RS3 process via `gdb` + `dlopen`, using a native C++ bootstrap library that hooks the JVM and loads a Kotlin-based scripting engine.

> **Linux only.** There is no native Windows support. See [WSL 2 Setup](#running-on-windows-1011-via-wsl-2) if you're on Windows.

## Discord

https://discord.gg/xZnszEdHdp

## Features

- **Process injection** via GDB into the running NXT client
- **Coroutine-based scripting** with automatic 40ms tick loop and parallel execution
- **Hot-reload / live debugging** over JDWP (port 5005) for rapid script development
- **ImGui overlay** rendered via SDL2 + EGL for in-game UI
- **Game cache parser** with SQLite storage
- **Pathfinding** with collision detection
- **Traversal DSL** for building movement/interaction sequences

## Prerequisites

| Dependency | Notes |
|---|---|
| **Java 25** (Project Panama) | `JAVA_HOME` must be set |
| **Git** | With `--recursive` for submodules |
| **CMake + Clang** | For the native bootstrap library |
| **GDB** | Used by the inject script |
| **SDL2** | Runtime dependency for the overlay |

## Getting Started

### 1. Install dependencies

**Arch Linux:**

```bash
sudo pacman -S git base-devel cmake clang gdb curl zip unzip jdk-openjdk sdl2
```

Optionally install Java 25 via [SDKMAN](https://sdkman.io/):

```bash
curl -s "https://get.sdkman.io" | bash
sdk install java 25-tem
```

**Other distros:** Install the equivalent packages for your package manager. The dependencies are the same -- only the package names differ.

### 2. Clone the repository

```bash
git clone --recursive https://gitlab.com/project-undercut/engine.git
cd engine
```

### 3. Build

```bash
./gradlew build
```

This compiles the Kotlin engine, builds the native bootstrap library (`libundercutbootstrap.so`), and produces a shadow JAR in `build/libs/`.

### 4. Launch RS3 and inject

Start the RuneScape client (e.g. via [Bolt Launcher](https://github.com/nicholasgasior/bolt-launcher)), then:

```bash
./inject
```

The script will find running `rs2client` processes, let you pick one if there are multiple, and inject the library. Requires `sudo` for GDB attachment.

## Script Development

Scripts are Kotlin classes annotated with `@ScriptDescription` and placed in `~/.undercut/scripts/` as JARs. The engine auto-discovers and loads them at runtime.

See `example-script-module/` for a working example. Build it with:

```bash
./gradlew :example-script-module:build
```

The JAR is automatically copied to `~/.undercut/scripts/`.

### Hot-Reload / Remote Debugging

You can attach IntelliJ's remote debugger for live code hot-swap during development:

1. Open **Run > Edit Configurations** in IntelliJ
2. Add a **Remote JVM Debug** configuration with port **5005**
3. *(Optional)* Under the **Logs** tab, add `~/.undercut/` as the log directory to see `println` output and stack traces in the IDE console

Start RS3 and inject first, then connect the debugger.

![Edit Configurations](image.png)
![Remote JVM Debug setup](image-1.png)
![Log file configuration](image-2.png)

## Build Commands Reference

| Command | Description |
|---|---|
| `./gradlew build` | Full build (Kotlin + native + shadow JAR) |
| `./gradlew compileKotlin` | Compile Kotlin only (fast error check) |
| `./gradlew buildNativeBootstrap` | Build native `.so` only |
| `./gradlew :example-script-module:build` | Build example scripts to `~/.undercut/scripts/` |

## Running on Windows 10/11 via WSL 2

You can run Project Undercut on Windows through WSL 2 with Arch Linux.

**Video guide:** https://www.youtube.com/watch?v=ql959hpUTP0

### WSL 2 prerequisites

1. [Enable Hyper-V / virtualization in your BIOS](https://www.youtube.com/watch?v=rrpzWCPatLo)
2. [Enable WSL 2 in Windows Features](https://www.youtube.com/watch?v=eId6K8d0v6o)
3. Install [Arch Linux from the Microsoft Store](https://apps.microsoft.com/detail/9mznmnksm73x)

### Setup

1. Launch Arch Linux from the Start menu and create a user
2. Run the following setup script:

```bash
# System setup
sudo pacman -Syu base base-devel nano plasma
echo -e "C.UTF-8 UTF-8\nen_AU ISO-8859-1\nen_AU.UTF-8 UTF-8\nen_US ISO-8859-1\nen_US.UTF-8 UTF-8" | sudo tee /etc/locale.gen > /dev/null
sudo locale-gen

# Install daemonize (AUR dependency)
sudo curl -Lo PKGBUILD https://aur.archlinux.org/cgit/aur.git/plain/PKGBUILD?h=daemonize
makepkg -si
sudo rm PKGBUILD

# Install VcXsrv PKGBUILD (for X11 display)
sudo curl -Lo PKGBUILD https://gist.githubusercontent.com/rashil2000/f148d5fd207eb30c43f269dcd4f7c6fb/raw/73db1b0a14634c23b35a01168b5068aee877b74c/PKGBUILD-release
makepkg -si
sudo rm PKGBUILD

# Install yay (AUR helper)
sudo pacman -S --needed git base-devel && git clone https://aur.archlinux.org/yay.git && cd yay && makepkg -si
cd ~

# X11 display forwarding
sudo pacman -S xorg-xcalc
echo 'export DISPLAY=$(route.exe print | grep 0.0.0.0 | head -1 | awk "{print \$4}"):0.0' >> ~/.bashrc

# Project dependencies
sudo pacman -S git base-devel cmake clang gdb curl zip unzip jdk-openjdk
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25-tem
yay -S bolt-launcher gtk2 openssl-1.1 sdl2

# Clone and build
git clone --recursive https://gitlab.com/project-undercut/engine.git
cd engine
./gradlew build
```

> **Tip:** If you get "No Authorization protocol found" on XFCE or another X-based window manager:
> ```bash
> sudo pacman -S xorg-xhost
> xhost +local:
> ```
