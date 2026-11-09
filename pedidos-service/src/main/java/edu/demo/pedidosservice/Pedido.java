package edu.demo.pedidosservice;

import java.math.BigDecimal;
import java.util.List;

public record Pedido(
        Long id,
        String numero,
        Long clienteId,
        String fecha,
        String estado,
        String metodoPago,
        String direccionEnvio,
        List<Item> items,
        BigDecimal total,
        String moneda) {

    public record Item(Long productoId, String sku, String descripcion, int cantidad,
            BigDecimal precioUnitario, BigDecimal subtotal) {

        static Item de(Long productoId, String sku, String descripcion, int cantidad, String precio) {
            BigDecimal unitario = new BigDecimal(precio);
            return new Item(productoId, sku, descripcion, cantidad, unitario,
                    unitario.multiply(BigDecimal.valueOf(cantidad)));
        }
    }

    static Pedido de(Long id, Long clienteId, String fecha, String estado, String metodoPago,
            String direccionEnvio, Item... items) {
        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items) {
            total = total.add(item.subtotal());
        }
        return new Pedido(id, String.format("PED-2026-%04d", id), clienteId, fecha, estado, metodoPago,
                direccionEnvio, List.of(items), total, "PEN");
    }
}
