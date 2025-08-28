package com.example.kanban.dao;

import com.example.kanban.model.CardBlock;
import java.util.List;
import java.util.Optional;

public interface CardBlockDao {
    CardBlock create(CardBlock b);
    List<CardBlock> findByCard(Long cardId);
    Optional<CardBlock> findLastBlock(Long cardId);
    void closeOpenBlock(Long cardId, String unblockReason);
}
