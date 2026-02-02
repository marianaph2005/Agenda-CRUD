package org.example;

public class Telefono {
    private int id;
    private int personaId;
    private String numero;

    public Telefono(int id, int personaId, String numero) {
        this.id = id;
        this.personaId = personaId;
        this.numero = numero;
    }


    // Getters
    public int getId() { return id; }
    public String getNumero() { return numero; }
}