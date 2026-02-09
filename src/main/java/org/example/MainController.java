package org.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.database.PersonaDAO;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, Integer> colID;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDireccion;
    @FXML private TableColumn<Persona, String> colTelefonos;

    @FXML private TextField txtBuscar;
    @FXML private TextField txtEditID;
    @FXML private TextField txtEditNombre;
    @FXML private TextField txtEditDireccion;

    private PersonaDAO personaDAO = new PersonaDAO();
    private ObservableList<Persona> masterData = FXCollections.observableArrayList();
    private FilteredList<Persona> filteredData;

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(cellData -> {
            var lista = cellData.getValue().getDirecciones();
            if (lista == null || lista.isEmpty()) {
                return new SimpleStringProperty("Sin dirección");
            } else {
                // Muestra la primera dirección y si hay más
                String primera = lista.get(0).getUbicacion();
                return new SimpleStringProperty(lista.size() > 1 ? primera + "..." : primera);
            }
        });

        configurarColumnaBotones();

        // Se crea una lista 1 vez
        filteredData = new FilteredList<>(masterData, p -> true);
        tablaPersonas.setItems(filteredData);

        cargarDatos();

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtEditID.setText(String.valueOf(newSelection.getId()));
                txtEditNombre.setText(newSelection.getNombre());

                // Si tiene direcciones se muestra la primera
                if (!newSelection.getDirecciones().isEmpty()) {
                    txtEditDireccion.setText(newSelection.getDirecciones().get(0).getUbicacion());
                } else {
                    txtEditDireccion.clear();
                }
            }
        });

        configurarBusqueda();
    }

    private void cargarDatos() {
        try {
            //Actualizamos masterData
            masterData.setAll(personaDAO.read());
        } catch (SQLException e) {
            mostrarError("Error DB: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una persona primero");
            return;
        }
        abrirVentanaEdicion(seleccionada);
    }

    @FXML
    private void handleEliminar() {
        if (txtEditID.getText().isEmpty()) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    personaDAO.delete(Integer.parseInt(txtEditID.getText()));
                    cargarDatos();
                    limpiarCampos();
                } catch (SQLException e) {
                    mostrarError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void abrirVentanaAgregar() {
        abrirVentanaEdicion(null); // null es una nueva persona
    }

    private void abrirVentanaEdicion(Persona personaAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agregar-persona.fxml"));
            Scene scene = new Scene(loader.load());

            AgregarController controller = loader.getController();
            // Si le pasamos a alguien se está editando, si es null es alguien nuevo
            if (personaAEditar != null) {
                controller.editarDatos(personaAEditar);
            }

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(personaAEditar == null ? "Nueva Persona" : "Editar Persona");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDatos();
            limpiarCampos();
        } catch (IOException e) {
            mostrarError("Error abriendo ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(persona -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();

                if (String.valueOf(persona.getId()).contains(lower)) return true;
                if (persona.getNombre().toLowerCase().contains(lower)) return true;

                // Búsqueda en teléfonos
                boolean matchTel = persona.getTelefonos().stream()
                        .anyMatch(t -> t.getNumero().contains(lower));

                // Búsqueda en la lista de direcciones
                boolean matchDir = persona.getDirecciones().stream()
                        .anyMatch(d -> d.getUbicacion().toLowerCase().contains(lower));

                return matchTel || matchDir;
            });
        });
    }

    private void limpiarCampos() {
        txtEditID.clear();
        txtEditNombre.clear();
        txtEditDireccion.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.showAndWait();
    }

    private void configurarColumnaBotones() {
        colTelefonos.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Ver Teléfonos");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    btn.setOnAction(event -> mostrarAlertaTelefonos(getTableView().getItems().get(getIndex())));
                    setGraphic(btn);
                }
            }
        });
        colDireccion.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Ver Direcciones");
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btn.setOnAction(event -> mostrarAlertaDirecciones(getTableView().getItems().get(getIndex())));
                    setGraphic(btn);
                }
            }
        });
    }

    private void mostrarAlertaTelefonos(Persona p) {
        String listaTels = p.getTelefonos().stream()
                .map(Telefono::getNumero)
                .reduce("", (a, b) -> a + b + "\n");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Teléfonos de " + p.getNombre());
        alert.setHeaderText("Teléfonos registrados:");
        alert.setContentText(listaTels.isEmpty() ? "Sin números." : listaTels);
        alert.showAndWait();
    }

    private void mostrarAlertaDirecciones(Persona p) {
        String listaDirs = p.getDirecciones().stream()
                .map(Direccion::getUbicacion)
                .reduce("", (a, b) -> a + b + "\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Direcciones de " + p.getNombre());
        alert.setHeaderText("Ubicaciones registradas:");
        alert.setContentText(listaDirs.isEmpty() ? "Sin direcciones registradas." : listaDirs);
        alert.showAndWait();
    }
}