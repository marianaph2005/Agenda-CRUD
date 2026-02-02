package org.example;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private String direccion;
    private List<Telefono> telefonos;

    public Persona(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public List<Telefono> getTelefonos() { return telefonos; }
}