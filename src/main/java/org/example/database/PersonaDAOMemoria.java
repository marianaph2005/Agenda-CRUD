package org.example.database;

import org.example.Persona;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Se extiende funcionalidad sin tocar el código anterior.
// Esta clase puede sustituir a PersonaDAO y el programa seguirá funcionando.
public class PersonaDAOMemoria implements IPersonaDAO {
    private List<Persona> almacenamiento = new ArrayList<>();
    private int contadorId = 1;

    @Override
    public void create(Persona p) throws SQLException {
        p.setId(contadorId++);
        almacenamiento.add(p);
        System.out.println("Guardado en MEMORIA (No DB): " + p.getNombre());
    }

    @Override
    public List<Persona> read() throws SQLException {
        return new ArrayList<>(almacenamiento);
    }

    @Override
    public void update(Persona p) throws SQLException {
        // Simulación simple de update
        delete(p.getId());
        almacenamiento.add(p);
    }

    @Override
    public void delete(int id) throws SQLException {
        almacenamiento.removeIf(persona -> persona.getId() == id);
    }
}