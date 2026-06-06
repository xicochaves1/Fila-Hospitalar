package com.hospital.fila.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração WebSocket com STOMP.
 *
 * Conceito de Sistemas Distribuídos:
 * - Comunicação assíncrona por eventos (pub/sub)
 * - Transparência de localização: painel recebe atualizações em tempo real
 *   sem precisar fazer polling a cada segundo
 * - Middleware de mensageria integrado ao Spring (broker embutido)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker simples embutido - em produção pode-se usar RabbitMQ ou Redis
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-fila")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Fallback para navegadores sem WebSocket nativo
    }
}
