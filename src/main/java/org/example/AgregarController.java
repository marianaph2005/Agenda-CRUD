package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.PersonaDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgregarController {
    @FXML private TextField txtNombre, txtDireccion, txtNuevoTelefono;
    @FXML private ListView<String> listTelefonos;
    @FXML private Button btnEliminarTelefono;
    @FXML private Label lblTitulo;

    private PersonaDAO dao = new PersonaDAO();
    private Persona personaEnEdicion; // null- modo crear, dif de null- modo editar

    @FXML
    public void initialize() {
        // Habilitar botón de eliminar
        btnEliminarTelefono.setDisable(true);
        listTelefonos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            btnEliminarTelefono.setDisable(newVal == null);
        });
    }

    //Método llamado desde MainController para editar una persona
    public void editarDatos(Persona p) {
        this.personaEnEdicion = p;
        lblTitulo.setText("Editar Persona - ID: " + p.getId());
        txtNombre.setText(p.getNombre());
        txtDireccion.setText(p.getDireccion());

        // Cargar teléfonos existentes
        listTelefonos.getItems().clear();
        for (Telefono t : p.getTelefonos()) {
            listTelefonos.getItems().add(t.getNumero());
        }
    }

    //Agregar un nuevo teléfono a la lista
    @FXML
    private void agregarTelefonoALista() {
        String numero = txtNuevoTelefono.getText().trim();

        if (numero.isEmpty()) {
            mostrarAdvertencia("Escribe un número de teléfono");
            return;
        }

        // No permitir duplicados
        if (listTelefonos.getItems().contains(numero)) {
            mostrarAdvertencia("Este número ya está en la lista");
            return;
        }

        listTelefonos.getItems().add(numero);
        txtNuevoTelefono.clear();
        txtNuevoTelefono.requestFocus();
    }

    //Eliminar el teléfono seleccionado de la lista
    @FXML
    private void eliminarTelefonoSeleccionado() {
        String seleccionado = listTelefonos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Selecciona un teléfono para eliminar");
            return;
        }

        // Confirmación antes de eliminar
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar el teléfono: " + seleccionado + "?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                listTelefonos.getItems().remove(seleccionado);
            }
        });
    }

    // Guardar datos (crear o actualizar)
    @FXML
    private void guardarPersona() {
        // Validaciones por si deja vacío algún campo
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAdvertencia("El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        if (direccion.isEmpty()) {
            mostrarAdvertencia("La dirección es obligatoria");
            txtDireccion.requestFocus();
            return;
        }

        if (listTelefonos.getItems().isEmpty()) {
            mostrarAdvertencia("Agrega al menos un teléfono");
            txtNuevoTelefono.requestFocus();
            return;
        }

        // Se construye la lista de los telefonos
        List<Telefono> listaTelefonos = new ArrayList<>();
        for (String numeroTel : listTelefonos.getItems()) {
            // Los IDs de los teléfonos se generan automáticamente en la db
            listaTelefonos.add(new Telefono(0, 0, numeroTel));
        }

        // Guardar en la base de datos
        try {
            if (personaEnEdicion == null) {
                // Modo crear nuevas personas
                Persona nueva = new Persona(nombre, direccion, listaTelefonos);
                dao.create(nueva);
                mostrarExito("Persona creada exitosamente con ID: " + nueva.getId());
            } else {
                // Modo para editar las personas existentes
                personaEnEdicion.setNombre(nombre);
                personaEnEdicion.setDireccion(direccion);

                // Reemplazar lista de teléfonos completa
                personaEnEdicion.getTelefonos().clear();
                personaEnEdicion.getTelefonos().addAll(listaTelefonos);

                dao.update(personaEnEdicion);
                mostrarExito("Persona actualizada exitosamente");
            }

            cerrarVentana();

        } catch (SQLException e) {
            mostrarError("Error al guardar en la base de datos:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cerrar la ventana actual
    @FXML
    private void cerrarVentana() {
        txtNombre.getScene().getWindow().hide();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}