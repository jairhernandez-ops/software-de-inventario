package validador;

import modelo.Producto;

/**
 * Reglas de validación para un Producto antes de guardarlo en la base de datos.
 * Lo dejé como una clase aparte para poder probarlo con JUnit sin necesitar la conexión a MySQL.
 */
public class ProductoValidator {

    public static boolean esValido(Producto p) {
        if (p == null) {
            return false;
        }
        // El nombre no puede ser nulo ni estar vacío
        if (p.getNombreProducto() == null || p.getNombreProducto().trim().isEmpty()) {
            return false;
        }
        // El precio tiene que ser mayor a 0
        if (p.getPrecio() <= 0) {
            return false;
        }
        // La cantidad no puede ser negativa (0 sí se permite, significa que no hay unidades en bodega)
        if (p.getCantidad() < 0) {
            return false;
        }
        // El producto tiene que estar asociado a un usuario válido
        if (p.getUsuariosIdUsuario() <= 0) {
            return false;
        }
        return true;
    }
}