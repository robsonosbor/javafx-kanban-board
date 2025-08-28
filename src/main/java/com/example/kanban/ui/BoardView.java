package com.example.kanban.ui;

import com.example.kanban.model.Board;
import com.example.kanban.model.Column;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.CardService;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class BoardView extends ScrollPane {

    private final BoardService boardService;
    private final CardService cardService;
    private final HBox columnsContainer;

    public BoardView(BoardService boardService, CardService cardService) {
        this.boardService = boardService;
        this.cardService = cardService;

        columnsContainer = new HBox(10);
        columnsContainer.setPadding(new Insets(10));
        setContent(columnsContainer);

        // Carregar primeiro board (exemplo)
        boardService.listBoards().stream().findFirst().ifPresent(this::loadBoard);
    }

    /**
     * (Já existente) Limpa o container e desenha as colunas do board informado.
     */
    public void loadBoard(Board board) {
        if (board == null) {
            clear();
            return;
        }
        columnsContainer.getChildren().clear();
        List<Column> cols = boardService.listColumns(board.getId());
        for (Column c : cols) {
            //columnsContainer.getChildren().add(new ColumnView(c, cardService));
        }
    }

    /**
     * Método público conveniente para mostrar um board (ou limpar, se null).
     */
    public void showBoard(Board board) {
        loadBoard(board);
    }

    /**
     * Limpa a área de colunas (útil quando nenhum board está selecionado).
     */
    public void clear() {
        columnsContainer.getChildren().clear();
    }

}

