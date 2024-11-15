package io.github.innobridge.llama;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.args.MiroStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootApplication(
		scanBasePackages = {
				"io.github.innobridge.llama.configuration.ApplicationSpecificSpringConfiguration",
		}
)
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
//			float[] embedding = model.embed("Embed this");
		}

//		ModelParameters modelParams = new ModelParameters()
//				.setModelFilePath("/Users/yilengyao/.hugging_face/models/qwen2.5-coder-14b-instruct-q4_0.gguf")
//				.setNGpuLayers(43);
//
//		String system = "This is a conversation between User and Llama, a friendly chatbot.\n" +
//				"Llama is helpful, kind, honest, good at writing, and never fails to answer any " +
//				"requests immediately and with precision.\n";
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
//		try (LlamaModel model = new LlamaModel(modelParams)) {
//			System.out.print(system);
//			String prompt = system;
//			while (true) {
//				prompt += "\nUser: ";
//				System.out.print("\nUser: ");
//				String input = reader.readLine();
//				prompt += input;
//				System.out.print("Llama: ");
//				prompt += "\nLlama: ";
//				InferenceParameters inferParams = new InferenceParameters(prompt)
//						.setTemperature(0.7f)
//						.setPenalizeNl(true)
//						.setMiroStat(MiroStat.V1);
//				for (LlamaOutput output : model.generate(inferParams)) {
//					System.out.print(output);
//					prompt += output;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("hello world");
		SpringApplication.run(Application.class, args);
	}

}
