package org.example;

import org.example.database.PersonaDAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonaDAOTest {

    @Test
    public void testInsertarPersonaCompleta() {
        PersonaDAO dao = new PersonaDAO();
        Persona personaFake = new Persona("Guillermo Del Toro", "Calle UABC");

        personaFake.agregarTelefono(new Telefono(0, 0, "123-456-7890"));

        // Se ejecuta la acción y verificamos que no lance errores
        assertDoesNotThrow(() -> {
            dao.create(personaFake);
        }, "El método create lanzó una excepción de SQL");

        // Verificamos que el ID ya no sea 0
        // Si la base de datos lo registró, el ID debe ser mayor a 0
        assertTrue(personaFake.getId() > 0, "Error: La persona no recibió un ID de la base de datos");
        System.out.println("¡Prueba superada! Se guardó a " + personaFake.getNombre() + " con el ID: " + personaFake.getId());
    }
}