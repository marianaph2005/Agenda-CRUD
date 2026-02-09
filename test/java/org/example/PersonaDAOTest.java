package org.example;

import org.example.database.PersonaDAO;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PersonaDAOTest {

    // Instancia del DAO que usaremos en todas las pruebas
    PersonaDAO dao = new PersonaDAO();

    @Test
    public void testCRUDCompleto() throws SQLException {
        // Parte 1 - crear
        System.out.println("--- Iniciando Test para crear ---");

        // Se preparan los datos (nueva persona)
        List<Telefono> tels = new ArrayList<>();
        tels.add(new Telefono(0, 0, "555-111-2222"));
        List<Direccion> dirs = new ArrayList<>();
        dirs.add(new Direccion(0, 0, "Calle Prueba 123"));
        Persona p = new Persona("Test JUnit", dirs, tels);

        // Se inserta
        dao.create(p);

        // Se verifica que tenga ID
        assertTrue(p.getId() > 0, "Fallo Insertar: ID debería ser mayor a 0");
        System.out.println("Insertado correctamente con ID: " + p.getId());


        // Parte 2- leer
        System.out.println("\n--- Iniciando Test de Lectura ---");

        // Se leen todas las personas de la db
        List<Persona> todas = dao.read();

        // Se busca a la que acabamos de agregar
        Persona encontrada = null;
        for (Persona persona : todas) {
            if (persona.getId() == p.getId()) {
                encontrada = persona;
                break;
            }
        }

        // Se verifica
        assertNotNull(encontrada, "Fallo Leer: No se encontró la persona insertada");
        assertEquals("Test JUnit", encontrada.getNombre(), "Fallo Leer: El nombre no coincide");

        // Verificamos que tenga teléfonos y direcciones
        assertFalse(encontrada.getTelefonos().isEmpty(), "Fallo Leer: No trajo teléfonos");
        assertFalse(encontrada.getDirecciones().isEmpty(), "Fallo Leer: No trajo direcciones");

        // Verificar que la dirección sea la correcta
        assertEquals("Calle Prueba 123", encontrada.getDirecciones().get(0).getUbicacion());

        System.out.println("Persona encontrada y datos verificados.");


        // --- PARTE 3: MODIFICAR ---
        System.out.println("\n--- Iniciando Test de Actualización ---");

        // Se modifican los datos del objeto
        encontrada.setNombre("Test Modificado");
        encontrada.getDirecciones().clear();
        encontrada.getDirecciones().add(new Direccion(0, 0, "Calle Nueva 999"));

        encontrada.getTelefonos().add(new Telefono(0, 0, "999-000-1111"));

        // Se actualiza la db
        dao.update(encontrada);

        // Se vuelve a leer de la db para verificar que se guardó
        List<Persona> listaNueva = dao.read();
        Persona modificada = listaNueva.stream()
                .filter(x -> x.getId() == p.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(modificada);
        assertEquals("Test Modificado", modificada.getNombre());

        // Verificamos cambios en listas
        assertEquals("Calle Nueva 999", modificada.getDirecciones().get(0).getUbicacion(), "Fallo Update: Dirección no cambió");
        assertTrue(modificada.getTelefonos().size() >= 2, "Fallo Update: No se agregaron teléfonos");

        System.out.println("Actualización verificada exitosamente.");


        // Parte 4- borrar
        System.out.println("\n--- Iniciando Test de Eliminación ---");

        // Se elimina con el ID
        dao.delete(modificada.getId());

        // Se busca para asegurar que ya no existe
        List<Persona> listaFinal = dao.read();
        boolean existe = listaFinal.stream().anyMatch(x -> x.getId() == p.getId());

        assertFalse(existe, "Fallo Delete: La persona sigue existiendo en la BD");
        System.out.println("Eliminación verificada. Ciclo CRUD terminado.");
    }
}