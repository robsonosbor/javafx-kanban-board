package com.example.kanban.ui;

import com.example.kanban.model.Board;
import com.example.kanban.service.BoardService;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeleteBoardDialog extends Stage {

    public DeleteBoardDialog(BoardService boardService) {
        setTitle("Excluir Boards");
        initModality(Modality.APPLICATION_MODAL);

        var boards = boardService.listBoards();

        ListView<Board> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(boards));

        Button btnDelete = new Button("Excluir");
        Button btnCancel = new Button("Cancelar");

        btnDelete.setOnAction(e -> {
            Board selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Tem certeza que deseja excluir o board \"" + selected.getName() + "\"?",
                        ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        boardService.deleteBoard(selected.getId());
                        listView.getItems().remove(selected);
                    }
                });
            }
        });

        btnCancel.setOnAction(e -> close());

        VBox box = new VBox(10, listView, btnDelete, btnCancel);
        setScene(new Scene(box, 400, 300));
    }
}
