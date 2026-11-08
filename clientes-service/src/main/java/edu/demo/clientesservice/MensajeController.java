package edu.demo.clientesservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MensajeController {
    @Value("${mensaje}")
    private String mensaje;

    @GetMapping("/mensaje")
    public String mensaje() {
        return mensaje;
    }
}
