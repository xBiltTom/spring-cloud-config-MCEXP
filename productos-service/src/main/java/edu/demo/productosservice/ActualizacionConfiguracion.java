package edu.demo.productosservice;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "config.events.enabled", havingValue = "true", matchIfMissing = true)
public class ActualizacionConfiguracion implements WebSocket.Listener {
    private static final Logger log = LoggerFactory.getLogger(ActualizacionConfiguracion.class);
    private final ContextRefresher refresher;
    private final URI eventsUri;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "config-events");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicReference<WebSocket> connection = new AtomicReference<>();
    private final AtomicBoolean connecting = new AtomicBoolean();
    private final AtomicBoolean stopping = new AtomicBoolean();
    private final AtomicBoolean retryScheduled = new AtomicBoolean();
    private final StringBuilder message = new StringBuilder();
    private volatile long lastHeartbeat;

    public ActualizacionConfiguracion(ContextRefresher refresher,
            @Value("${CONFIG_SERVER_URL:http://localhost:8888}") String serverUrl) {
        this.refresher = refresher;
        URI base = URI.create(serverUrl);
        String scheme = switch (base.getScheme()) {
            case "http" -> "ws";
            case "https" -> "wss";
            default -> throw new IllegalArgumentException("CONFIG_SERVER_URL debe usar http o https");
        };
        String path = base.getPath().replaceAll("/+$", "") + "/config-events";
        this.eventsUri = URI.create(scheme + "://" + base.getRawAuthority() + path);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void iniciar() {
        executor.scheduleWithFixedDelay(this::mantenerConexion, 0, 5, TimeUnit.SECONDS);
    }

    private void mantenerConexion() {
        if (stopping.get()) return;
        WebSocket current = connection.get();
        if (current != null) {
            if (System.nanoTime() - lastHeartbeat > TimeUnit.MINUTES.toNanos(4)) {
                current.sendText("heartbeat", true).exceptionally(error -> {
                    desconectar(current);
                    return null;
                });
                lastHeartbeat = System.nanoTime();
            }
            return;
        }
        if (!connecting.compareAndSet(false, true)) return;
        client.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(10))
                .buildAsync(eventsUri, this).whenComplete((socket, error) -> {
                    connecting.set(false);
                    if (error != null && !stopping.get()) {
                        log.warn("No se pudo conectar a eventos; se reintentará: {}", error.getMessage());
                    }
                });
    }

    @Override
    public void onOpen(WebSocket socket) {
        if (stopping.get()) {
            socket.abort();
            return;
        }
        connection.set(socket);
        lastHeartbeat = System.nanoTime();
        log.info("Conectado a eventos de configuración");
        // Reconsultar al conectar recupera cambios que ocurrieron durante una desconexión.
        ejecutarActualizacion();
        socket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
        message.append(data);
        if (last) {
            if ("config-changed".contentEquals(message)) ejecutarActualizacion();
            message.setLength(0);
        }
        socket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket socket, int status, String reason) {
        desconectar(socket);
        return null;
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        desconectar(socket);
    }

    private void desconectar(WebSocket socket) {
        if (connection.compareAndSet(socket, null)) {
            socket.abort();
            if (!stopping.get()) log.warn("Conexión de eventos interrumpida; se reintentará");
        }
    }

    private void ejecutarActualizacion() {
        if (!stopping.get()) executor.execute(this::actualizar);
    }

    private void actualizar() {
        try {
            Set<String> changes = refresher.refresh();
            if (!changes.isEmpty()) log.info("Configuración actualizada: {}", changes);
        } catch (Exception ex) {
            log.warn("No se pudo actualizar la configuración; se reintentará: {}", ex.getMessage());
            if (!stopping.get() && retryScheduled.compareAndSet(false, true)) {
                executor.schedule(() -> {
                    retryScheduled.set(false);
                    actualizar();
                }, 5, TimeUnit.SECONDS);
            }
        }
    }

    @PreDestroy
    public void detener() {
        stopping.set(true);
        WebSocket socket = connection.getAndSet(null);
        if (socket != null) socket.abort();
        executor.shutdownNow();
    }
}
