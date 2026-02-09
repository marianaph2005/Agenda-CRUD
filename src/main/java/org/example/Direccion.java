package org.example;

public class Direccion {
    private int id;
    private int personaId;
    private String ubicacion;

    public Direccion(int id, int personaId, String ubicacion) {
        this.id = id;
        this.personaId = personaId;
        this.ubicacion = ubicacion;
    }

    // Getters
    public int getId() { return id; }
    public String getUbicacion() { return ubicacion; }
}