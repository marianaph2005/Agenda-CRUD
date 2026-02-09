package org.example;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private List<Direccion> direcciones;
    private List<Telefono> telefonos;

    // Constructor para crear persona nueva sin ID ni telefonos ni direcciones
    public Persona(String nombre) {
        this.nombre = nombre;
        this.direcciones = new ArrayList<>();
        this.telefonos = new ArrayList<>();
    }

    // Constructor para crear persona nueva con telefonos y direcciones
    public Persona(String nombre, List<Direccion> direcciones, List<Telefono> telefonos) {
        this.nombre = nombre;
        this.direcciones = (direcciones != null) ? direcciones : new ArrayList<>();
        this.telefonos = (telefonos != null) ? telefonos : new ArrayList<>();
    }

    // Constructor para cuando cargamos de la DB con ID
    public Persona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.direcciones = new ArrayList<>();
        this.telefonos = new ArrayList<>();
    }

    public void agregarTelefono(Telefono telefono) {
        this.telefonos.add(telefono);
    }

    public void agregarDireccion(Direccion direccion) {
        this.direcciones.add(direccion);
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public List<Direccion> getDirecciones() { return direcciones; }
    public List<Telefono> getTelefonos() { return telefonos; }
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones; }
}