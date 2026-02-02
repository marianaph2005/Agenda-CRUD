package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";

    public static Connection get() throws SQLException {
        // Regresa la conexión lista
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}