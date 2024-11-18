package io.github.innobridge.llama.client;

import io.github.innobridge.llama.model.InferenceInput;
import io.github.innobridge.llama.model.GenerationCallback;
import io.github.innobridge.llama.client.LlamaIterator;
import io.github.innobridge.llama.client.LlamaOutput;
import java.util.Iterator;

/**
 * LlamaClient is the main entry point for interacting with the Llama model.
 * It provides methods for completing and generating text.
 */
public class LlamaClient implements LLMClient, AutoCloseable {

    private final LlamaModel model;

    public LlamaClient(LlamaModel model) {
        this.model = model;
    } 

    @Override
    public String complete(InferenceInput input) {
        InferenceParameters params = input.toInferenceParameters()
                                        .setStream(false);
        return model.complete(params);
    }

    @Override
    public LlamaIterable generate(InferenceInput input) {
        InferenceParameters params = input.toInferenceParameters()
                                        .setStream(true);
        return model.generate(params);
    }

    @Override
    public LlamaIterable generate(InferenceInput input, GenerationCallback callback) {
        InferenceParameters params = input.toInferenceParameters()
                                        .setStream(true);
        
        return new LlamaIterable() {
            @Override
            public LlamaIterator iterator() {
                LlamaIterator iterator = new LlamaIterator(model, params);
                new Thread(() -> {
                    try {
                        while (iterator.hasNext()) {
                            LlamaOutput output = iterator.next();
                            if (output != null) {
                                callback.onResponse(output.toString());
                            }
                        }
                        callback.onComplete();
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }).start();
                return iterator;
            }
        };
    }    

    @Override
    public void close() {
        model.close();
    }
}
