package com.example.kanban.dao.jdbc;

import com.example.kanban.dao.CardDao;
import com.example.kanban.model.Card;
import com.example.kanban.util.Db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCardDao implements CardDao {

    public JdbcCardDao() {
    }

    @Override
    public Card findById(long id) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar card", e);
        }
        return null;
    }

    public static List<Card> findByBoard(long boardId) {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE board_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cards", e);
        }
        return list;
    }

    // 🔹 Método auxiliar para mapear resultado em objeto Card
    private static Card mapRow(ResultSet rs) throws SQLException {
        return new Card(
                rs.getLong("id"),
                rs.getLong("board_id"),
                rs.getLong("column_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("locked")
        );
    }

    @Override
    public Card create(Card c) {
        String sql = "INSERT INTO cards (board_id, column_id, title, description, created_at, locked) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, c.getBoardId());
            ps.setLong(2, c.getColumnId());
            ps.setString(3, c.getTitle());
            ps.setString(4, c.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(c.getCreatedAt() != null ? c.getCreatedAt() : LocalDateTime.now()));
            ps.setBoolean(6, c.isLocked());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar card", e);
        }
    }

    @Override
    public void update(Card c) {
        String sql = "UPDATE cards SET board_id=?, column_id=?, title=?, description=?, locked=? WHERE id=?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, c.getBoardId());
            ps.setLong(2, c.getColumnId());
            ps.setString(3, c.getTitle());
            ps.setString(4, c.getDescription());
            ps.setBoolean(5, c.isLocked());
            ps.setLong(6, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar card", e);
        }
    }

    public List<Card> findByBoardAndColumn(Long boardId, Long columnId) {
        List<Card> list = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE board_id = ? AND column_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, boardId);
            ps.setLong(2, columnId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cards por board e coluna", e);
        }
        System.out.println(list);
        return list;
    }

    @Override
    public Optional<Card> findById(Long id) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar card por id", e);
        }
        return Optional.empty();
    }

    public static void delete(Long id) {
        String sql = "DELETE FROM cards WHERE id=?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar card", e);
        }
    }
}
