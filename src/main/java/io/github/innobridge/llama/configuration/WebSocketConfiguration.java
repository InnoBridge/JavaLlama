package io.github.innobridge.llama.configuration;

import io.github.innobridge.llama.websocket.LlamaWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//@Configuration
//@EnableWebSocket
//public class WebSocketConfiguration implements WebSocketConfigurer {

//    private final LlamaWebSocketHandler llamaWebSocketHandler;

//    public WebSocketConfig(LlamaWebSocketHandler llamaWebSocketHandler) {
//        this.llamaWebSocketHandler = llamaWebSocketHandler;
//    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(llamaWebSocketHandler, "/ws/llama")
//               .setAllowedOrigins("*");  // Configure CORS as needed
//    }
//}
