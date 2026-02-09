package org.example;

import org.example.database.PersonaDAO;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DireccionTest {

    PersonaDAO dao = new PersonaDAO();

    @Test
    public void testDireccionesCompartidas() throws SQLException {
        System.out.println("--- PRUEBA MAESTRA: Direcciones Compartidas ---");

        // Se prepara una dirección en común
        String calleComun = "Av. Reforma #555";

        // Se cre una persona con esa dirección
        List<Direccion> dirsA = new ArrayList<>();
        dirsA.add(new Direccion(0, 0, calleComun));
        Persona mariana = new Persona("Mariana", dirsA, null);

        // Se crea otra persona con la misma direccion
        List<Direccion> dirsB = new ArrayList<>();
        dirsB.add(new Direccion(0, 0, calleComun));
        Persona pepito = new Persona("Pepito", dirsB, null);

        // Se guarda en la db
        dao.create(mariana);
        System.out.println("Guardada Mariana con ID: " + mariana.getId());

        dao.create(pepito);
        System.out.println("Guardado Pepito con ID: " + pepito.getId());

        // Se recuperan los datos comprobando que el método read funcione
        List<Persona> todas = dao.read();

        Persona marianaBD = todas.stream().filter(p -> p.getId() == mariana.getId()).findFirst().orElse(null);
        Persona pepitoBD = todas.stream().filter(p -> p.getId() == pepito.getId()).findFirst().orElse(null);

        assertNotNull(marianaBD);
        assertNotNull(pepitoBD);

        int idDirMariana = marianaBD.getDirecciones().get(0).getId();
        int idDirPepito = pepitoBD.getDirecciones().get(0).getId();

        System.out.println("ID Dirección Mariana: " + idDirMariana);
        System.out.println("ID Dirección Pepito: " + idDirPepito);

        // Deberian de ser iguales
        assertEquals(idDirMariana, idDirPepito, "¡Error! Deberían compartir el mismo ID de dirección.");

        System.out.println("¡Éxito! El sistema detectó que viven donde mismo y reutilizó la dirección.");

        // Borramos los datos de prueba
        dao.delete(mariana.getId());
        dao.delete(pepito.getId());
    }
}