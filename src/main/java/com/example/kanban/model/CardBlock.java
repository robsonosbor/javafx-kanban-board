package com.example.kanban.model;

import java.time.LocalDateTime;

public class CardBlock {
    private String reasonBlock;
    private String reasonUnblock;
    private Long id;
    private Long cardId;
    private LocalDateTime blockedAt;
    private LocalDateTime unblockedAt;
    private String blockReason;
    private String unblockReason;

    public CardBlock() {
    }

    public CardBlock(long id, long cardId, String reasonBlock, String reasonUnblock,
                     LocalDateTime blockedAt, LocalDateTime unblockedAt) {

        if (id <= 0) {
            throw new IllegalArgumentException("O id do bloqueio deve ser maior que zero.");
        }
        if (cardId <= 0) {
            throw new IllegalArgumentException("O id do card deve ser maior que zero.");
        }
        if (reasonBlock == null || reasonBlock.isBlank()) {
            throw new IllegalArgumentException("O motivo do bloqueio não pode ser nulo ou vazio.");
        }

        this.id = id;
        this.cardId = cardId;
        this.reasonBlock = reasonBlock.trim();
        this.reasonUnblock = (reasonUnblock != null && !reasonUnblock.isBlank()) ? reasonUnblock.trim() : null;
        this.blockedAt = (blockedAt != null) ? blockedAt : LocalDateTime.now();
        this.unblockedAt = unblockedAt;
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

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public LocalDateTime getUnblockedAt() {
        return unblockedAt;
    }

    public void setUnblockedAt(LocalDateTime unblockedAt) {
        this.unblockedAt = unblockedAt;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public String getUnblockReason() {
        return unblockReason;
    }

    public void setUnblockReason(String unblockReason) {
        this.unblockReason = unblockReason;
    }

    // Métodos a serem criados
    public String getReasonBlock() {
        return null;
    }

    public String getReasonUnblock() {
        return null;
    }

    public String getReason() {
        return null;
    }

    public void setReasonBlock(String reasonBlock) {
        this.reasonBlock = reasonBlock;
    }

    public void setReasonUnblock(String reasonUnblock) {
        this.reasonUnblock = reasonUnblock;
    }
}