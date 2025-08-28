package com.example.kanban.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private Long id;
    private Long boardId;
    private String name;
    private int ord;
    private ColumnType type;
    private List<Card> cards = new ArrayList<>();;

    public Column() {
    }

    public Column(Long id, Long boardId, String name, int ord, ColumnType type) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.ord = ord;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrd() {
        return ord;
    }

    public void setOrd(int ord) {
        this.ord = ord;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}