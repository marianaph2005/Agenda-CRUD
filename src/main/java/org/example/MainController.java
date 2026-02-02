package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController {
    @FXML
    private TableView<Persona> tablaPersonas;
    @FXML
    private TableColumn<Persona, Integer> colID;
    @FXML
    private TableColumn<Persona, String> colNombre;
    @FXML
    private TableColumn<Persona, String> colDireccion;

    @FXML
    public void initialize() {
        System.out.println("Controlador conectado.");
    }
}