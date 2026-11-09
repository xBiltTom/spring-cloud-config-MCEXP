package edu.demo.configserver;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class EventosConfiguracion implements WebSocketConfigurer {
    private static final Logger log = LoggerFactory.getLogger(EventosConfiguracion.class);
    private final Notificador notificador = new Notificador();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificador, "/config-events");
    }

    void notificar() {
        notificador.notificar();
    }

    private static class Notificador extends TextWebSocketHandler {
        private final Set<WebSocketSession> sesiones = ConcurrentHashMap.newKeySet();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            sesiones.add(session);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            sesiones.remove(session);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable error) throws IOException {
            sesiones.remove(session);
            if (session.isOpen()) session.close(CloseStatus.SERVER_ERROR);
        }

        void notificar() {
            for (WebSocketSession session : sesiones) {
                try {
                    synchronized (session) {
                        if (session.isOpen()) session.sendMessage(new TextMessage("config-changed"));
                    }
                } catch (IOException ex) {
                    sesiones.remove(session);
                    log.warn("No se pudo notificar a un cliente WebSocket: {}", ex.getMessage());
                    try { session.close(CloseStatus.SERVER_ERROR); } catch (IOException ignored) { }
                }
            }
        }
    }
}
