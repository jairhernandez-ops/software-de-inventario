package controlador;

import dao.ProductoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Producto;
import java.io.IOException;
import java.util.List;

@WebServlet("/ProductoServlet")
public class ProductoServlet extends HttpServlet {

    private final ProductoDAO dao = new ProductoDAO();

    // GET: devuelvo en JSON los productos del usuario que inició sesión
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        Integer idUsuario = obtenerIdUsuario(request);
        if (idUsuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            escribir(response, respuesta(false, "Sesión no iniciada"));
            return;
        }

        List<Producto> lista = dao.listarPorUsuario(idUsuario);
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Producto p = lista.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{\"id\":").append(p.getIdProducto())
                .append(",\"nombre\":\"").append(escapar(p.getNombreProducto())).append("\"")
                .append(",\"precio\":").append(p.getPrecio())
                .append(",\"cantidad\":").append(p.getCantidad())
                .append("}");
        }
        json.append("]");
        escribir(response, json.toString());
    }

    // POST: según el parámetro "accion" agrego, actualizo, elimino o cierro la sesión
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String accion = request.getParameter("accion");

        // Cerrar sesión no necesita estar logueado para funcionar
        if ("cerrar".equals(accion)) {
            HttpSession sesion = request.getSession(false);
            if (sesion != null) {
                sesion.invalidate();
            }
            escribir(response, respuesta(true, "Sesión cerrada"));
            return;
        }

        // Para todo lo demás sí tiene que haber un usuario en la sesión
        Integer idUsuario = obtenerIdUsuario(request);
        if (idUsuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            escribir(response, respuesta(false, "Sesión no iniciada"));
            return;
        }

        if (accion == null) {
            escribir(response, respuesta(false, "Acción no válida"));
            return;
        }

        switch (accion) {
            case "agregar": {
                String nombre = request.getParameter("nombre");
                Double precio = leerPrecio(request.getParameter("precio"));
                Integer cantidad = leerEntero(request.getParameter("cantidad"));
                String problema = revisarDatos(nombre, precio, cantidad);
                if (problema != null) {
                    escribir(response, respuesta(false, problema));
                    return;
                }
                boolean ok = dao.insertar(new Producto(nombre.trim(), precio, cantidad, idUsuario));
                escribir(response, respuesta(ok, ok ? "Producto agregado correctamente" : "No se pudo agregar el producto"));
                break;
            }
            case "actualizar": {
                Integer id = leerEntero(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                Double precio = leerPrecio(request.getParameter("precio"));
                Integer cantidad = leerEntero(request.getParameter("cantidad"));
                if (id == null) {
                    escribir(response, respuesta(false, "Producto no válido"));
                    return;
                }
                String problema = revisarDatos(nombre, precio, cantidad);
                if (problema != null) {
                    escribir(response, respuesta(false, problema));
                    return;
                }
                Producto p = new Producto(nombre.trim(), precio, cantidad, idUsuario);
                p.setIdProducto(id);
                boolean ok = dao.actualizar(p);
                escribir(response, respuesta(ok, ok ? "Producto actualizado correctamente" : "No se encontró el producto o no se pudo actualizar"));
                break;
            }
            case "eliminar": {
                Integer id = leerEntero(request.getParameter("id"));
                if (id == null) {
                    escribir(response, respuesta(false, "Producto no válido"));
                    return;
                }
                boolean ok = dao.eliminar(id, idUsuario);
                escribir(response, respuesta(ok, ok ? "Producto eliminado correctamente" : "No se encontró el producto o no se pudo eliminar"));
                break;
            }
            default:
                escribir(response, respuesta(false, "Acción no válida"));
        }
    }

    // Saco de la sesión el id del usuario que guardó AuthServlet al iniciar sesión
    private Integer obtenerIdUsuario(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        Object id = sesion.getAttribute("idUsuario");
        return (id instanceof Integer) ? (Integer) id : null;
    }

    // Reviso los datos del formulario y devuelvo un mensaje si algo está mal (null si todo está bien)
    private String revisarDatos(String nombre, Double precio, Integer cantidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacío";
        }
        if (nombre.trim().length() > 45) {
            return "El nombre no puede tener más de 45 caracteres";
        }
        if (precio == null || Double.isNaN(precio) || Double.isInfinite(precio)) {
            return "El precio debe ser un número";
        }
        if (precio <= 0) {
            return "El precio debe ser mayor a 0";
        }
        if (precio > 99999999.99) {
            return "El precio es demasiado grande";
        }
        if (cantidad == null) {
            return "La cantidad debe ser un número entero";
        }
        if (cantidad < 0) {
            return "La cantidad no puede ser negativa";
        }
        return null;
    }

    private Double leerPrecio(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Lo uso para el id y para la cantidad, los dos son números enteros
    private Integer leerEntero(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String respuesta(boolean ok, String mensaje) {
        return "{\"ok\":" + ok + ",\"mensaje\":\"" + escapar(mensaje) + "\"}";
    }

    private void escribir(HttpServletResponse response, String texto) throws IOException {
        response.getWriter().write(texto);
    }

    // Escapo las comillas y otros caracteres raros para que el JSON no se dañe
    private String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}