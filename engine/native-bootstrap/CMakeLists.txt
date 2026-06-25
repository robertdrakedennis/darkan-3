cmake_minimum_required(VERSION 3.5)
project(undercutbootstrap CXX)

set(CMAKE_C_COMPILER_ID clang)
set(CMAKE_CXX_COMPILER_ID clang++)

set(CMAKE_CXX_STANDARD 20)
# -g -gdwarf-4: clean, gdb-readable debug info (gdb was choking on corrupt/mismatched
# DWARF, which also breaks addr2line on the in-process crash backtrace).
# Uncompressed debug sections avoid the "wrong version in unit header" parse errors.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -Wall -g -gdwarf-4 -fno-omit-frame-pointer")

find_package(JNI REQUIRED)
find_package(SDL2 REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(HAVE_FVISIBILITY_HIDDEN OFF CACHE BOOL "Have -fvisibility=hidden support" FORCE)
set(FUNCHOOK_BUILD_SHARED OFF CACHE BOOL "Build shared library" FORCE)
set(FUNCHOOK_BUILD_STATIC ON CACHE BOOL "Build static library" FORCE)

add_subdirectory(funchook)

add_library(imgui-static STATIC
    imgui/imgui.cpp
    imgui/imgui_draw.cpp
    imgui/imgui_tables.cpp
    imgui/imgui_widgets.cpp
    imgui/imgui_demo.cpp
    imgui/backends/imgui_impl_opengl3.cpp
    imgui/backends/imgui_impl_sdl2.cpp
)
target_include_directories(imgui-static PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/imgui
    ${CMAKE_CURRENT_SOURCE_DIR}/imgui/backends
)
target_link_libraries(imgui-static PUBLIC EGL OpenGL SDL2::SDL2)

add_library(undercutbootstrap SHARED undercut_bootstrap.cpp undercut_imgui.cpp undercut_imgui_inputprocessor.cpp)

find_package(Threads REQUIRED)
set(ADDITIONAL_LIBS ${CMAKE_THREAD_LIBS_INIT} dl)

# Export dynamic symbols so backtrace_symbols_fd() in the crash handler resolves
# our own frames to function names instead of bare module+offset.
target_link_options(undercutbootstrap PRIVATE -rdynamic -Wl,--compress-debug-sections=none)

if (UNIX AND NOT APPLE)
    target_link_libraries(undercutbootstrap PRIVATE "-Wl,--whole-archive" funchook-static "-Wl,--no-whole-archive" imgui-static ${ADDITIONAL_LIBS})
else()
    target_link_libraries(undercutbootstrap PRIVATE funchook-static imgui-static ${ADDITIONAL_LIBS})
endif()
