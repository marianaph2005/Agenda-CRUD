package org.example;

import java.util.List;

// Esta clase solo valida
public class ValidadorPersona {

    public static void validarDatos(String nombre, List<String> telefonos, List<String> direcciones) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio.");
        }
        if (telefonos == null || telefonos.isEmpty()) {
            throw new Exception("Debe registrar al menos un teléfono.");
        }
        if (direcciones == null || direcciones.isEmpty()) {
            throw new Exception("Debe registrar al menos una dirección.");
        }
    }
}