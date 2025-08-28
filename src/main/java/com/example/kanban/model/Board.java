package com.example.kanban.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private Long id;
    private String name;
    private Instant createdAt;

    // ✅ Lista de colunas
    private List<Column> columns = new ArrayList<>();

    public Board() {
    }

    public Board(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }

    // ✅ Métodos para manipular colunas
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public void removeColumn(Column column) {
        this.columns.remove(column);
    }
}
