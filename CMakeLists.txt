cmake_minimum_required(VERSION 3.14)

project(jllama CXX)

include(FetchContent)

set(BUILD_SHARED_LIBS ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

option(LLAMA_VERBOSE	"llama: verbose output"		OFF)

#################### json ####################

FetchContent_Declare(
	json
	GIT_REPOSITORY https://github.com/nlohmann/json
	GIT_TAG        v3.11.3
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

# Determine OS and architecture using CMake's built-in variables
if(APPLE)
    set(OS_NAME "Mac")
    if(CMAKE_SYSTEM_PROCESSOR STREQUAL "arm64")
        set(OS_ARCH "aarch64")
    else()
        set(OS_ARCH "x86_64")
    endif()
elseif(WIN32)
    set(OS_NAME "Windows")
    set(OS_ARCH "x86_64")
elseif(UNIX AND NOT APPLE)
    set(OS_NAME "Linux")
    if(CMAKE_SYSTEM_PROCESSOR STREQUAL "aarch64")
        set(OS_ARCH "aarch64")
    else()
        set(OS_ARCH "x86_64")
    endif()
else()
    message(FATAL_ERROR "Unsupported operating system")
endif()

message(STATUS "Building for ${OS_NAME} ${OS_ARCH}")

if(GGML_CUDA)
    set(JLLAMA_DIR ${CMAKE_SOURCE_DIR}/src/main/resources/io/github/innobridge/llama/client/${OS_NAME}/${OS_ARCH})
    message(STATUS "GPU (CUDA Linux) build - Installing files to ${JLLAMA_DIR}")
else()
    set(JLLAMA_DIR ${CMAKE_SOURCE_DIR}/src/main/resources/io/github/innobridge/llama/client/${OS_NAME}/${OS_ARCH})
    message(STATUS "CPU build - Installing files to ${JLLAMA_DIR}")
endif()

# Create output directories
file(MAKE_DIRECTORY ${JLLAMA_DIR})

# include jni.h and jni_md.h
if(NOT DEFINED JNI_INCLUDE_DIRS)
    # Skip Java version checks and use Java 22 directly
    set(Java_JAVA_EXECUTABLE "/usr/lib/jvm/jdk-22/bin/java")
    set(Java_JAVAC_EXECUTABLE "/usr/lib/jvm/jdk-22/bin/javac")
    set(JAVA_INCLUDE_PATH "/usr/lib/jvm/jdk-22/include")
    set(JAVA_INCLUDE_PATH2 "/usr/lib/jvm/jdk-22/include/linux")
    set(JNI_INCLUDE_DIRS "${JAVA_INCLUDE_PATH};${JAVA_INCLUDE_PATH2}")
endif()

# Add JNI include paths
include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})

add_library(jllama SHARED src/main/cpp/jllama.cpp src/main/cpp/server.hpp src/main/cpp/utils.hpp)

set_target_properties(jllama PROPERTIES POSITION_INDEPENDENT_CODE ON)
target_include_directories(jllama PRIVATE src/main/cpp ${JNI_INCLUDE_DIRS})
target_link_libraries(jllama PRIVATE common llama nlohmann_json)
target_compile_features(jllama PRIVATE cxx_std_11)

target_compile_definitions(jllama PRIVATE
    SERVER_VERBOSE=$<BOOL:${LLAMA_VERBOSE}>
)

if(OS_NAME STREQUAL "Windows")
	set_target_properties(jllama llama ggml PROPERTIES
	  RUNTIME_OUTPUT_DIRECTORY_DEBUG ${JLLAMA_DIR}
	  RUNTIME_OUTPUT_DIRECTORY_RELEASE ${JLLAMA_DIR}
	)
else()
	set_target_properties(jllama llama ggml PROPERTIES
	  LIBRARY_OUTPUT_DIRECTORY ${JLLAMA_DIR}
	)
endif()

if (LLAMA_METAL)
    # Create Metal shader directory if needed
    file(MAKE_DIRECTORY ${JLLAMA_DIR})
    # copy ggml-metal.metal to output directory
    configure_file(${llama.cpp_SOURCE_DIR}/ggml/src/ggml-metal.metal ${JLLAMA_DIR}/ggml-metal.metal COPYONLY)
    # Also copy to resources directory for development
    configure_file(${llama.cpp_SOURCE_DIR}/ggml/src/ggml-metal.metal ${CMAKE_SOURCE_DIR}/src/main/resources/ggml-metal.metal COPYONLY)
endif()
