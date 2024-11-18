package io.github.innobridge.llama.client;

import io.github.innobridge.llama.model.InferenceInput;
import io.github.innobridge.llama.model.GenerationCallback;

public interface LLMClient {
    String complete(InferenceInput input);
    LlamaIterable generate(InferenceInput input);
    LlamaIterable generate(InferenceInput input, GenerationCallback callback);
}
