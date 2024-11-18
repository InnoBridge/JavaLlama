package io.github.innobridge.llama.client;

import io.github.innobridge.llama.model.InferenceInput;
import io.github.innobridge.llama.model.GenerationCallback;
import org.springframework.beans.factory.annotation.Autowired;

public class LlamaClient implements LLMClient, AutoCloseable {

    private final LlamaModel model;

    public LlamaClient(LlamaModel model) {
        this.model = model;
    } 

    @Override
    public String complete(InferenceInput input) {
        InferenceParameters params = createInferenceParameters(input);
        return model.complete(params);
    }

    @Override
    public LlamaIterable generate(InferenceInput input) {
        InferenceParameters params = createInferenceParameters(input);
        return model.generate(params);
    }

    public void generate(InferenceInput input, GenerationCallback callback) {
        InferenceParameters params = createInferenceParameters(input);
        try {
            LlamaIterable iterable = model.generate(params);
            for (LlamaOutput output : iterable) {
                callback.onResponse(output.toString()); 
            }
            callback.onComplete();
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private InferenceParameters createInferenceParameters(InferenceInput input) {
        return new InferenceParameters(input.prompt())
                .setCachePrompt(input.cachePrompt() != null ? input.cachePrompt() : true)
                .setNPredict(input.nPredict() != null ? input.nPredict() : 2048)
                .setTopK(input.topK() != null ? input.topK() : 40)
                .setTopP(input.topP() != null ? input.topP() : 0.95f)
                .setTemperature(input.temperature() != null ? input.temperature() : 0.8f)
                .setRepeatPenalty(input.repeatPenalty() != null ? input.repeatPenalty() : 1.1f)
                .setStream(input.stream() != null ? input.stream() : false);
    }

    @Override
    public void close() {
        model.close();
    }
}
