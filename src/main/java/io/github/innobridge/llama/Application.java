package io.github.innobridge.llama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "io.github.innobridge.llama.configuration.ApplicationSpecificSpringConfiguration",
})
public class Application {

  public static void main(String[] args) {
    ModelParameters modelParams = new ModelParameters()
        .setModelFilePath("/Users/yilengyao/.hugging_face/models/qwen2.5-coder-7b-instruct-q4_0.gguf")
        .setNGpuLayers(0);

    InferenceParameters inferParams = new InferenceParameters("What is capital of Neverland?")
        .setTemperature(0.7f)
        .setNPredict(48);

    try (LlamaModel model = new LlamaModel(modelParams)) {
      // Stream a response and access more information about each output.
      for (LlamaOutput output : model.generate(inferParams)) {
        System.out.print(output);
      }
      // Calculate a whole response before returning it.
      String response = model.complete(inferParams);
      System.out.println(response);
      // Returns the hidden representation of the context + prompt.
      // float[] embedding = model.embed("Embed this");
    }
    SpringApplication.run(Application.class, args);
  }

}
