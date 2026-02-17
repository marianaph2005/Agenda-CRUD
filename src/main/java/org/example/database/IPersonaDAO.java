package org.example.database;

import org.example.Persona;
import java.util.List;
import java.sql.SQLException;

// Interfaz específica solo para el CRUD de personas
public interface IPersonaDAO {
    void create(Persona p) throws SQLException;
    List<Persona> read() throws SQLException;
    void update(Persona p) throws SQLException;
    void delete(int id) throws SQLException;
}