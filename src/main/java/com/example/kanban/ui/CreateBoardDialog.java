package com.example.kanban.ui;

import com.example.kanban.model.Column;
import com.example.kanban.service.BoardService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreateBoardDialog extends Stage {

    public CreateBoardDialog(BoardService boardService) {
        setTitle("Criar Novo Board");
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label lblName = new Label("Nome do Board:");
        TextField txtName = new TextField();

        Label lblDescription = new Label("Descrição:");
        TextField txtDescription = new TextField();

        Button btnSave = new Button("Salvar");
        Button btnCancel = new Button("Cancelar");

        btnSave.setOnAction(e -> {
            String name = txtName.getText().trim();
            String desc= txtDescription.getText().trim();
            List<Column> columns = new ArrayList<>();

            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "O nome do board é obrigatório!", ButtonType.OK);
                alert.showAndWait();
            } else {
                boardService.createBoard(name, columns);
                close();
            }
        });

        btnCancel.setOnAction(e -> close());

        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblDescription, 0, 1);
        grid.add(txtDescription, 1, 1);
        grid.add(btnSave, 0, 2);
        grid.add(btnCancel, 1, 2);

        setScene(new Scene(grid, 400, 200));
    }
}
