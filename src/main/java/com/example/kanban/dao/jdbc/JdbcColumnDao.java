package com.example.kanban.dao.jdbc;

import com.example.kanban.dao.ColumnDao;
import com.example.kanban.model.Column;
import com.example.kanban.model.ColumnType;
import com.example.kanban.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcColumnDao implements ColumnDao {

    public JdbcColumnDao() {
    }

    private Column mapRow(ResultSet rs) throws SQLException {
        return new Column(
                rs.getLong("id"),
                rs.getLong("board_id"),
                rs.getString("name"),
                rs.getInt("ord"),
                ColumnType.valueOf(rs.getString("type"))
        );
    }

    @Override
    public Column create(Column c) {
        String sql = "INSERT INTO columns (board_id, name, ord, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, c.getBoardId());
            ps.setString(2, c.getName());
            ps.setInt(3, c.getOrd());
            ps.setString(4, c.getType().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getLong(1));
                }
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar coluna", e);
        }
    }

    @Override
    public void deleteByBoard(Long boardId) {
        String deleteCardsSql = "DELETE FROM cards WHERE column_id IN (SELECT id FROM columns WHERE board_id = ?)";
        String deleteColumnsSql = "DELETE FROM columns WHERE board_id = ?";

        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false); // 🔹 Garantir atomicidade (transação)

            try (PreparedStatement psCards = conn.prepareStatement(deleteCardsSql);
                 PreparedStatement psColumns = conn.prepareStatement(deleteColumnsSql)) {

                // Primeiro, apagar os cards das colunas do board
                psCards.setLong(1, boardId);
                psCards.executeUpdate();

                // Agora, apagar as colunas do board
                psColumns.setLong(1, boardId);
                psColumns.executeUpdate();

                conn.commit(); // 🔹 Confirma transação
            } catch (SQLException e) {
                conn.rollback(); // 🔹 Reverte se der erro
                throw new RuntimeException("Erro ao excluir colunas e cards do board: " + boardId, e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão ao excluir colunas/cards do board: " + boardId, e);
        }
    }


    @Override
    public void update(Column c) {
        String sql = "UPDATE columns SET name=?, ord=?, type=? WHERE id=?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getOrd());
            ps.setString(3, c.getType().name());
            ps.setLong(4, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar coluna", e);
        }
    }

    @Override
    public List<Column> findByBoard(Long boardId) {
        List<Column> list = new ArrayList<>();
        String sql = "SELECT * FROM columns WHERE board_id=? ORDER BY ord";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar colunas", e);
        }
        return list;
    }

    @Override
    public Optional<Column> findById(Long id) {
        String sql = "SELECT * FROM columns WHERE id=?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar coluna", e);
        }
        return Optional.empty();
    }
}

