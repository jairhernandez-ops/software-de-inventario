package dao;

import conexion.Conexion;
import modelo.Producto;
import validador.ProductoValidator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto p) {
        // Antes de tocar la base de datos, reviso que el producto tenga datos válidos
        if (!ProductoValidator.esValido(p)) {
            System.out.println("Producto inválido: no se realizó la inserción (nombre vacío, precio <= 0, cantidad negativa o usuario inválido)");
            return false;
        }
        // La tabla tiene dos columnas de usuario (id_usuario y usuarios_id_usuario) y las dos son obligatorias,
        // así que en las dos guardo el mismo id del usuario
        String sql = "INSERT INTO Productos (nombre_producto, precio, cantidad, id_usuario, usuarios_id_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombreProducto().trim());
                ps.setDouble(2, p.getPrecio());
                ps.setInt(3, p.getCantidad());
                ps.setInt(4, p.getUsuariosIdUsuario());
                ps.setInt(5, p.getUsuariosIdUsuario());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listarPorUsuario(int idUsuario) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, nombre_producto, precio, cantidad, usuarios_id_usuario FROM Productos WHERE usuarios_id_usuario = ? ORDER BY id_producto";
        try (Connection conn = Conexion.conectar()) {
            if (conn == null) {
                return lista;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Producto p = new Producto();
                        p.setIdProducto(rs.getInt("id_producto"));
                        p.setNombreProducto(rs.getString("nombre_producto"));
                        p.setPrecio(rs.getDouble("precio"));
                        p.setCantidad(rs.getInt("cantidad"));
                        p.setUsuariosIdUsuario(rs.getInt("usuarios_id_usuario"));
                        lista.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Producto p) {
        // Valido igual que al insertar, para que no se pueda guardar un dato malo editando
        if (!ProductoValidator.esValido(p)) {
            System.out.println("Producto inválido: no se realizó la actualización");
            return false;
        }
        // También filtro por usuario para que cada quien solo pueda editar sus propios productos
        String sql = "UPDATE Productos SET nombre_producto = ?, precio = ?, cantidad = ? WHERE id_producto = ? AND usuarios_id_usuario = ?";
        try (Connection conn = Conexion.conectar()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombreProducto().trim());
                ps.setDouble(2, p.getPrecio());
                ps.setInt(3, p.getCantidad());
                ps.setInt(4, p.getIdProducto());
                ps.setInt(5, p.getUsuariosIdUsuario());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idProducto, int idUsuario) {
        // Igual que al actualizar: solo borro si el producto es del usuario que lo pide
        String sql = "DELETE FROM Productos WHERE id_producto = ? AND usuarios_id_usuario = ?";
        try (Connection conn = Conexion.conectar()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idProducto);
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}