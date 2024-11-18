package io.github.innobridge.llama.controller;

import io.github.innobridge.llama.client.LLMClient;
import io.github.innobridge.llama.model.GenerationCallback;
import io.github.innobridge.llama.client.LlamaOutput;
import io.github.innobridge.llama.client.LlamaClient;
import io.github.innobridge.llama.client.LlamaIterable;
import io.github.innobridge.llama.model.InferenceInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/llama")
public class LlamaController {

    @Autowired
    private LLMClient lLMClient;

    @PostMapping("/complete")
    public String complete(
            @RequestParam(required = true) String prompt

    ) {
        InferenceInput input = new InferenceInput()
                .prompt(prompt)
                .
        return lLMClient.complete(input);
    }
    
@PostMapping("/generate")
public Iterable<String> generate(@RequestBody InferenceInput input) {
    LlamaIterable llamaIterable = lLMClient.generate(input);
    return StreamSupport.stream(llamaIterable.spliterator(), false)
        .map(LlamaOutput::toString)
        .collect(Collectors.toList());
}

//    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter generateStream(@RequestBody InferenceInput input) {
//        SseEmitter emitter = new SseEmitter();
//
//        lLMClient.generate(input, new GenerationCallback() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    emitter.send(response);
//                } catch (IOException e) {
//                    emitter.completeWithError(e);
//                }
//            }
//
//            @Override
//            public void onComplete() {
//                emitter.complete();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                emitter.completeWithError(t);
//            }
//        });
        
//        return emitter;
//    }
}
