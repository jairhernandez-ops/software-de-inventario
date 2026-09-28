package controlador;

import conexion.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recibo los datos del formulario (deben coincidir con el name en el HTML)
        String user = request.getParameter("usuario");
        String pass = request.getParameter("password");

        // Uso la clase Conexion para no repetir aquí los datos de la base de datos
        try (Connection con = Conexion.conectar()) {
            if (con == null) {
                throw new Exception("No se pudo conectar con la base de datos");
            }

            // Ahora también traigo el id_usuario, porque lo necesito para saber de quién es cada producto
            String sql = "SELECT id_usuario FROM usuarios WHERE username = ? AND password = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user);
                ps.setString(2, pass);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Login correcto: guardo el id del usuario en la sesión y lo mando al inventario
                        HttpSession sesion = request.getSession(true);
                        sesion.setAttribute("idUsuario", rs.getInt("id_usuario"));
                        response.sendRedirect("inventario.html");
                    } else {
                        // Login incorrecto: regreso al login con el aviso de error
                        response.sendRedirect("login.html?error=1");
                    }
                }
            }
        } catch (Exception e) {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.print("Error interno: " + e.getMessage());
        }
    }
}