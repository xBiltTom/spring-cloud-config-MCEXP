package edu.demo.pedidosservice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PedidoController {

    private static final List<Pedido> PEDIDOS = List.of(
            Pedido.de(1L, 1L, "2026-10-12", "ENTREGADO", "TARJETA_CREDITO", "Av. Larco 1234, Urb. Los Pinos, Trujillo",
                    Pedido.Item.de(1L, "LAP-HP-15FD", "Laptop HP 15-fd0021la 15.6\" Core i5 16GB 512GB SSD", 1, "2499.00"),
                    Pedido.Item.de(4L, "MOU-LOG-M170", "Mouse inalambrico Logitech M170", 1, "49.90")),
            Pedido.de(2L, 4L, "2026-11-03", "ENVIADO", "YAPE", "Jr. Pizarro 455, Centro, Trujillo",
                    Pedido.Item.de(2L, "MON-LG-24MP", "Monitor LG 24MP400 24\" Full HD IPS", 2, "489.90"),
                    Pedido.Item.de(3L, "TEC-LOG-K380", "Teclado inalambrico Logitech K380 Bluetooth", 2, "159.00")),
            Pedido.de(3L, 3L, "2026-11-08", "PENDIENTE_PAGO", "TRANSFERENCIA", "Av. Javier Prado Este 2310, San Isidro, Lima",
                    Pedido.Item.de(6L, "IMP-EPS-L3250", "Impresora multifuncional Epson EcoTank L3250 WiFi", 1, "899.00")),
            Pedido.de(4L, 2L, "2026-11-06", "PAGADO", "TARJETA_DEBITO", "Calle Elias Aguirre 580, Chiclayo",
                    Pedido.Item.de(5L, "AUD-SON-CH520", "Audifonos Sony WH-CH520 Bluetooth", 1, "229.00"),
                    Pedido.Item.de(8L, "CAM-LOG-C920", "Webcam Logitech C920 HD Pro 1080p", 1, "329.00")),
            Pedido.de(5L, 5L, "2026-10-28", "CANCELADO", "PLIN", "Av. Grau 1020, Piura",
                    Pedido.Item.de(7L, "SSD-KIN-A400", "SSD Kingston A400 480GB SATA", 3, "139.00"),
                    Pedido.Item.de(4L, "MOU-LOG-M170", "Mouse inalambrico Logitech M170", 1, "49.90")));

    @GetMapping("/pedidos")
    public List<Pedido> listar(@RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado) {
        return PEDIDOS.stream()
                .filter(p -> clienteId == null || p.clienteId().equals(clienteId))
                .filter(p -> estado == null || p.estado().equalsIgnoreCase(estado))
                .toList();
    }

    @GetMapping("/pedidos/{id}")
    public Pedido obtener(@PathVariable Long id) {
        return PEDIDOS.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
    }
}
