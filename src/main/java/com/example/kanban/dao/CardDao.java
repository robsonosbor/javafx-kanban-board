package com.example.kanban.dao;

import com.example.kanban.model.Card;
import java.util.List;
import java.util.Optional;

public interface CardDao {
    Card create(Card c);
    static List<Card> findByBoard(long boardId) {
        return null;
    }
    void update(Card c);
    List<Card> findByBoardAndColumn(Long boardId, Long columnId);
    Card findById(long id);
    Optional<Card> findById(Long id);
}