package validador;

import modelo.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ProductoValidatorTest {

    // Caso normal: un producto con todos los datos bien puestos debería pasar
    @Test
    @DisplayName("Un producto con datos correctos debe ser válido")
    void productoValidoDebeAprobar() {
        Producto p = new Producto("Teclado mecánico", 150000, 1);
        assertTrue(ProductoValidator.esValido(p), "Un producto con datos correctos debería pasar la validación");
    }

    // Si el nombre viene vacío, no debería dejarlo pasar
    @Test
    @DisplayName("Un producto con nombre vacío debe ser rechazado")
    void nombreVacioDebeRechazar() {
        Producto p = new Producto("", 150000, 1);
        assertFalse(ProductoValidator.esValido(p), "El nombre vacío no debería ser aceptado");
    }

    // Lo mismo pero si el nombre es null en vez de vacío
    @Test
    @DisplayName("Un producto con nombre nulo debe ser rechazado")
    void nombreNuloDebeRechazar() {
        Producto p = new Producto(null, 150000, 1);
        assertFalse(ProductoValidator.esValido(p), "El nombre nulo no debería ser aceptado");
    }

    // Un precio negativo no tiene sentido para un producto
    @Test
    @DisplayName("Un producto con precio negativo debe ser rechazado")
    void precioNegativoDebeRechazar() {
        Producto p = new Producto("Mouse inalámbrico", -5000, 1);
        assertFalse(ProductoValidator.esValido(p), "Un precio negativo no debería ser aceptado");
    }

    // Precio en 0 tampoco debería ser válido
    @Test
    @DisplayName("Un producto con precio en cero debe ser rechazado")
    void precioCeroDebeRechazar() {
        Producto p = new Producto("Mouse inalámbrico", 0, 1);
        assertFalse(ProductoValidator.esValido(p), "Un precio de 0 no debería ser aceptado");
    }

    // Si no viene asociado a un usuario válido, tampoco debería guardarse
    @Test
    @DisplayName("Un producto sin usuario asociado debe ser rechazado")
    void usuarioInvalidoDebeRechazar() {
        Producto p = new Producto("Monitor 24 pulgadas", 500000, 0);
        assertFalse(ProductoValidator.esValido(p), "Un producto sin usuario_id válido no debería ser aceptado");
    }
}