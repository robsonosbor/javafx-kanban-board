package com.example.kanban.dao.jdbc;

import com.example.kanban.dao.BoardDao;
import com.example.kanban.model.Board;
import com.example.kanban.util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBoardDao implements BoardDao {

    public JdbcBoardDao() {
    }

    @Override
    public Board create(Board b) {
        String sql = "INSERT INTO boards(name) VALUES (?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) b.setId(rs.getLong(1));
            }
            return b;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteAll() {
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM boards")) {
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM boards WHERE id=?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar card", e);
        }
    }

    @Override
    public List<Board> findAll() {
        String sql = "SELECT id, name, created_at FROM boards ORDER BY created_at DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Board> list = new ArrayList<>();
            while (rs.next()) {
                Board b = new Board();
                b.setId(rs.getLong("id"));
                b.setName(rs.getString("name"));
                b.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                list.add(b);
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Board> findById(Long id) {
        String sql = "SELECT id, name, created_at FROM boards WHERE id=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Board b = new Board();
                    b.setId(rs.getLong("id"));
                    b.setName(rs.getString("name"));
                    b.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                    return Optional.of(b);
                }
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}