package com.example.kanban.dao;

import com.example.kanban.model.Column;
import java.util.List;
import java.util.Optional;

public interface ColumnDao {
    Column create(Column c);
    List<Column> findByBoard(Long boardId);
    void update(Column c);
    void deleteByBoard(Long boardId);
    Optional<Column> findById(Long id);
}