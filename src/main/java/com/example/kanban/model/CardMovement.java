package com.example.kanban.model;

import java.time.Instant;
import java.time.LocalDateTime;

public class CardMovement {
    private Long id;
    private Long cardId;
    private Long fromColumnId;
    private Long toColumnId;
    private Instant enteredAt;
    private Instant leftAt;
    LocalDateTime movedAt;

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

    public Instant getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Instant enteredAt) {
        this.enteredAt = enteredAt;
    }

    public Instant getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Instant leftAt) {
        this.leftAt = leftAt;
    }

    public LocalDateTime getMovedAt() {
        return movedAt;
    }

    public void setMovedAt(LocalDateTime movedAt) {
        this.movedAt = movedAt;
    }
}