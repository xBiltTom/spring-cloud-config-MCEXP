package edu.demo.productosservice;

import java.math.BigDecimal;

public record Producto(
        Long id,
        String sku,
        String nombre,
        String marca,
        String categoria,
        BigDecimal precio,
        String moneda,
        int stock) {
}
