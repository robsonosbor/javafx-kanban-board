
package com.example.kanban.service;

import com.example.kanban.dao.CardBlockDao;
import com.example.kanban.dao.CardDao;
import com.example.kanban.dao.CardMovementDao;
import com.example.kanban.dao.ColumnDao;
import com.example.kanban.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class CardService {

    private CardDao cardDao;
    private ColumnDao columnDao;
    private CardMovementDao movementDao;
    private CardBlockDao blockDao;

    public CardService() {

    }
    
    public CardService(CardDao cardDao, ColumnDao columnDao, CardMovementDao movementDao, CardBlockDao blockDao) {
        this.cardDao = Objects.requireNonNull(cardDao);
        this.columnDao = Objects.requireNonNull(columnDao);
        this.movementDao = Objects.requireNonNull(movementDao);
        this.blockDao = Objects.requireNonNull(blockDao);
    }

    /**
     * Cria um card posicionado na coluna INITIAL do board.
     */
    public Card createCard(Long boardId, String title, String description) {
        if (boardId == null) throw new IllegalArgumentException("boardId é obrigatório");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Título é obrigatório");

        Column initial = getInitialColumn(boardId)
                .orElseThrow(() -> new IllegalStateException("Board não possui coluna INITIAL"));

        Card c = new Card();
        c.setBoardId(boardId);
        c.setColumnId(initial.getId());
        c.setTitle(title.trim());
        c.setDescription(description);
        c.setCreatedAt(LocalDateTime.now());
        c.setLocked(false);

        // persiste card
        cardDao.create(c);

        // registra movimento inicial (from = null)
        CardMovement mv = new CardMovement();
        mv.setCardId(c.getId());
        mv.setFromColumnId(null);
        mv.setToColumnId(initial.getId());
        mv.setEnteredAt(Instant.now());
        movementDao.create(mv);

        return c;
    }


    /** Move o card para a próxima coluna seguindo a ordem definida. */
    public void moveToNext(Long cardId) {
        Card card = cardDao.findById(cardId).orElseThrow(() -> new NoSuchElementException("Card não encontrado"));
        if (card.isLocked()) {
            throw new IllegalStateException("Card está bloqueado e não pode ser movido");
        }

        List<Column> cols = columnsOrdered(card.getBoardId());
        int curIdx = indexOfColumn(cols, card.getColumnId());
        Column current = cols.get(curIdx);

        if (current.getType() == ColumnType.CANCEL || current.getType() == ColumnType.FINAL) {
            throw new IllegalStateException("Card não pode avançar a partir de coluna FINAL ou CANCEL");
        }

        Column next = cols.get(curIdx + 1);

        // Atualiza card
        Long fromId = card.getColumnId();
        card.setColumnId(next.getId());
        cardDao.update(card);

        // Registra movimento
        CardMovement mv = new CardMovement();
        mv.setCardId(card.getId());
        mv.setFromColumnId(fromId);
        mv.setToColumnId(next.getId());
        mv.setEnteredAt(Instant.now());
        movementDao.create(mv);
    }

    /** Cancela o card movendo para a coluna CANCEL a partir de qualquer coluna que não seja FINAL. */
    public void cancel(Long cardId) {
        Card card = cardDao.findById(cardId).orElseThrow(() -> new NoSuchElementException("Card não encontrado"));
        if (card.isLocked()) {
            throw new IllegalStateException("Card está bloqueado e não pode ser cancelado");
        }

        List<Column> cols = columnsOrdered(card.getBoardId());
        Column current = cols.get(indexOfColumn(cols, card.getColumnId()));
        if (current.getType() == ColumnType.FINAL) {
            throw new IllegalStateException("Card finalizado não pode ser cancelado");
        }
        Column cancel = cols.getLast(); // por regra é a última

        // Atualiza card e registra movimento
        Long fromId = card.getColumnId();
        card.setColumnId(cancel.getId());
        cardDao.update(card);

        CardMovement mv = new CardMovement();
        mv.setCardId(card.getId());
        mv.setFromColumnId(fromId);
        mv.setToColumnId(cancel.getId());
        mv.setEnteredAt(Instant.now());
        movementDao.create(mv);
    }

    /** Bloqueia o card com motivo. */
    public void block(Long cardId, String reason) {
        Card card = cardDao.findById(cardId).orElseThrow(() -> new NoSuchElementException("Card não encontrado"));
        if (card.isLocked()) {
            throw new IllegalStateException("Card já está bloqueado");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Motivo de bloqueio é obrigatório");
        }

        card.setLocked(true);
        cardDao.update(card);

        CardBlock block = new CardBlock();
        block.setCardId(card.getId());
        block.setBlockReason(reason.trim());
        block.setBlockedAt(LocalDateTime.now());
        blockDao.create(block);
    }

    /** Desbloqueia o card e encerra o último bloqueio aberto com o motivo. */
    public void unblock(Long cardId, String reason) {
        Card card = cardDao.findById(cardId).orElseThrow(() -> new NoSuchElementException("Card não encontrado"));
        if (!card.isLocked()) {
            throw new IllegalStateException("Card não está bloqueado");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Motivo de desbloqueio é obrigatório");
        }

        card.setLocked(false);
        cardDao.update(card);

        // Preferir operação específica se existir
        try {
            blockDao.closeOpenBlock(cardId, reason.trim());
        } catch (Throwable t) {
            // fallback: registrar um novo evento de bloqueio com unblockedAt preenchido
            CardBlock b = new CardBlock();
            b.setCardId(card.getId());
            b.setUnblockReason(reason.trim());
            b.setUnblockedAt(LocalDateTime.now());
            blockDao.create(b);
        }
    }

    // --------- helpers ---------

    private Optional<Column> getInitialColumn(Long boardId) {
        return columnsOrdered(boardId).stream()
                .filter(c -> c.getType() == ColumnType.INITIAL)
                .findFirst();
    }

    private List<Column> columnsOrdered(Long boardId) {
        List<Column> cols = columnDao.findByBoard(boardId);
        cols.sort(Comparator.comparingInt(Column::getOrd));
        return cols;
    }

    private int indexOfColumn(List<Column> cols, Long columnId) {
        for (int i = 0; i < cols.size(); i++) {
            if (Objects.equals(cols.get(i).getId(), columnId)) return i;
        }
        throw new IllegalStateException("Coluna atual não pertence ao board");
    }

    public void moveCard(Card card, Board board) {
    }

    public Card moveCardToColumn(Long cardId, Long toColumnId) {
        Card card = cardDao.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Card não encontrado"));

        if (card.isLocked()) {
            throw new IllegalStateException("Card está bloqueado");
        }

        Long fromId = card.getColumnId();
        card.setColumnId(toColumnId);
        cardDao.update(card);

        CardMovement mv = new CardMovement();
        mv.setCardId(card.getId());
        mv.setFromColumnId(fromId);
        mv.setToColumnId(toColumnId);
        mv.setEnteredAt(java.time.Instant.now());
        movementDao.create(mv);

        return card;
    }

    public Card findById(Long cardId) {
        return cardDao.findById(cardId).orElse(null);
    }


}
