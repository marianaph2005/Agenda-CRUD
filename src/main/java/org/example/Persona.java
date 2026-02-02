package org.example;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private String direccion;
    private List<Telefono> telefonos;

    //Constructor para crear una persona (sin ID de la DB)
    public Persona(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }

    // Constructor completo (al leer de la BD)
    public Persona(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }

    public void agregarTelefono(Telefono telefono) {
        this.telefonos.add(telefono);
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public List<Telefono> getTelefonos() { return telefonos; }
}