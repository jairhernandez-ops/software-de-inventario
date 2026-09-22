package validador;

import modelo.Producto;

/**
 * Reglas de validación para un Producto antes de ser persistido.
 * Extraído como clase independiente para poder probarlo con
 * pruebas unitarias sin necesidad de conexión a base de datos.
 */
public class ProductoValidator {

    public static boolean esValido(Producto p) {
        if (p == null) {
            return false;
        }
        if (p.getNombreProducto() == null || p.getNombreProducto().trim().isEmpty()) {
            return false;
        }
        if (p.getPrecio() <= 0) {
            return false;
        }
        if (p.getUsuariosIdUsuario() <= 0) {
            return false;
        }
        return true;
    }
}