package org.example.database;

public class FabricaDAO {

    private static PersonaDAOMemoria instanciaMemoria = new PersonaDAOMemoria();

    public static IPersonaDAO getPersonaDAO() {
        // Opción 1- database real (SQL)
        //return new PersonaDAO();

        // Opción 2- memoria (para pruebas de OCP y LSP)
        // Se devuelve siempre la misma instancia
        return instanciaMemoria;
    }
}