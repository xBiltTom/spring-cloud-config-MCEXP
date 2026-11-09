package edu.demo.pedidosservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class MensajeController {
    @Value("${mensaje}")
    private String mensaje;

    @GetMapping("/mensaje")
    public String mensaje() {
        return mensaje;
    }
}
