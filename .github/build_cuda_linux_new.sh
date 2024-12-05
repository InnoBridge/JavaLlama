#!/bin/sh

# Set Java 22 as the default for this build
export JAVA_HOME=/usr/lib/jvm/jdk-22
export PATH=$JAVA_HOME/bin:$PATH

# Set CUDA environment variables
export CUDA_HOME=/usr/local/cuda-12.1
export PATH=$CUDA_HOME/bin:$PATH
export LD_LIBRARY_PATH=$CUDA_HOME/lib64:$LD_LIBRARY_PATH

# Add flags for GCC 12.3.0 compatibility
export CUDAFLAGS="-allow-unsupported-compiler"

exec .github/build.sh $@ -DGGML_CUDA=1 -DCMAKE_CUDA_COMPILER=/usr/local/cuda-12.1/bin/nvcc -DJAVA_HOME=/usr/lib/jvm/jdk-22 -DCMAKE_CUDA_FLAGS="-allow-unsupported-compiler"
