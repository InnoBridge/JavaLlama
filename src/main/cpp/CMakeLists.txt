cmake_minimum_required(VERSION 3.14)
project(llama)

# Find JNI
find_package(JNI REQUIRED)

# Include directories
include_directories(
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_SOURCE_DIR}/build/_deps/json-src/include
    ${CMAKE_SOURCE_DIR}/build/_deps/llama.cpp-src
    ${CMAKE_SOURCE_DIR}/build/_deps/llama.cpp-src/common
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add common library from llama.cpp
add_library(llama_common STATIC IMPORTED)
set_target_properties(llama_common PROPERTIES
    IMPORTED_LOCATION "${CMAKE_BINARY_DIR}/_deps/llama.cpp-build/common/libcommon.a"
)

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
    llama_common
    nlohmann_json
    ${JNI_LIBRARIES}
)

# Handle Metal shader for macOS
if(APPLE)
    set(METAL_SHADER ${CMAKE_SOURCE_DIR}/build/_deps/llama.cpp-src/ggml-metal.metal)
    if(EXISTS ${METAL_SHADER})
        configure_file(${METAL_SHADER} ${CMAKE_CURRENT_BINARY_DIR}/ggml-metal.metal COPYONLY)
    endif()
endif()

# Set output directory
set_target_properties(jllama PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
)
