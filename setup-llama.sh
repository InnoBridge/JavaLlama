#!/bin/bash

set -e

# Directory setup
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$SCRIPT_DIR/lib"
BUILD_DIR="$SCRIPT_DIR/llama.cpp/build"
LLAMA_REPO="https://github.com/ggerganov/llama.cpp.git"

# Create directories
mkdir -p "$LIB_DIR"

# Clone and build llama.cpp if needed
if [ ! -d "$SCRIPT_DIR/llama.cpp" ]; then
    echo "Cloning llama.cpp repository..."
    git clone "$LLAMA_REPO"
fi

# Build llama.cpp
cd "$SCRIPT_DIR/llama.cpp"
git pull
mkdir -p build
cd build

# Configure with proper flags for stable CPU build
cmake .. \
    -DBUILD_SHARED_LIBS=ON \
    -DLLAMA_METAL=ON \
    -DLLAMA_BLAS=ON \
    -DCMAKE_OSX_ARCHITECTURES="arm64;x86_64"
cmake --build . --config Release

echo "Copying libraries to $LIB_DIR..."

# Copy all dylib files
find . -name "*.dylib" -exec cp {} "$LIB_DIR/" \;

# Copy Metal shader if it exists
if [ -f "ggml-metal.metal" ]; then
    cp "ggml-metal.metal" "$LIB_DIR/"
fi

# Update library paths
cd "$LIB_DIR"

# Fix all dylib paths
for lib in *.dylib; do
    # Get all dependencies
    otool -L "$lib" | grep "@rpath" | while read -r line; do
        dep=$(echo "$line" | awk '{print $1}')
        dep_name=$(basename "$dep")
        if [ -f "$LIB_DIR/$dep_name" ]; then
            echo "Fixing $dep in $lib"
            install_name_tool -change "$dep" "@loader_path/$dep_name" "$lib"
        fi
    done
done

echo "Library setup completed successfully!"
ls -l "$LIB_DIR"