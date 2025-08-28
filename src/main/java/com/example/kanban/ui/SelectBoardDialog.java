package com.example.kanban.ui;

import com.example.kanban.model.Board;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.CardService;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class SelectBoardDialog extends Stage {

    public SelectBoardDialog(Stage owner, BoardService boardService, CardService cardService) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Selecionar Board");

        List<Board> boards = boardService.listBoards();

        ListView<Board> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(boards));

        Button btnOpen = new Button("Abrir");
        btnOpen.setOnAction(e -> {
            Board selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Exibe o BoardMenuView como cena principal do Stage pai
                BoardMenuView boardMenu = new BoardMenuView(boardService, cardService, selected);
                Scene scene = new Scene(boardMenu.getView(), 900, 600);
                owner.setScene(scene);
                close();
            }
        });

        VBox box = new VBox(10, listView, btnOpen);
        setScene(new Scene(box, 400, 300));
    }
}
