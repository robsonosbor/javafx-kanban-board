package com.example.kanban.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class ReportDialogs {

    public static void showTimeReport(Long boardId) {
        String text = ""
            + "Este relatório resume as movimentações por card.\n"
            + "Dica: para um relatório mais robusto, crie uma view SQL que una cards e card_movements.\n";
        info("Relatório de Tempo", text);
    }

    public static void showBlockReport(Long boardId) {
        String text = ""
            + "Este relatório apresenta bloqueios por card.\n"
            + "Para produção, junte cards e card_blocks por board_id.\n";
        info("Relatório de Bloqueios", text);
    }

    private static void info(String title, String text) {
        var area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.getDialogPane().setContent(area);
        alert.setHeaderText(title);
        alert.showAndWait();
    }
}
