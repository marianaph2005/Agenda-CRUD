package org.example;

import org.example.database.Conexion;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ConexionTest {

    @Test
    public void testConexionExitosa() {
        System.out.println("Prueba 1: Verificando conexión a MariaDB...");

        // Se intenta obtener la conexión
        try (Connection conn = Conexion.get()) {

            // Verificamos que no sea null
            assertNotNull(conn, "Error: La conexión devolvió null");

            // Verificamos que esté abierta
            assertFalse(conn.isClosed(), "Error: La conexión se cerró inesperadamente");

            System.out.println("¡Éxito! Conexión establecida correctamente.");

        } catch (SQLException e) {
            fail("Falló la conexión: " + e.getMessage());
        }
    }
}