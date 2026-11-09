package edu.demo.clientesservice;

public record Cliente(
        Long id,
        String dni,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        String ciudad,
        String direccion,
        String fechaRegistro) {
}
