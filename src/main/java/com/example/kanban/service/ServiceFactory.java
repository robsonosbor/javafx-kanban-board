package com.example.kanban.service;

import com.example.kanban.dao.*;
import com.example.kanban.dao.jdbc.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ServiceFactory {

    private final BoardService boardService;
    private final CardService cardService;

    public ServiceFactory() {
        DataSource dataSource = buildDataSource();

        // DAOs JDBC
        BoardDao boardDao = new JdbcBoardDao();
        ColumnDao columnDao = new JdbcColumnDao();
        CardDao cardDao = new JdbcCardDao();
        CardMovementDao cardMovementDao = new JdbcCardMovementDao();
        CardBlockDao cardBlockDao = new JdbcCardBlockDao();

        // Services
        this.boardService = new BoardService(boardDao, columnDao);
        this.cardService = new CardService(cardDao, columnDao, cardMovementDao, cardBlockDao);
    }

    /** Lê configuração de app.properties e monta DataSource */
    private DataSource buildDataSource() {
        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/app.properties")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo app.properties não encontrado no classpath!");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar app.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");

        try {
            com.mysql.cj.jdbc.MysqlDataSource ds = new com.mysql.cj.jdbc.MysqlDataSource();
            ds.setURL(url);
            ds.setUser(user);
            ds.setPassword(pass);
            return ds;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar DataSource", e);
        }

    }

    // Getters
    public BoardService boardService() {
        return boardService;
    }

    public CardService cardService() {
        return cardService;
    }
}

