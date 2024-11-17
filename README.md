# JavaLlama

## Downloading the model
Install HuggingFace Cli
```text
pip install -U huggingface_hub
```

Create a directory for the model, then cd into it.

Downloading the model
```text
huggingface-cli download Qwen/Qwen2.5-Coder-7B-Instruct-GGUF --include "qwen2.5-coder-7b-instruct-q4_0*.gguf" --local-dir . --local-dir-use-symlinks False\n
```

## Compiling
```text
brew install cmake
```
```text
<!-- cmake -B build -->
<!-- ```. -->

<!-- ```text -->
<!-- ./mvnw clean compile -->
```

```
  ```
mvn compile  # don't forget this line
cmake -B build # add any other arguments for your backend, e.g. -DGGML_CUDA=ON
cmake --build build --config Release
```


Not starting the server
```text
./mvnw spring-boot:run
```
