package com.example.kanban.model;

import com.example.kanban.service.BoardService;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.kanban.service.ServiceConfig.boardService;

public class CardMovement {
    private Long id;
    private Long cardId;
    private Long fromColumnId;
    private Long toColumnId;
    private LocalDateTime enteredAt;
    private LocalDateTime leftAt;
    LocalDateTime movedAt;
    BoardService boardService;

    public CardMovement() {

    }

    public CardMovement(long id, long cardId, Long fromColumnId, long toColumnId, LocalDateTime movedAt) {
        if (id <= 0) {
            throw new IllegalArgumentException("O id do movimento deve ser maior que zero.");
        }
        if (cardId <= 0) {
            throw new IllegalArgumentException("O id do card deve ser maior que zero.");
        }
        if (toColumnId <= 0) {
            throw new IllegalArgumentException("O id da coluna de destino deve ser maior que zero.");
        }
        if (movedAt == null) {
            throw new IllegalArgumentException("A data/hora do movimento não pode ser nula.");
        }

        this.id = id;
        this.cardId = cardId;
        this.fromColumnId = (fromColumnId != null && fromColumnId > 0) ? fromColumnId : null;
        this.toColumnId = toColumnId;
        this.movedAt = movedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getFromColumnId() {
        return fromColumnId;
    }

    public void setFromColumnId(Long fromColumnId) {
        this.fromColumnId = fromColumnId;
    }

    public Long getToColumnId() {
        return toColumnId;
    }

    public void setToColumnId(Long toColumnId) {
        this.toColumnId = toColumnId;
    }

    public LocalDateTime getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(LocalDateTime enteredAt) {
        this.enteredAt = enteredAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public LocalDateTime getMovedAt() {
        return movedAt;
    }

    public void setMovedAt(LocalDateTime movedAt) {
        this.movedAt = movedAt;
    }

    public Column getColumn() {
        if (fromColumnId == null) return null;

        return boardService.findColumnById(fromColumnId).orElse(null);
    }


    public Column getFromColumn(BoardService boardService, Long boardId) {
        if (fromColumnId == null) return null;

        return boardService.findColumnById(boardId, fromColumnId).orElse(null);
    }

    public Column getToColumn(BoardService boardService, Long boardId) {
        if (toColumnId == null) return null;

        return boardService.findColumnById(boardId, toColumnId).orElse(null);
    }

}