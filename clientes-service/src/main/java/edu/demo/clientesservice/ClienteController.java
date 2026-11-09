package edu.demo.clientesservice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ClienteController {

    private static final List<Cliente> CLIENTES = List.of(
            new Cliente(1L, "45218763", "Carlos Alberto", "Mendoza Quispe", "carlos.mendoza@gmail.com", "+51 944 218 763", "Trujillo", "Av. Larco 1234, Urb. Los Pinos", "2025-03-14"),
            new Cliente(2L, "71936428", "Lucia Fernanda", "Paredes Vasquez", "lucia.paredes@outlook.com", "+51 987 654 321", "Chiclayo", "Calle Elias Aguirre 580", "2025-06-02"),
            new Cliente(3L, "08472915", "Jorge Luis", "Salazar Rojas", "jsalazar@empresa-salazar.pe", "+51 912 345 678", "Lima", "Av. Javier Prado Este 2310, San Isidro", "2024-11-21"),
            new Cliente(4L, "46173052", "Maria del Carmen", "Castillo Diaz", "mcastillo.diaz@gmail.com", "+51 976 102 884", "Trujillo", "Jr. Pizarro 455, Centro", "2025-08-09"),
            new Cliente(5L, "73820164", "Diego Armando", "Chavez Ruiz", "diego.chavez@hotmail.com", "+51 955 730 142", "Piura", "Av. Grau 1020", "2026-01-17"),
            new Cliente(6L, "42905371", "Andrea Valeria", "Torres Linares", "andrea.torres@gmail.com", "+51 933 481 906", "Arequipa", "Calle Mercaderes 312, Cercado", "2026-04-05"));

    @GetMapping("/clientes")
    public List<Cliente> listar(@RequestParam(required = false) String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            return CLIENTES;
        }
        return CLIENTES.stream()
                .filter(c -> c.ciudad().equalsIgnoreCase(ciudad))
                .toList();
    }

    @GetMapping("/clientes/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return CLIENTES.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado: " + id));
    }
}
