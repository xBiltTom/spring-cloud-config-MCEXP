package edu.demo.configserver;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GithubWebhookController {
    private final EventosConfiguracion eventos;
    private final String secret;

    public GithubWebhookController(EventosConfiguracion eventos,
            @Value("${GITHUB_WEBHOOK_SECRET:}") String secret) {
        this.eventos = eventos;
        this.secret = secret;
    }

    @PostMapping("/github-webhook")
    public ResponseEntity<Void> recibir(@RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody byte[] body) throws GeneralSecurityException {
        if (secret.isBlank()) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        if (!firmaValida(body, signature)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if ("push".equals(event)) eventos.notificar();
        return ResponseEntity.accepted().build();
    }

    private boolean firmaValida(byte[] body, String signature) throws GeneralSecurityException {
        if (signature == null || !signature.startsWith("sha256=")) return false;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String expected = "sha256=" + HexFormat.of().formatHex(mac.doFinal(body));
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.US_ASCII),
                signature.getBytes(StandardCharsets.US_ASCII));
    }
}
