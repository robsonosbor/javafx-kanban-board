package com.example.kanban.ui;

import com.example.kanban.service.BoardService;
import com.example.kanban.service.CardService;
import com.example.kanban.service.ServiceFactory;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class App extends BorderPane {

    private final Label boardTitle;
    private final BoardService boardService;
    private final CardService cardService;

    public App() {
        ServiceFactory factory = new ServiceFactory();
        this.boardService = factory.boardService();
        this.cardService = factory.cardService();

        ControlsBar controls = new ControlsBar(boardService, cardService);
        this.boardTitle = new Label("Nenhum board selecionado");

        VBox topBox = new VBox(10, controls, boardTitle);
        setTop(topBox);

        // escuta mudanças no combobox da ControlsBar
        controls.setOnBoardSelected(board -> {
            if (board != null) {
                boardTitle.setText("Board: " + board.getName());
                boardTitle.setPadding(new Insets(8));
                BoardMenuView menuView = new BoardMenuView(boardService, cardService, board);
                setCenter(menuView.getView());  // <-- usa o BoardMenuView
            } else {
                boardTitle.setText("Nenhum board selecionado");
                setCenter(new Label("Nenhum board carregado"));
            }
        });

        boardService.listBoards().stream().findFirst().ifPresent(b -> {
            controls.refreshBoards();
            controls.selectBoard(b);
            controls.notifyBoardSelected(b);
        });
    }
}



