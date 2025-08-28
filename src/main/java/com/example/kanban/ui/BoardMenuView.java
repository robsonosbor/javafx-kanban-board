package com.example.kanban.ui;

import com.example.kanban.dao.CardDao;
import com.example.kanban.dao.jdbc.JdbcCardDao;
import com.example.kanban.model.Board;
import com.example.kanban.model.Card;
import com.example.kanban.model.Column;
import com.example.kanban.model.ColumnType;
import com.example.kanban.service.BoardService;
import com.example.kanban.service.CardService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * BoardMenuView atualizado — monta o board (colunas + cards) e painel de ações.
 */
public class BoardMenuView {

    private final BoardService boardService;
    private final CardService cardService;
    private final Board board;
    private final List<ColumnView> columnViews = new ArrayList<>(); // lista das colunas renderizadas
    private final CardDao cardDao = new JdbcCardDao();


    // raiz mantida para refrescar
    private BorderPane rootPane;

    public BoardMenuView(BoardService boardService, CardService cardService, Board board) {
        this.boardService = boardService;
        this.cardService = cardService;
        this.board = board;
    }

    public Parent getView() {
        rootPane = new BorderPane();

        // Área central (colunas + cards)
        updateCenter();

        // Painel lateral com ações rápidas (ex: criar card)
        VBox actionsBox = new VBox(10);
        actionsBox.setPadding(new Insets(12));
        actionsBox.setAlignment(Pos.TOP_CENTER);
        actionsBox.setPrefWidth(220);

        Button btnCreateCard = new Button("Criar card");
        btnCreateCard.setMaxWidth(Double.MAX_VALUE);
        btnCreateCard.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Novo Card");
            dialog.setHeaderText("Criar um novo card");
            dialog.setContentText("Título do card:");

            dialog.showAndWait().ifPresent(title -> {
                // cria novo card
                Card card = new Card();
                card.setTitle(title);
                card.setDescription("Descrição do card"); // pode abrir outro diálogo depois
                card.setCreatedAt(LocalDateTime.now());
                card.setLocked(false);

                // identifica a coluna inicial do board
                Column initialCol = board.getColumns().stream()
                        .filter(c -> c.getType() == ColumnType.INITIAL)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Board sem coluna inicial"));

                card.setColumnId(initialCol.getId());

                // salva no banco
                cardService.createCard(card.getId(), card.getTitle(), card.getDescription());

                // adiciona na UI
                for (ColumnView colView : columnViews) {
                    if (colView.getColumn().getId().equals(initialCol.getId())) {
                        colView.addCard(card);
                    }
                }
            });
        });


        Button btnList = new Button("Listar (console)");
        btnList.setMaxWidth(Double.MAX_VALUE);
        btnList.setOnAction(e -> {
            List<Column> cols = boardService.listColumns(board.getId());
            cols.forEach(col -> {
                System.out.println("Coluna: " + col.getName() + " (" + col.getType() + ")");
                if (col.getCards() == null || col.getCards().isEmpty()) {
                    System.out.println("  (vazia)");
                } else {
                    for (Card c : col.getCards()) {
                        System.out.println("  id=" + c.getId()
                                + " title=" + c.getTitle()
                                + " locked=" + c.isLocked()
                                + " created=" + c.getCreatedAt());
                    }
                }
            });
        });

        actionsBox.getChildren().addAll(btnCreateCard, btnList);

        rootPane.setRight(actionsBox);

        return rootPane;
    }

    /** Reconstrói a área central com colunas e cards (chame após cada alteração). */
    private void updateCenter() {
        HBox columnsBox = new HBox(16);
        columnsBox.setPadding(new Insets(12));
        columnsBox.setAlignment(Pos.TOP_LEFT);

        List<Column> cols = boardService.listColumns(board.getId());
        for (Column col : cols) {
            VBox colBox = new VBox(8);
            colBox.setPadding(new Insets(8));
            colBox.setAlignment(Pos.TOP_LEFT);
            colBox.setPrefWidth(260);

            Label colTitle = new Label(col.getName());
            colTitle.setWrapText(true);
            colTitle.setTextAlignment(TextAlignment.CENTER);

            // Container de cards (scrollable)
            VBox cardsContainer = new VBox(8);
            cardsContainer.setPadding(new Insets(6));

            // busca cards diretamente do DAO/serviço e indica no Column antes de renderizar
            List<Card> cardsForColumn;
            try {
                cardsForColumn = cardDao.findByBoardAndColumn(board.getId(), col.getId());
            } catch (Exception ex) {
                cardsForColumn = new ArrayList<>();
            }

            for (Card card : cardsForColumn) {
                Node cardNode = createCardNode(card);
                cardsContainer.getChildren().add(cardNode);
            }


            ScrollPane scroll = new ScrollPane(cardsContainer);
            scroll.setFitToWidth(true);
            scroll.setPrefViewportHeight(420);

            colBox.getChildren().addAll(colTitle, scroll);
            columnsBox.getChildren().add(colBox);
        }

        rootPane.setCenter(columnsBox);
    }

    /** Cria um node visual para um card, com botões que chamam CardService. */
    private Node createCardNode(Card card) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(8));
        box.setPrefWidth(220);

        Label title = new Label(card.getTitle());
        title.setWrapText(true);
        title.getStyleClass().add("titleCard");

        Label meta = new Label("id: " + card.getId() + (card.isLocked() ? " 🔒" : ""));
        meta.getStyleClass().add("label-meta");

        // Formatter de entrada (ISO)
        DateTimeFormatter formatedDateBr = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        ZoneId BrZone = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime zonedBrasil = card.getCreatedAt().atZone(ZoneOffset.UTC).withZoneSameInstant(BrZone);
        Label dateCard = new Label("Data: " + zonedBrasil.format(formatedDateBr));
        dateCard.getStyleClass().add("label-meta");

        Label descriptionCard = new Label(card.getDescription());
        descriptionCard.getStyleClass().add("label-description");

        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_LEFT);

        // Carrega a imagem
        Image add = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/move.png")));
        ImageView moveView = new ImageView(add);
        moveView.setFitWidth(16);
        moveView.setFitHeight(16);

        Button moveBtn = new Button("", moveView);
        moveBtn.setContentDisplay(ContentDisplay.LEFT);
        moveBtn.setGraphic(moveView);
        moveBtn.setOnAction(e -> {
            try {
                cardService.moveToNext(card.getId());
                updateCenter();
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // Carrega a imagem
        Image cancel = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/cancel.png")));
        ImageView cancelView = new ImageView(cancel);
        cancelView.setFitWidth(16);
        cancelView.setFitHeight(16);

        Button cancelBtn = new Button("");
        cancelBtn.setContentDisplay(ContentDisplay.LEFT);
        cancelBtn.setGraphic(cancelView);
        cancelBtn.setOnAction(e -> {
            try {
                Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Confirma cancelar o card " + card.getId() + " ?", ButtonType.YES, ButtonType.NO);
                conf.setTitle("Confirmar");
                Optional<ButtonType> resp = conf.showAndWait();
                if (resp.isPresent() && resp.get() == ButtonType.YES) {
                    cardService.cancel(card.getId());
                    updateCenter();
                }
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        Image block = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/lock.png")));
        ImageView blockView = new ImageView(block);
        blockView.setFitWidth(16);
        blockView.setFitHeight(16);

        Button blockBtn = new Button("");
        blockBtn.setContentDisplay(ContentDisplay.LEFT);
        blockBtn.setGraphic(blockView);
        blockBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setTitle("Bloquear card");
            d.setHeaderText("Motivo do bloqueio:");
            Optional<String> reason = d.showAndWait();
            reason.ifPresent(r -> {
                try {
                    cardService.block(card.getId(), r);
                    updateCenter();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });
        });

        Image unblock = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/unlock.png")));
        ImageView unblockView = new ImageView(unblock);
        unblockView.setFitWidth(16);
        unblockView.setFitHeight(16);

        Button unblockBtn = new Button("");
        unblockBtn.setContentDisplay(ContentDisplay.LEFT);
        unblockBtn.setGraphic(unblockView);
        unblockBtn.setOnAction(e -> {
            TextInputDialog d = new TextInputDialog();
            d.setTitle("Desbloquear card");
            d.setHeaderText("Motivo do desbloqueio:");
            Optional<String> reason = d.showAndWait();
            reason.ifPresent(r -> {
                try {
                    cardService.unblock(card.getId(), r);
                    updateCenter();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            });
        });

        // Habilita ou desabilita botões conforme estado do card
        moveBtn.setDisable(card.isLocked());
        cancelBtn.setDisable(card.isLocked());
        blockBtn.setDisable(card.isLocked());
        unblockBtn.setDisable(!card.isLocked());

        actions.getChildren().addAll(moveBtn, cancelBtn, blockBtn, unblockBtn);

        box.getChildren().addAll(title, meta, dateCard, descriptionCard, actions);
        return box;
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Erro");
        a.showAndWait();
    }
}
