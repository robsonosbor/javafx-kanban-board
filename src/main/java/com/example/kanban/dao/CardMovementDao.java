package com.example.kanban.dao;

import com.example.kanban.model.CardMovement;
import java.util.List;
import java.util.Optional;

public interface CardMovementDao {
    CardMovement create(CardMovement m);
    List<CardMovement> findByCard(Long cardId);
    Optional<CardMovement> findLastMovement(Long cardId);
}

