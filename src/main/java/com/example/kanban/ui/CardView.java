package com.example.kanban.ui;

import com.example.kanban.model.Card;
import com.example.kanban.service.CardService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class CardView extends VBox {

    private final Card card;

    public CardView(Card card, CardService cardService) {
        this.card = card;

        Parent root = null;
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());


        setSpacing(5);
        this.getStyleClass().add("card");     // CardView

        Label title = new Label(card.getTitle());
        Label desc = new Label(card.getDescription());

        getChildren().addAll(title, desc);

        // Habilita Drag
        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(card.getId())); // Passa ID do card
            db.setContent(content);
            event.consume();
        });
    }

    public Card getCard() {
        return card;
    }
}
