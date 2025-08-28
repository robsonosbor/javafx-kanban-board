package com.example.kanban.ui;

import com.example.kanban.model.Card;
import com.example.kanban.model.Column;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ColumnView extends VBox {

    private final Column column;
    private final VBox cardsBox;

    public ColumnView(Column column) {
        this.column = column;
        this.setSpacing(10);

        Label title = new Label(column.getName());
        cardsBox = new VBox(5);

        this.getChildren().addAll(title, cardsBox);
    }

    public Column getColumn() {
        return column;
    }

    public void addCard(Card card) {
        Label cardLabel = new Label(card.getTitle());
        cardsBox.getChildren().add(cardLabel);
    }

    public void removeCard(Card card) {
        // Implementar depois se precisar
    }
}
