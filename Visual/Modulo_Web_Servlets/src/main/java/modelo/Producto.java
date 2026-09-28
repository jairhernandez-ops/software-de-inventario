package modelo;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private double precio;
    private int cantidad;
    private int usuariosIdUsuario;

    public Producto() {}

    // Este constructor lo dejo como estaba (sin cantidad) para que las pruebas que ya tenía sigan funcionando.
    // La cantidad queda en 0 por defecto.
    public Producto(String nombreProducto, double precio, int usuariosIdUsuario) {
        this(nombreProducto, precio, 0, usuariosIdUsuario);
    }

    // Constructor nuevo, con la cantidad que hay en bodega
    public Producto(String nombreProducto, double precio, int cantidad, int usuariosIdUsuario) {
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.usuariosIdUsuario = usuariosIdUsuario;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getUsuariosIdUsuario() { return usuariosIdUsuario; }
    public void setUsuariosIdUsuario(int usuariosIdUsuario) { this.usuariosIdUsuario = usuariosIdUsuario; }
}