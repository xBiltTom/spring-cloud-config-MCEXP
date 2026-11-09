package edu.demo.productosservice;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProductoController {

    private static final List<Producto> CATALOGO = List.of(
            new Producto(1L, "LAP-HP-15FD", "Laptop HP 15-fd0021la 15.6\" Core i5 16GB 512GB SSD", "HP", "Computo", new BigDecimal("2499.00"), "PEN", 14),
            new Producto(2L, "MON-LG-24MP", "Monitor LG 24MP400 24\" Full HD IPS", "LG", "Monitores", new BigDecimal("489.90"), "PEN", 32),
            new Producto(3L, "TEC-LOG-K380", "Teclado inalambrico Logitech K380 Bluetooth", "Logitech", "Perifericos", new BigDecimal("159.00"), "PEN", 58),
            new Producto(4L, "MOU-LOG-M170", "Mouse inalambrico Logitech M170", "Logitech", "Perifericos", new BigDecimal("49.90"), "PEN", 120),
            new Producto(5L, "AUD-SON-CH520", "Audifonos Sony WH-CH520 Bluetooth", "Sony", "Audio", new BigDecimal("229.00"), "PEN", 27),
            new Producto(6L, "IMP-EPS-L3250", "Impresora multifuncional Epson EcoTank L3250 WiFi", "Epson", "Impresion", new BigDecimal("899.00"), "PEN", 9),
            new Producto(7L, "SSD-KIN-A400", "SSD Kingston A400 480GB SATA", "Kingston", "Almacenamiento", new BigDecimal("139.00"), "PEN", 64),
            new Producto(8L, "CAM-LOG-C920", "Webcam Logitech C920 HD Pro 1080p", "Logitech", "Perifericos", new BigDecimal("329.00"), "PEN", 18));

    @GetMapping("/productos")
    public List<Producto> listar(@RequestParam(required = false) String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return CATALOGO;
        }
        return CATALOGO.stream()
                .filter(p -> p.categoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @GetMapping("/productos/{id}")
    public Producto obtener(@PathVariable Long id) {
        return CATALOGO.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
    }
}
