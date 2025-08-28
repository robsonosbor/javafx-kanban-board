package com.example.kanban.ui;

import com.example.kanban.dao.CardBlockDao;
import com.example.kanban.dao.CardDao;
import com.example.kanban.dao.CardMovementDao;
import com.example.kanban.model.Card;
import com.example.kanban.model.CardBlock;
import com.example.kanban.model.CardMovement;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Classe responsável por gerar relatórios em PDF (tarefas e bloqueios).
 */
public class ReportDialogs {

    private final CardMovementDao movementDao;
    private final CardBlockDao blockDao;

    public ReportDialogs(CardDao cardDao, CardMovementDao movementDao, CardBlockDao blockDao) {

        this.movementDao = movementDao;
        this.blockDao = blockDao;
    }

    /**
     * Gera relatório PDF de tempo das tarefas, pedindo local para salvar.
     */
    public void generateTaskDurationReport(Long boardId, Stage stage) {
        File file = chooseFile(stage, "relatorio_tarefas.pdf");
        if (file == null) return;

        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            doc.add(new Paragraph("📊 Relatório de Tempo das Tarefas", titleFont));
            doc.add(new Paragraph("\n"));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            List<Card> cards = CardDao.findByBoard(boardId);
            assert cards != null;
            for (Card c : cards) {
                doc.add(new Paragraph("Card: " + c.getTitle(), new Font(Font.HELVETICA, 14, Font.BOLD)));

                List<CardMovement> moves = movementDao.findByCard(c.getId());
                for (CardMovement mv : moves) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" - Coluna: ").append(mv.getColumn().getName())
                            .append(" | Entrada: ").append(mv.getEnteredAt().format(fmt));

                    if (mv.getLeftAt() != null) {
                        sb.append(" | Saída: ").append(mv.getLeftAt().format(fmt));
                        Duration dur = Duration.between(mv.getEnteredAt(), mv.getLeftAt());
                        sb.append(" | Tempo: ").append(dur.toHours()).append("h");
                    } else {
                        sb.append(" | Ainda ativo");
                    }

                    doc.add(new Paragraph(sb.toString(), normalFont));
                }
                doc.add(new Paragraph("\n"));
            }

            doc.close();
            System.out.println("Relatório de tarefas salvo em: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gera relatório PDF de bloqueios, pedindo local para salvar.
     */
    public void generateCardBlockReport(Long boardId, Stage stage) {
        File file = chooseFile(stage, "relatorio_bloqueios.pdf");
        if (file == null) return;

        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            doc.add(new Paragraph("⛔ Relatório de Bloqueios", titleFont));
            doc.add(new Paragraph("\n"));

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            List<Card> cards = CardDao.findByBoard(boardId);
            assert cards != null;
            for (Card c : cards) {
                doc.add(new Paragraph("Card: " + c.getTitle(), new Font(Font.HELVETICA, 14, Font.BOLD)));

                List<CardBlock> blocks = blockDao.findByCard(c.getId());
                for (CardBlock b : blocks) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" - Motivo: ").append(b.getBlockReason())
                            .append(" | Início: ").append(b.getBlockedAt().format(fmt));

                    if (b.getUnblockedAt() != null) {
                        sb.append(" | Fim: ").append(b.getUnblockedAt().format(fmt))
                                .append(" | Justificativa: ").append(b.getUnblockReason());
                        Duration dur = Duration.between(b.getBlockedAt(), b.getUnblockedAt());
                        sb.append(" | Tempo bloqueado: ").append(dur.toHours()).append("h");
                    } else {
                        sb.append(" | Ainda bloqueado");
                    }

                    doc.add(new Paragraph(sb.toString(), normalFont));
                }
                doc.add(new Paragraph("\n"));
            }

            doc.close();
            System.out.println("Relatório de bloqueios salvo em: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre um FileChooser para o usuário selecionar onde salvar o PDF.
     */
    private File chooseFile(Stage stage, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf"));
        fileChooser.setInitialFileName(defaultFileName);
        return fileChooser.showSaveDialog(stage);
    }
}
