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
            System.out.println("Producto inválido: no se realizó la inserción (nombre vacío, precio <= 0 o usuario inválido)");
            return false;
        }
        String sql = "INSERT INTO Productos (nombre_producto, precio, usuarios_id_usuario) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getNombreProducto());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getUsuariosIdUsuario());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listarPorUsuario(int idUsuario) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE usuarios_id_usuario = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombreProducto(rs.getString("nombre_producto"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setUsuariosIdUsuario(rs.getInt("usuarios_id_usuario"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE Productos SET nombre_producto = ?, precio = ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getNombreProducto());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getIdProducto());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM Productos WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}