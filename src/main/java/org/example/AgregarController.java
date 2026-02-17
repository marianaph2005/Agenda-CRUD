package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.PersonaDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.database.FabricaDAO;
import org.example.database.IPersonaDAO;

public class AgregarController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtNuevaDireccion;
    @FXML private ListView<String> listDirecciones;
    @FXML private Button btnEliminarDireccion;
    @FXML private TextField txtNuevoTelefono;
    @FXML private ListView<String> listTelefonos;
    @FXML private Button btnEliminarTelefono;

    @FXML private Label lblTitulo;

    private IPersonaDAO dao;
    private Persona personaEnEdicion;

    public AgregarController() {
        this.dao = FabricaDAO.getPersonaDAO();
    }

    @FXML
    public void initialize() {
        // Deshabilitar botones de eliminar si no hay nada seleccionado
        btnEliminarTelefono.setDisable(true);
        btnEliminarDireccion.setDisable(true);

        listTelefonos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                btnEliminarTelefono.setDisable(newVal == null));

        listDirecciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                btnEliminarDireccion.setDisable(newVal == null));
    }

    //Método llamado desde MainController para editar una persona
    public void editarDatos(Persona p) {
        this.personaEnEdicion = p;
        lblTitulo.setText("Editar Persona - ID: " + p.getId());
        txtNombre.setText(p.getNombre());

        // Cargar teléfonos existentes
        listTelefonos.getItems().clear();
        for (Telefono t : p.getTelefonos()) listTelefonos.getItems().add(t.getNumero());

        listDirecciones.getItems().clear();
        for (Direccion d : p.getDirecciones()) listDirecciones.getItems().add(d.getUbicacion());
    }

    @FXML
    private void agregarDireccionALista() {
        String dir = txtNuevaDireccion.getText().trim();
        if (!dir.isEmpty() && !listDirecciones.getItems().contains(dir)) {
            listDirecciones.getItems().add(dir);
            txtNuevaDireccion.clear();
        }
    }

    @FXML
    private void eliminarDireccionSeleccionada() {
        String seleccionado = listDirecciones.getSelectionModel().getSelectedItem();
        if (seleccionado != null) listDirecciones.getItems().remove(seleccionado);
    }

    //Agregar un nuevo teléfono a la lista
    @FXML
    private void agregarTelefonoALista() {
        String num = txtNuevoTelefono.getText().trim();
        if (!num.isEmpty() && !listTelefonos.getItems().contains(num)) {
            listTelefonos.getItems().add(num);
            txtNuevoTelefono.clear();
        }
    }

    //Eliminar el teléfono seleccionado de la lista
    @FXML
    private void eliminarTelefonoSeleccionado() {
        String seleccionado = listTelefonos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selecciona un teléfono para eliminar");
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
        try {
            // Validar
            ValidadorPersona.validarDatos(
                    txtNombre.getText(),
                    listTelefonos.getItems(),
                    listDirecciones.getItems()
            );

            // Preparar datos
            List<Telefono> listaTels = new ArrayList<>();
            for (String s : listTelefonos.getItems()) listaTels.add(new Telefono(0, 0, s));

            List<Direccion> listaDirs = new ArrayList<>();
            for (String s : listDirecciones.getItems()) listaDirs.add(new Direccion(0, 0, s));

            if (personaEnEdicion == null) {
                // CREAR
                Persona nueva = new Persona(txtNombre.getText(), listaDirs, listaTels);
                dao.create(nueva);
            } else {
                // ACTUALIZAR
                personaEnEdicion.setNombre(txtNombre.getText());

                // Reemplazar lista de teléfonos completa
                personaEnEdicion.getTelefonos().clear();
                personaEnEdicion.getTelefonos().addAll(listaTels);

                personaEnEdicion.getDirecciones().clear();
                personaEnEdicion.getDirecciones().addAll(listaDirs);

                dao.update(personaEnEdicion);
            }

            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta(e.getMessage());
        }
    }

    // Cerrar la ventana actual
    @FXML
    private void cerrarVentana() {
        txtNombre.getScene().getWindow().hide();
    }

    private void mostrarAlerta(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje).showAndWait();
    }
}