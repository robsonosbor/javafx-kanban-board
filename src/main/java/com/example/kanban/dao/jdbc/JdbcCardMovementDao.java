package com.example.kanban.dao.jdbc;

import com.example.kanban.dao.CardMovementDao;
import com.example.kanban.model.CardMovement;
import com.example.kanban.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCardMovementDao implements CardMovementDao {

    public JdbcCardMovementDao() {
    }

    @Override
    public CardMovement create(CardMovement m) {
        String sql = "INSERT INTO card_movements (card_id, from_column_id, to_column_id) VALUES (?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, m.getCardId());
            if (m.getFromColumnId() != null) {
                ps.setLong(2, m.getFromColumnId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setLong(3, m.getToColumnId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getLong(1));
                }
            }
            return m;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar movimento de card", e);
        }
    }

    @Override
    public List<CardMovement> findByCard(Long cardId) {
        List<CardMovement> list = new ArrayList<>();
        String sql = "SELECT * FROM card_movements WHERE card_id=? ORDER BY moved_at";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentos", e);
        }
        return list;
    }

    @Override
    public Optional<CardMovement> findLastMovement(Long cardId) {
        String sql = "SELECT * FROM card_movements WHERE card_id=? ORDER BY moved_at DESC LIMIT 1";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar último movimento", e);
        }
        return Optional.empty();
    }

    private CardMovement mapRow(ResultSet rs) throws SQLException {
        return new CardMovement(
                rs.getLong("id"),
                rs.getLong("card_id"),
                rs.getObject("from_column_id") != null ? rs.getLong("from_column_id") : null,
                rs.getLong("to_column_id"),
                rs.getTimestamp("moved_at").toLocalDateTime()
        );
    }
}

