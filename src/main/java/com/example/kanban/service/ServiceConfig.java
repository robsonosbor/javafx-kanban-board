
package com.example.kanban.service;

import com.example.kanban.dao.BoardDao;
import com.example.kanban.dao.CardBlockDao;
import com.example.kanban.dao.CardDao;
import com.example.kanban.dao.CardMovementDao;
import com.example.kanban.dao.ColumnDao;
import com.example.kanban.dao.jdbc.JdbcBoardDao;
import com.example.kanban.dao.jdbc.JdbcCardBlockDao;
import com.example.kanban.dao.jdbc.JdbcCardDao;
import com.example.kanban.dao.jdbc.JdbcCardMovementDao;
import com.example.kanban.dao.jdbc.JdbcColumnDao;

/** Fábrica simples de serviços usando implementações JDBC. */
public class ServiceConfig {

    private static BoardService boardService;
    private static CardService cardService;

    public static BoardService boardService() {
        if (boardService == null) {
            BoardDao b = new JdbcBoardDao();
            ColumnDao c = new JdbcColumnDao();
            boardService = new BoardService(b, c);
        }
        return boardService;
    }

    public static CardService cardService() {
        if (cardService == null) {
            CardDao cardDao = new JdbcCardDao();
            ColumnDao columnDao = new JdbcColumnDao();
            CardMovementDao mvDao = new JdbcCardMovementDao();
            CardBlockDao blockDao = new JdbcCardBlockDao();
            cardService = new CardService(cardDao, columnDao, mvDao, blockDao);
        }
        return cardService;
    }
}
