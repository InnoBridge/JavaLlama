package io.github.innobridge.llama.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.innobridge.llama.model.GenerationCallback;
import io.github.innobridge.llama.client.LlamaClient;
import io.github.innobridge.llama.model.InferenceInput;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

// @Component
// public class LlamaWebSocketHandler extends TextWebSocketHandler {

//     private final LlamaClient llamaClient;
//     private final ObjectMapper objectMapper;

//     public LlamaWebSocketHandler(LlamaClient llamaClient, ObjectMapper objectMapper) {
//         this.llamaClient = llamaClient;
//         this.objectMapper = objectMapper;
//     }

//     @Override
//     protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//         // Parse the incoming message as InferenceInput
//         InferenceInput input = objectMapper.readValue(message.getPayload(), InferenceInput.class);
        
//         // Generate tokens and send them back through the WebSocket
//         llamaClient.generate(input, new GenerationCallback() {
//             @Override
//             public void onResponse(String response) {
//                 try {
//                     session.sendMessage(new TextMessage(response));
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }

//             @Override
//             public void onComplete() {
//                 try {
//                     session.sendMessage(new TextMessage("[DONE]"));
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }

//             @Override
//             public void onError(Throwable t) {
//                 try {
//                     session.sendMessage(new TextMessage("[ERROR] " + t.getMessage()));
//                 } catch (Exception e) {
//                     e.printStackTrace();
//                 }
//             }
//         });
//     }
// }
