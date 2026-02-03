package org.example.database;

import org.example.Persona;
import org.example.Telefono;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    // Metodo para dar de alta
    public void create(Persona p) throws SQLException {
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String sqlTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        // Se usa la conexion
        try (Connection conn = Conexion.get()) {
            PreparedStatement psP = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            psP.setString(1, p.getNombre());
            psP.setString(2, p.getDireccion());
            psP.executeUpdate();

            ResultSet rs = psP.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                p.setId(idGenerado);

                // Insertamos los teléfonos usando la misma conexión
                try (PreparedStatement psT = conn.prepareStatement(sqlTelefono)) {
                    for (Telefono tel : p.getTelefonos()) {
                        psT.setInt(1, idGenerado);
                        psT.setString(2, tel.getNumero());
                        psT.executeUpdate();
                    }
                }
            }
        }
    }

    // Metodo de consulta para listar todos
    public List<Persona> read() throws SQLException {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Personas";
        try (Connection conn = Conexion.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Persona p = new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                // Se buscan sus teléfonos y se agregan
                p.getTelefonos().addAll(buscarTelefonosPorPersona(p.getId()));
                lista.add(p);
            }
        }
        return lista;
    }

    private List<Telefono> buscarTelefonosPorPersona(int personaId) throws SQLException {
        List<Telefono> tels = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personaId = ?";
        try (Connection conn = Conexion.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tels.add(new Telefono(rs.getInt("id"), personaId, rs.getString("telefono")));
            }
        }
        return tels;
    }

    public void update(Persona p) throws SQLException {
        String sqlUpdate = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";
        String sqlDeleteTels = "DELETE FROM Telefonos WHERE personaId = ?";
        String sqlInsertTels = "INSERT INTO Telefonos(personaId, telefono) VALUES (?, ?)";

        try (Connection conn = Conexion.get()) {
            conn.setAutoCommit(false); // IMPORTANTE: Iniciar transacción manual

            try {
                // Actualizar datos básicos
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setString(1, p.getNombre());
                    ps.setString(2, p.getDireccion());
                    ps.setInt(3, p.getId());
                    ps.executeUpdate();
                }

                // Borrar teléfonos anteriores
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTels)) {
                    ps.setInt(1, p.getId());
                    ps.executeUpdate();
                }

                // Insertar la lista nueva de teléfonos
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertTels)) {
                    for (Telefono t : p.getTelefonos()) {
                        ps.setInt(1, p.getId());
                        ps.setString(2, t.getNumero()); // Asegúrate de usar t.getNumero()
                        ps.executeUpdate();
                    }
                }

                conn.commit(); // si sale bien se guardan cambios
            } catch (SQLException e) {
                conn.rollback(); // si algo falló se deshace todo
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id = ?";
        try (Connection conn = Conexion.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}