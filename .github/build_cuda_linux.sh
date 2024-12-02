#!/bin/sh

# Set Java 22 as the default for this build
export JAVA_HOME=/usr/lib/jvm/jdk-22
export PATH=$JAVA_HOME/bin:$PATH

exec .github/build.sh $@ -DGGML_CUDA=1 -DCMAKE_CUDA_COMPILER=/usr/bin/nvcc -DJAVA_HOME=/usr/lib/jvm/jdk-22
