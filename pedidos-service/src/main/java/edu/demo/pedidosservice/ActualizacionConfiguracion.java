package edu.demo.pedidosservice;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActualizacionConfiguracion {
    private static final Logger log = LoggerFactory.getLogger(ActualizacionConfiguracion.class);
    private final ContextRefresher refresher;

    public ActualizacionConfiguracion(ContextRefresher refresher) {
        this.refresher = refresher;
    }

    @Scheduled(fixedDelayString = "${CONFIG_REFRESH_INTERVAL_MS:15000}",
               initialDelayString = "${CONFIG_REFRESH_INTERVAL_MS:15000}")
    public void actualizar() {
        try {
            Set<String> changes = refresher.refresh();
            if (!changes.isEmpty()) {
                log.info("Configuración actualizada: {}", changes);
            }
        } catch (Exception ex) {
            log.warn("No se pudo actualizar la configuración; se intentará de nuevo: {}", ex.getMessage());
        }
    }
}
