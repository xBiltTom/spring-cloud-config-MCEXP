package edu.demo.configserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GithubWebhookControllerTests {
    private static final byte[] BODY = "{\"ref\":\"refs/heads/main\"}".getBytes(StandardCharsets.UTF_8);
    private static final String SECRET = "secret-for-tests";

    @Test
    void notificaSoloPushConFirmaValida() throws Exception {
        EventosConfiguracion eventos = mock(EventosConfiguracion.class);
        GithubWebhookController controller = new GithubWebhookController(eventos, SECRET);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = "sha256=" + HexFormat.of().formatHex(mac.doFinal(BODY));

        assertEquals(HttpStatus.ACCEPTED, controller.recibir("ping", signature, BODY).getStatusCode());
        verify(eventos, never()).notificar();
        assertEquals(HttpStatus.ACCEPTED, controller.recibir("push", signature, BODY).getStatusCode());
        verify(eventos).notificar();
    }

    @Test
    void rechazaFirmaInvalidaOSecretoAusente() throws Exception {
        EventosConfiguracion eventos = mock(EventosConfiguracion.class);
        GithubWebhookController controller = new GithubWebhookController(eventos, SECRET);
        assertEquals(HttpStatus.FORBIDDEN, controller.recibir("push", null, BODY).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, controller.recibir("push", "sha256=incorrecta", BODY).getStatusCode());
        GithubWebhookController sinSecreto = new GithubWebhookController(eventos, "");
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, sinSecreto.recibir("push", null, BODY).getStatusCode());
        verify(eventos, never()).notificar();
    }
}
