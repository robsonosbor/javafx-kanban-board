package com.example.kanban.ui;

import com.example.kanban.model.Board;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.CardService;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.function.Consumer;

public class ControlsBar extends ToolBar {

    private final BoardService boardService;
    private final ComboBox<Board> boardSelector;
    private Consumer<Board> boardSelectedListener;

    public ControlsBar(BoardService boardService, CardService cardService) {
        this.boardService = boardService;

        // Botões principais
        Button newBoardBtn = new Button("Novo Board");
        Button deleteBoardBtn = new Button("Excluir Board");
        Button exitBtn = new Button("Sair");

        // Dropdown para selecionar board
        boardSelector = new ComboBox<>();
        boardSelector.setPromptText("Selecione um board");
        refreshBoards();

        // Ações
        newBoardBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Novo Board");
            dialog.setHeaderText("Criar novo board");
            dialog.setContentText("Digite o nome do board:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(boardName -> {
                if (boardName.trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Aviso");
                    alert.setHeaderText("Nome inválido");
                    alert.setContentText("O nome do board não pode ser vazio.");
                    alert.showAndWait();
                } else {
                    Board b = boardService.createBoardWithDefaults(boardName, null);
                    refreshBoards();
                    boardSelector.getSelectionModel().select(b);
                    notifyBoardSelected(b);
                }
            });
        });

        deleteBoardBtn.setOnAction(e -> {
            Board b = boardSelector.getValue();
            if (b != null) {
                boardService.deleteBoard(b.getId());
                refreshBoards();
                notifyBoardSelected(null); // limpa o BoardView
            }
        });

        exitBtn.setOnAction(e -> System.exit(0));

        boardSelector.setOnAction(e -> {
            Board selected = boardSelector.getSelectionModel().getSelectedItem();
            notifyBoardSelected(selected);
        });

        getItems().addAll(newBoardBtn, deleteBoardBtn, boardSelector, exitBtn);
    }

    public void refreshBoards() {
        boardSelector.getItems().setAll(boardService.listBoards());
    }

    public Board getSelectedBoard() {
        return boardSelector.getValue();
    }

    public void setOnBoardSelected(Consumer<Board> listener) {
        this.boardSelectedListener = listener;
    }

    void notifyBoardSelected(Board b) {
        if (boardSelectedListener != null) {
            boardSelectedListener.accept(b);
        }
    }

    public void selectBoard(Board b) {
        boardSelector.getSelectionModel().select(b);
    }
}


