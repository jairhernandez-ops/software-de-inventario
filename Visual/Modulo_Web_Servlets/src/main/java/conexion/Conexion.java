package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // URL con la zona horaria de Bogotá para evitar bloqueos
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true";


    // Se leen de variables de entorno: DB_USER y DB_PASSWORD.
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection conectar() {
        Connection conn = null;
        if (PASSWORD == null) {
            System.out.println("❌ Falta la variable de entorno DB_PASSWORD con la contraseña de MySQL");
            return null;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión establecida con éxito!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No se encontró el Driver de MySQL.");
            System.out.println("Revisa que mysql-connector-j esté en las dependencias del pom.xml");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            System.out.println("Revisa: que MySQL esté encendido, que la base de datos 'mydb' exista, y que DB_USER y DB_PASSWORD sean correctos");
        }
        return conn;
    }
}