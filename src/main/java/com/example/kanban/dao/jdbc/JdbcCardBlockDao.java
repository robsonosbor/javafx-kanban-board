package com.example.kanban.dao.jdbc;

import com.example.kanban.dao.CardBlockDao;
import com.example.kanban.model.CardBlock;
import com.example.kanban.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCardBlockDao implements CardBlockDao {

    public JdbcCardBlockDao() {
    }

    @Override
    public CardBlock create(CardBlock b) {
        String sql = "INSERT INTO card_blocks (card_id, reason_block, reason_unblock, blocked_at, unblocked_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, b.getCardId());
            ps.setString(2, b.getReasonBlock());
            ps.setString(3, b.getReasonUnblock());
            if (b.getBlockedAt() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(b.getBlockedAt()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            if (b.getUnblockedAt() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(b.getUnblockedAt()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    b.setId(rs.getLong(1));
                }
            }
            return b;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar bloqueio", e);
        }
    }

    @Override
    public List<CardBlock> findByCard(Long cardId) {
        List<CardBlock> list = new ArrayList<>();
        String sql = "SELECT * FROM card_blocks WHERE card_id=? ORDER BY blocked_at";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bloqueios", e);
        }
        return list;
    }

    @Override
    public Optional<CardBlock> findLastBlock(Long cardId) {
        String sql = "SELECT * FROM card_blocks WHERE card_id=? ORDER BY blocked_at DESC LIMIT 1";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar último bloqueio", e);
        }
        return Optional.empty();
    }

    @Override
    public void closeOpenBlock(Long cardId, String unblockReason) {
        String sql = "UPDATE card_blocks " +
                "SET unblocked_at = ?, unblock_reason = ? " +
                "WHERE card_id = ? AND unblocked_at IS NULL";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(2, unblockReason);
            ps.setLong(3, cardId);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("Nenhum bloqueio aberto encontrado para o card: " + cardId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desbloquear card: " + cardId, e);
        }
    }


    private CardBlock mapRow(ResultSet rs) throws SQLException {
        return new CardBlock(
                rs.getLong("id"),
                rs.getLong("card_id"),
                rs.getString("reason_block"),
                rs.getString("reason_unblock"),
                rs.getTimestamp("blocked_at") != null ? rs.getTimestamp("blocked_at").toLocalDateTime() : null,
                rs.getTimestamp("unblocked_at") != null ? rs.getTimestamp("unblocked_at").toLocalDateTime() : null
        );
    }
}
