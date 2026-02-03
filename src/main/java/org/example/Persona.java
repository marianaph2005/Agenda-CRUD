package org.example;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private String direccion;
    private List<Telefono> telefonos;

    // Constructor para crear persona nueva sin ID ni telefonos
    public Persona(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }
    // Constructor para crear persona nueva con telefonos
    public Persona(String nombre, String direccion, List<Telefono> telefonos) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = (telefonos != null) ? telefonos : new ArrayList<>();
    }
    // Constructor para cuando cargamos de la DB con ID
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
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public List<Telefono> getTelefonos() { return telefonos; }
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion;}
}