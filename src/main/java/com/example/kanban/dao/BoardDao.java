package com.example.kanban.dao;

import com.example.kanban.model.Board;

import java.util.List;
import java.util.Optional;

public interface BoardDao {
    Board create(Board b);
    List<Board> findAll();
    Optional<Board> findById(Long id);
    void delete(Long id);
    void deleteAll();
}
