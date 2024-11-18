package io.github.innobridge.llama.client;

import io.github.innobridge.llama.model.InferenceInput;

public interface LLMClient {
    String complete(InferenceInput input);
    LlamaIterable generate(InferenceInput input);
}
