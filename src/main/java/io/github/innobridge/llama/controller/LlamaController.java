package io.github.innobridge.llama.controller;

import io.github.innobridge.llama.client.LLMClient;
import io.github.innobridge.llama.client.LlamaOutput;
import io.github.innobridge.llama.client.LlamaIterable;
import io.github.innobridge.llama.model.InferenceInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
            @RequestParam(required = false) String[] stopStrings
    ) {
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
            @RequestParam(required = false) String[] stopStrings
    ) {
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
