package com.example.kanban.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        try (InputStream in = Db.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties props = new Properties();
            if (in != null) props.load(in);
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/board");
            user = props.getProperty("db.user", "root");
            password = props.getProperty("db.password", "changeme");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar app.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}