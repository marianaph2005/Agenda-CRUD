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
        Persona p = new Persona("Test JUnit", "Calle Prueba 123", tels);

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
        assertFalse(encontrada.getTelefonos().isEmpty(), "Fallo Leer: No trajo los teléfonos");
        System.out.println("Persona encontrada y datos verificados.");


        // Parte 3- modificar
        System.out.println("\n--- Iniciando Test de Actualización ---");

        // Se modifican los datos del objeto
        encontrada.setNombre("Test Modificado");
        encontrada.setDireccion("Calle Nueva 999");
        encontrada.getTelefonos().add(new Telefono(0, 0, "999-000-1111")); // Agregamos otro tel

        // Se actualiza la db
        dao.update(encontrada);

        // Se vuelve a leer de la db para verificar que se guardó
        List<Persona> listaNueva = dao.read();
        Persona modificada = listaNueva.stream().filter(x -> x.getId() == p.getId()).findFirst().orElse(null);

        assertNotNull(modificada);
        assertEquals("Test Modificado", modificada.getNombre(), "Fallo Update: Nombre no cambió");
        //ahora tiene 2 telefonos el original y el nuevo
        assertTrue(modificada.getTelefonos().size() >= 2, "Fallo Update: No se guardaron los teléfonos nuevos");
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