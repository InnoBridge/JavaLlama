cmake_minimum_required(VERSION 3.14)
project(llama)

# Include FetchContent
include(FetchContent)

# Set llama.cpp build options
set(LLAMA_STATIC ON CACHE BOOL "Build llama.cpp as static library")
set(BUILD_SHARED_LIBS OFF CACHE BOOL "Build shared libraries")
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "Build llama.cpp examples")
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "Build llama.cpp tests")
set(LLAMA_NATIVE OFF CACHE BOOL "Build with native CPU optimizations")

# Detect architecture
if(APPLE)
    execute_process(
        COMMAND uname -m
        OUTPUT_VARIABLE ARCH
        OUTPUT_STRIP_TRAILING_WHITESPACE
    )
    if(ARCH STREQUAL "arm64")
        set(CMAKE_OSX_ARCHITECTURES "arm64")
    else()
        set(CMAKE_OSX_ARCHITECTURES "x86_64")
    endif()
endif()

# Fetch llama.cpp
FetchContent_Declare(
    llama.cpp
    GIT_REPOSITORY https://github.com/ggerganov/llama.cpp.git
    GIT_TAG master
)
FetchContent_MakeAvailable(llama.cpp)

# Fetch nlohmann/json
FetchContent_Declare(
    json
    URL https://github.com/nlohmann/json/releases/download/v3.11.3/json.tar.xz
)
FetchContent_MakeAvailable(json)

# Find JNI
find_package(JNI REQUIRED)

# Include directories
include_directories(
    ${JNI_INCLUDE_DIRS}
    ${json_SOURCE_DIR}/include
    ${llama.cpp_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add jllama library
add_library(jllama SHARED
    jllama.cpp
)

# Set library suffix based on platform
if(WIN32)
    set_target_properties(jllama PROPERTIES SUFFIX ".dll")
elseif(APPLE)
    set_target_properties(jllama PROPERTIES SUFFIX ".dylib")
else()
    set_target_properties(jllama PROPERTIES SUFFIX ".so")
endif()

# Link libraries
target_link_libraries(jllama PRIVATE
    llama
    nlohmann_json::nlohmann_json
    ${JNI_LIBRARIES}
)

# Add compile definitions
target_compile_definitions(jllama PRIVATE
    GGML_USE_K_QUANTS
)

# Handle Metal shader for macOS
if(APPLE)
    set(METAL_SHADER ${llama.cpp_SOURCE_DIR}/ggml-metal.metal)
    if(EXISTS ${METAL_SHADER})
        add_custom_command(
            TARGET jllama POST_BUILD
            COMMAND ${CMAKE_COMMAND} -E copy
                ${METAL_SHADER}
                ${CMAKE_SOURCE_DIR}/../resources/ggml-metal.metal
        )
    endif()
endif()

# Set output directory
set_target_properties(jllama PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
)
