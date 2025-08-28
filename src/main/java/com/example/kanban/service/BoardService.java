
package com.example.kanban.service;

import com.example.kanban.dao.BoardDao;
import com.example.kanban.dao.CardDao;
import com.example.kanban.dao.ColumnDao;
import com.example.kanban.model.Board;
import com.example.kanban.model.Card;
import com.example.kanban.model.Column;
import com.example.kanban.model.ColumnType;
import com.example.kanban.dao.jdbc.JdbcCardDao;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Camada de serviço para regras de negócio de Boards/Colunas.
 * Orquestra DAOs e valida as regras antes de persistir.
 */
public class BoardService {

    private final BoardDao boardDao;
    private final ColumnDao columnDao;
    private CardDao cardDao;
    private final JdbcCardDao jdbcCardDao;

    public BoardService(BoardDao boardDao, ColumnDao columnDao) {
        this.boardDao = Objects.requireNonNull(boardDao);
        this.columnDao = Objects.requireNonNull(columnDao);

        this.jdbcCardDao = new JdbcCardDao();
    }

    /**
     * Cria um board com colunas já validadas e com a ordem definida.
     */
    public Board createBoard(String name, List<Column> columns) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome obrigatório");
        if (columns == null || columns.size() < 3) throw new IllegalArgumentException("Mínimo 3 colunas");
        var ordered = new ArrayList<>(columns);
        ordered.sort(Comparator.comparingInt(Column::getOrd));
        validateColumns(ordered);

        Board b = new Board();
        b.setName(name.trim());
        b.setCreatedAt(Instant.now());
        b = boardDao.create(b);

        int ord = 0;
        for (Column c : ordered) {
            c.setBoardId(b.getId());
            c.setOrd(ord++);
            columnDao.create(c);
        }
        return b;
    }

    /**
     * Cria um board com layout "padrão": Inicial, [Pendente...], Final, Cancelamento.
     */
    public Board createBoardWithDefaults(String name, List<String> pendentes) {
        List<Column> cols = new ArrayList<>();
        cols.add(new Column(null, null, "Inicial", 0, ColumnType.INITIAL));
        if (pendentes != null) {
            for (String p : pendentes) {
                if (p != null && !p.isBlank()) {
                    cols.add(new Column(null, null, p.trim(), 0, ColumnType.PENDING));
                }
            }
        }
        if (cols.stream().noneMatch(c -> c.getType() == ColumnType.PENDING)) {
            cols.add(new Column(null, null, "Em andamento", 0, ColumnType.PENDING));
        }
        cols.add(new Column(null, null, "Concluídas", 0, ColumnType.FINAL));
        cols.add(new Column(null, null, "Canceladas", 0, ColumnType.CANCEL));
        return createBoard(name, cols);
    }

    /**
     * Lista boards
     */
    public List<Board> listBoards() {
        return boardDao.findAll();
    }

    public Optional<Board> findBoard(Long id) {
        return boardDao.findById(id);
    }

    /**
     * Retorna as colunas do board já ordenadas por 'ord'
     */

    public List<Column> listColumns(Long boardId) {
        List<Column> cols = columnDao.findByBoard(boardId);
        cols.sort(Comparator.comparingInt(Column::getOrd));

        // popula os cards de cada coluna a partir do DAO de cards
        for (Column c : cols) {
            // se você tem um CardDao de instância, use cardDao.findByBoardAndColumn(...)
            // aqui uso a chamada estática que já existe no seu projeto (se aplicável):
            try {
                List<Card> cards = jdbcCardDao.findByBoardAndColumn(boardId, c.getId());
                c.setCards(cards);
            } catch (Exception e) {
                // fallback seguro: lista vazia
                c.setCards(new ArrayList<>());
            }
        }
        return cols;
    }


    /**
     * Valida as regras de colunas conforme enunciado.
     */
    private void validateColumns(List<Column> cols) {
        long inicial = cols.stream().filter(c -> c.getType() == ColumnType.INITIAL).count();
        long cancel = cols.stream().filter(c -> c.getType() == ColumnType.CANCEL).count();
        long fin = cols.stream().filter(c -> c.getType() == ColumnType.FINAL).count();
        if (inicial != 1 || cancel != 1 || fin != 1)
            throw new IllegalArgumentException("Exatamente 1 INITIAL, 1 FINAL e 1 CANCEL");

        int last = cols.size() - 1, penultimate = cols.size() - 2;
        if (cols.getFirst().getType() != ColumnType.INITIAL)
            throw new IllegalArgumentException("Primeira deve ser INITIAL");
        if (cols.get(penultimate).getType() != ColumnType.FINAL)
            throw new IllegalArgumentException("Penúltima deve ser FINAL");
        if (cols.get(last).getType() != ColumnType.CANCEL)
            throw new IllegalArgumentException("Última deve ser CANCEL");
        for (int i = 1; i < cols.size() - 2; i++)
            if (cols.get(i).getType() != ColumnType.PENDING)
                throw new IllegalArgumentException("Entre INITIAL e FINAL só PENDING");
    }

    public void deleteBoard(Long id) {
        // Se quiser sem depender de ON DELETE CASCADE:
        columnDao.deleteByBoard(id); // já apaga cards das colunas (transação)
        boardDao.delete(id);
    }

}
