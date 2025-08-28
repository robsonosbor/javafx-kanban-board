package com.example.kanban.model;

import java.time.LocalDateTime;

public class Card {
    private Long id;
    private Long boardId;
    private Long columnId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean locked;

    public Card() {
    }

    public Card(long id, long boardId, long columnId, String title, String description, LocalDateTime createdAt, boolean locked) {
        this.id = id;
        this.boardId = boardId;
        this.columnId = columnId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.locked = locked;
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

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return (locked ? "🔒 " : "") + title;
    }

    public boolean isBlocked() {
        return locked;
    }

}