cmake_minimum_required(VERSION 3.14)
project(jllama CXX)

include(FetchContent)

set(BUILD_SHARED_LIBS ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

option(LLAMA_VERBOSE "llama: verbose output" OFF)

#################### json ####################

set(JSON_BuildTests OFF CACHE INTERNAL "")
set(JSON_Install OFF CACHE INTERNAL "")

FetchContent_Declare(json
    URL https://github.com/nlohmann/json/releases/download/v3.11.3/json.tar.xz
    URL_HASH SHA256=d6c65aca6b1ed68e7a182f4757257b107ae403032760ed6ef121c9d55e81757d
)
FetchContent_MakeAvailable(json)

#################### llama.cpp ####################

FetchContent_Declare(
    llama.cpp
    GIT_REPOSITORY https://github.com/ggerganov/llama.cpp.git
    GIT_TAG        b3534
)
FetchContent_MakeAvailable(llama.cpp)

#################### jllama ####################

# Detect OS and architecture
if(APPLE)
    set(OS_NAME "Mac")
    if(CMAKE_SYSTEM_PROCESSOR STREQUAL "arm64")
        set(OS_ARCH "aarch64")
    else()
        set(OS_ARCH "x86_64")
    endif()
elseif(WIN32)
    set(OS_NAME "Windows")
    if(CMAKE_SYSTEM_PROCESSOR STREQUAL "AMD64")
        set(OS_ARCH "x86_64")
    else()
        message(FATAL_ERROR "Unsupported Windows architecture: ${CMAKE_SYSTEM_PROCESSOR}")
    endif()
elseif(UNIX AND NOT APPLE)
    set(OS_NAME "Linux")
    if(CMAKE_SYSTEM_PROCESSOR MATCHES "^(x86_64|amd64)$")
        set(OS_ARCH "x86_64")
    elseif(CMAKE_SYSTEM_PROCESSOR MATCHES "^(aarch64|arm64)$")
        set(OS_ARCH "aarch64")
    else()
        message(FATAL_ERROR "Unsupported Linux architecture: ${CMAKE_SYSTEM_PROCESSOR}")
    endif()
else()
    message(FATAL_ERROR "Unsupported operating system")
endif()

message(STATUS "Building for ${OS_NAME} ${OS_ARCH}")

if(GGML_CUDA)
    set(JLLAMA_DIR ${CMAKE_SOURCE_DIR}/src/main/resources_linux_cuda/io/github/innobridge/llama/client/${OS_NAME}/${OS_ARCH})
    message(STATUS "GPU (CUDA Linux) build - Installing files to ${JLLAMA_DIR}")
else()
    set(JLLAMA_DIR ${CMAKE_SOURCE_DIR}/src/main/resources/io/github/innobridge/llama/client/${OS_NAME}/${OS_ARCH})
    message(STATUS "CPU build - Installing files to ${JLLAMA_DIR}")
endif()

# include jni.h and jni_md.h
if(NOT DEFINED JNI_INCLUDE_DIRS)
    if(OS_NAME MATCHES "^Linux" OR OS_NAME STREQUAL "Mac")
        set(JNI_INCLUDE_DIRS .github/include/unix)
    elseif(OS_NAME STREQUAL "Windows")
        set(JNI_INCLUDE_DIRS .github/include/windows)
    else()
        find_package(Java REQUIRED)
        find_program(JAVA_EXECUTABLE NAMES java)
        find_path(JNI_INCLUDE_DIRS NAMES jni.h HINTS ENV JAVA_HOME PATH_SUFFIXES include)
        file(GLOB_RECURSE JNI_MD_PATHS RELATIVE "${JNI_INCLUDE_DIRS}" "${JNI_INCLUDE_DIRS}/**/jni_md.h")
        foreach(PATH IN LISTS JNI_MD_PATHS)
            get_filename_component(DIR ${PATH} DIRECTORY)
            list(APPEND JNI_INCLUDE_DIRS "${JNI_INCLUDE_DIRS}/${DIR}")
        endforeach()
    endif()
endif()
if(NOT JNI_INCLUDE_DIRS)
    message(FATAL_ERROR "Could not determine JNI include directories")
endif()

# Add JNI include paths
if(APPLE)
    include_directories(
        "/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home/include"
        "/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home/include/darwin"
    )
endif()

# Build the native libraries
add_subdirectory(src/main/cpp)

# Set rpath for macOS
if(APPLE)
    set_target_properties(jllama PROPERTIES
        INSTALL_RPATH "@loader_path"
        BUILD_WITH_INSTALL_RPATH TRUE
    )
endif()

# Install libraries and resources
install(
    TARGETS jllama
    LIBRARY DESTINATION ${JLLAMA_DIR}
    RUNTIME DESTINATION ${JLLAMA_DIR}
)

install(
    TARGETS ggml llama
    LIBRARY DESTINATION ${JLLAMA_DIR}
    RUNTIME DESTINATION ${JLLAMA_DIR}
)

if(APPLE)
    install(
        FILES ${CMAKE_BINARY_DIR}/src/main/cpp/ggml-metal.metal
        DESTINATION ${JLLAMA_DIR}
    )
endif()

install(TARGETS ggml llama
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
