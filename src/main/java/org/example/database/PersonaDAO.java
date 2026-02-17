package org.example.database;

import org.example.Direccion;
import org.example.Persona;
import org.example.Telefono;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO implements IPersonaDAO{

    // Metodo para dar de alta
    public void create(Persona p) throws SQLException {
        String sqlPersona = "INSERT INTO Personas (nombre) VALUES (?)";
        String sqlAsociarDir = "INSERT INTO Personas_Direcciones (persona_id, direccion_id) VALUES (?, ?)";
        String sqlTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        // Se usa la conexion
        try (Connection conn = Conexion.get()) {
            conn.setAutoCommit(false); // Inicio transacción
            try {
                PreparedStatement psP = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
                psP.setString(1, p.getNombre());
                psP.executeUpdate();

                ResultSet rs = psP.getGeneratedKeys();
                if (rs.next()) {
                    int idPersona = rs.getInt(1);
                    p.setId(idPersona);

                    // Insertamos las direcciones usando la misma conexión
                    for (Direccion dir : p.getDirecciones()) {
                        int idDir = obtenerOCrearDireccion(dir.getUbicacion(), conn);
                        try (PreparedStatement psD = conn.prepareStatement(sqlAsociarDir)) {
                            psD.setInt(1, idPersona);
                            psD.setInt(2, idDir);
                            psD.executeUpdate();
                        }
                    }

                    // Insertamos los teléfonos usando la misma conexión
                    try (PreparedStatement psT = conn.prepareStatement(sqlTelefono)) {
                        for (Telefono tel : p.getTelefonos()) {
                            psT.setInt(1, idPersona);
                            psT.setString(2, tel.getNumero());
                            psT.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
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
                // Solo recuperamos ID y Nombre de esta tabla
                Persona p = new Persona(rs.getInt("id"), rs.getString("nombre"));
                p.getTelefonos().addAll(buscarTelefonosPorPersona(p.getId()));
                p.getDirecciones().addAll(buscarDireccionesPorPersona(p.getId()));
                lista.add(p);
            }
        }
        return lista;
    }

    public void update(Persona p) throws SQLException {
        // Se actualiza solo el nombre
        String sqlUpdateNombre = "UPDATE Personas SET nombre = ? WHERE id = ?";
        // Se borran los antiguos
        String sqlDeleteLinksDir = "DELETE FROM Personas_Direcciones WHERE persona_id = ?";
        String sqlDeleteTels = "DELETE FROM Telefonos WHERE personaId = ?";

        // Se actualizan
        String sqlInsertLinkDir = "INSERT INTO Personas_Direcciones (persona_id, direccion_id) VALUES (?, ?)";
        String sqlInsertTel = "INSERT INTO Telefonos(personaId, telefono) VALUES (?, ?)";

        try (Connection conn = Conexion.get()) {
            conn.setAutoCommit(false); // Transacción manual

            try {
                // Actualizar nombre
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateNombre)) {
                    ps.setString(1, p.getNombre());
                    ps.setInt(2, p.getId());
                    ps.executeUpdate();
                }

                // Actualizar direcciones (borrar antiguas y crear todas nuevas)
                // Borrar las anteriores
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteLinksDir)) {
                    ps.setInt(1, p.getId());
                    ps.executeUpdate();
                }
                // Insertar nuevas
                for (Direccion dir : p.getDirecciones()) {
                    int idDir = obtenerOCrearDireccion(dir.getUbicacion(), conn);
                    try (PreparedStatement psLink = conn.prepareStatement(sqlInsertLinkDir)) {
                        psLink.setInt(1, p.getId());
                        psLink.setInt(2, idDir);
                        psLink.executeUpdate();
                    }
                }

                // Actualizar teléfonos
                try (PreparedStatement ps = conn.prepareStatement(sqlDeleteTels)) {
                    ps.setInt(1, p.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertTel)) {
                    for (Telefono t : p.getTelefonos()) {
                        ps.setInt(1, p.getId());
                        ps.setString(2, t.getNumero());
                        ps.executeUpdate();
                    }
                }

                conn.commit(); // Confirmar cambios
            } catch (SQLException e) {
                conn.rollback(); // Deshacer si falla
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

    private int obtenerOCrearDireccion(String ubicacion, Connection conn) throws SQLException {
        String sqlBuscar = "SELECT id FROM Direcciones WHERE ubicacion = ?";
        String sqlInsertar = "INSERT INTO Direcciones (ubicacion) VALUES (?)";

        // Buscar si ya existe
        try (PreparedStatement psB = conn.prepareStatement(sqlBuscar)) {
            psB.setString(1, ubicacion);
            ResultSet rs = psB.executeQuery();
            if (rs.next()) return rs.getInt(1); // ¡Ya existe! Usamos esa.
        }

        // Si no existe se crea
        try (PreparedStatement psI = conn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            psI.setString(1, ubicacion);
            psI.executeUpdate();
            ResultSet rs = psI.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    private List<Direccion> buscarDireccionesPorPersona(int personaId) throws SQLException {
        List<Direccion> dirs = new ArrayList<>();
        String sql = "SELECT d.id, d.ubicacion FROM Direcciones d " +
                "JOIN Personas_Direcciones pd ON d.id = pd.direccion_id " +
                "WHERE pd.persona_id = ?";
        try (Connection conn = Conexion.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dirs.add(new Direccion(rs.getInt("id"), personaId, rs.getString("ubicacion")));
            }
        }
        return dirs;
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
}