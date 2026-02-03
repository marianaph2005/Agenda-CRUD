package org.example;

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
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        configurarColumnaBotones();

        // Se crea una lista 1 vez
        filteredData = new FilteredList<>(masterData, p -> true);
        tablaPersonas.setItems(filteredData);

        cargarDatos();

        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtEditID.setText(String.valueOf(newSelection.getId()));
                txtEditNombre.setText(newSelection.getNombre());
                txtEditDireccion.setText(newSelection.getDireccion());
            }
        });

        configurarBusqueda();
    }

    private void cargarDatos() {
        try {
            //Actualizamos masterData
            masterData.setAll(personaDAO.read());
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Error de base de datos: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleActualizar() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una persona primero");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agregar-persona.fxml"));
            Scene scene = new Scene(loader.load());

            AgregarController controller = loader.getController();
            controller.editarDatos(seleccionada);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Persona");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDatos();
            limpiarCampos();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Error abriendo ventana: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleEliminar() {
        if (txtEditID.getText().isEmpty()) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar a esta persona?");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agregar-persona.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Nueva Persona");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarDatos();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Error abriendo ventana: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void configurarBusqueda() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(persona -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(persona.getId()).contains(lowerCaseFilter)) return true;
                if (persona.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                if (persona.getDireccion().toLowerCase().contains(lowerCaseFilter)) return true;

                return persona.getTelefonos().stream()
                        .anyMatch(t -> t.getNumero().contains(lowerCaseFilter));
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
    }

    private void mostrarAlertaTelefonos(Persona p) {
        String listaTels = p.getTelefonos().stream()
                .map(Telefono::getNumero)
                .reduce("", (a, b) -> a + b + "\n");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Teléfonos de " + p.getNombre());
        alert.setContentText(listaTels.isEmpty() ? "Sin números." : listaTels);
        alert.showAndWait();
    }
}