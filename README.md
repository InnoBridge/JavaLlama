# JavaLlama
This library allows you to run LLaMA models natively in Java.
It does this using the [llama-cpp](https://github.com/ggerganov/llama.cpp) library.

A more detailed description can be found [Run LLMs Natively in your Java Application
](https://www.linkedin.com/pulse/run-llms-natively-your-java-application-yi-leng-yao-brpnc/?trackingId=4WCES9EnTHGkGd7U%2FZt12w%3D%3D)

## Usage

### Downloading the model
Install HuggingFace Cli

```
pip install -U huggingface_hub
```

Downloading the model, replace <llm model> with a specific model name.

```
huggingface-cli download <llm model>
```

Example

```
huggingface-cli download Qwen/Qwen2.5-Coder-7B-Instruct-GGUF --include "qwen2.5-coder-7b-instruct-q4_0*.gguf" --local-dir . --local-dir-use-symlinks False
```

### Setting up Java Application [Demo](https://github.com/InnoBridge/JavaLlamaDemo)
Add the Innobridge llama to your pom.xml

```
<dependency>
    <groupId>io.github.innobridge</groupId>
    <artifactId>llama</artifactId>
    <version>0.0.4</version>
</dependency>
```

Set up LLMConfiguration file.

```
@Configuration
public class LLMConfiguration {
  @Bean
  public LLMClient llamaClient(
      @Value("${model.file.path}") String modelFilepath,
      @Value("${model.gpu.layers}") Integer nGpuLayers) {
    System.out.println("Creating LlamaClient with modelFilepath: " + modelFilepath + " and nGpuLayers: " + nGpuLayers);
    LlamaModel model = new LlamaModel(new ModelParameters()
        .setModelFilePath(modelFilepath)
        .setNGpuLayers(nGpuLayers));
    return new LlamaClient(model);
  }
}
```

Add the path of your model to application.properties
```
model.file.path=/Users/yilengyao/.hugging_face/models/qwen2.5-coder-7b-instruct-q4_0.gguf
model.gpu.layers=0
```

Note: model.gpu.layers is set to 0 because my computer does not have cpu so the application is only using cpu.
Create LLMController

```
@RestController
@RequestMapping("/api/llama")
public class LlamaController {
  @Autowired
  private LLMClient lLMClient;
  @PostMapping("/complete")
  public String complete(
      @RequestParam(required = true) String prompt,
      @RequestParam(required = false, defaultValue = "0.7") float temperature,
      @RequestParam(required = false, defaultValue = "40") int topK,
      @RequestParam(required = false, defaultValue = "0.9") float topP,
      @RequestParam(required = false, defaultValue = "256") int nPredict,
      @RequestParam(required = false) String[] stopStrings) {
    InferenceInput.Builder builder = InferenceInput.builder()
        .prompt(prompt)
        .temperature(temperature)
        .topK(topK)
        .topP(topP)
        .nPredict(nPredict);
    if (stopStrings != null && stopStrings.length > 0) {
      builder.stopStrings(stopStrings);
    }
    return lLMClient.complete(builder.build());
  }
  @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter generateStream(
      @RequestParam(required = true) String prompt,
      @RequestParam(required = false, defaultValue = "0.7") float temperature,
      @RequestParam(required = false, defaultValue = "40") int topK,
      @RequestParam(required = false, defaultValue = "0.9") float topP,
      @RequestParam(required = false, defaultValue = "256") int nPredict,
      @RequestParam(required = false) String[] stopStrings) {
    SseEmitter emitter = new SseEmitter(300000L); // 5 minute timeout
    Thread generationThread = new Thread(() -> {
      try {
        InferenceInput.Builder builder = InferenceInput.builder()
            .prompt(prompt)
            .temperature(temperature)
            .topK(topK)
            .topP(topP)
            .nPredict(nPredict);
        if (stopStrings != null && stopStrings.length > 0) {
          builder.stopStrings(stopStrings);
        }
        // Use the asynchronous generate method for true streaming
        LlamaIterable<LlamaOutput> outputs = lLMClient.generate(builder.build());
        for (LlamaOutput output : outputs) {
          emitter.send(SseEmitter.event().data(output.toString()));
        }
        emitter.complete();
      } catch (Exception e) {
        try {
          emitter.completeWithError(e);
        } catch (Exception ex) {
        }
      }
    });
    generationThread.setName("LLM-Generation-Thread");
    generationThread.start();
    return emitter;
  }
}

## Building from Source

### GPU Build (CUDA)

To build with GPU support:

1. Build CUDA libraries:
```bash
sh .github/build_cuda_linux.sh
```

2. Build the project:
```bash
./mvnw clean install -Dgpg.skip
```

This will compile the project with CUDA support for GPU acceleration.

### Running the application
```
./mvnw spring-boot:run
```

## Changelog
- 0.0.4: Initial release Completion and Generate Endpoints, supports MacOS Arm64, Linux Arm64, Linux x86_64(havent tested yet)