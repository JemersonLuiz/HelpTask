package com.example.helpdesk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.helpdesk.database.DatabaseHelper;
import com.example.helpdesk.models.Chamado;

import java.util.ArrayList;
import java.util.List;

public class ChamadoDAO {
    private final DatabaseHelper dbHelper;

    public ChamadoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long criarChamado(Chamado chamado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id_usuario", chamado.getIdUsuario());
        values.put("titulo", chamado.getTitulo());
        values.put("descricao", chamado.getDescricao());
        values.put("categoria", chamado.getCategoria());
        values.put("prioridade", chamado.getPrioridade());
        values.put("local", chamado.getLocal());
        values.put("status", "Aberto");
        values.put("data_abertura", DatabaseHelper.dataHoraAtual());
        values.put("data_atualizacao", DatabaseHelper.dataHoraAtual());
        if (chamado.getAnexoImagem() != null && !chamado.getAnexoImagem().trim().isEmpty()) {
            values.put("anexo_imagem", chamado.getAnexoImagem().trim());
        }

        return db.insert(DatabaseHelper.TABELA_CHAMADOS, null, values);
    }

    public List<Chamado> listarChamados(Integer idUsuario, String status, String categoria,
                                        String prioridade, String pesquisa) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, u.nome AS nome_solicitante ");
        sql.append("FROM chamados c ");
        sql.append("INNER JOIN usuarios u ON u.id_usuario = c.id_usuario ");
        sql.append("WHERE 1 = 1 ");

        List<String> args = new ArrayList<>();

        if (idUsuario != null) {
            sql.append("AND c.id_usuario = ? ");
            args.add(String.valueOf(idUsuario));
        }
        if (filtroAtivo(status)) {
            sql.append("AND c.status = ? ");
            args.add(status);
        }
        if (filtroAtivo(categoria)) {
            sql.append("AND c.categoria = ? ");
            args.add(categoria);
        }
        if (filtroAtivo(prioridade)) {
            sql.append("AND c.prioridade = ? ");
            args.add(prioridade);
        }
        if (pesquisa != null && !pesquisa.trim().isEmpty()) {
            sql.append("AND (LOWER(c.titulo) LIKE ? OR LOWER(u.nome) LIKE ?) ");
            String termo = "%" + pesquisa.trim().toLowerCase() + "%";
            args.add(termo);
            args.add(termo);
        }

        sql.append("ORDER BY c.id_chamado DESC");

        List<Chamado> chamados = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                chamados.add(cursorParaChamado(cursor));
            }
        }
        return chamados;
    }

    public Chamado buscarPorId(int idChamado) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT c.*, u.nome AS nome_solicitante " +
                "FROM chamados c " +
                "INNER JOIN usuarios u ON u.id_usuario = c.id_usuario " +
                "WHERE c.id_chamado = ?";

        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idChamado)})) {
            return cursor.moveToFirst() ? cursorParaChamado(cursor) : null;
        }
    }

    public boolean cancelarChamadoUsuario(int idChamado, int idUsuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "Cancelado");
        values.put("data_atualizacao", DatabaseHelper.dataHoraAtual());

        int linhas = db.update(DatabaseHelper.TABELA_CHAMADOS, values,
                "id_chamado = ? AND id_usuario = ? AND status = ?",
                new String[]{String.valueOf(idChamado), String.valueOf(idUsuario), "Aberto"});
        return linhas > 0;
    }

    public boolean atualizarAtendimento(int idChamado, int idTecnico, String statusNovo, String respostaTecnico) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", statusNovo);
        values.put("resposta_tecnico", respostaTecnico);
        values.put("id_tecnico", idTecnico);
        values.put("data_atualizacao", DatabaseHelper.dataHoraAtual());

        int linhas = db.update(DatabaseHelper.TABELA_CHAMADOS, values,
                "id_chamado = ?",
                new String[]{String.valueOf(idChamado)});
        return linhas > 0;
    }

    public int contarPorStatus(Integer idUsuario, String status) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM chamados WHERE status = ?");
        List<String> args = new ArrayList<>();
        args.add(status);

        if (idUsuario != null) {
            sql.append(" AND id_usuario = ?");
            args.add(String.valueOf(idUsuario));
        }

        try (Cursor cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]))) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private boolean filtroAtivo(String valor) {
        return valor != null && !valor.trim().isEmpty() && !"Todos".equalsIgnoreCase(valor);
    }

    private Chamado cursorParaChamado(Cursor cursor) {
        Chamado chamado = new Chamado();
        chamado.setIdChamado(cursor.getInt(cursor.getColumnIndexOrThrow("id_chamado")));
        chamado.setIdUsuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
        chamado.setNomeSolicitante(cursor.getString(cursor.getColumnIndexOrThrow("nome_solicitante")));
        chamado.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
        chamado.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow("descricao")));
        chamado.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow("categoria")));
        chamado.setPrioridade(cursor.getString(cursor.getColumnIndexOrThrow("prioridade")));
        chamado.setLocal(cursor.getString(cursor.getColumnIndexOrThrow("local")));
        chamado.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        chamado.setRespostaTecnico(cursor.getString(cursor.getColumnIndexOrThrow("resposta_tecnico")));
        chamado.setDataAbertura(cursor.getString(cursor.getColumnIndexOrThrow("data_abertura")));
        chamado.setDataAtualizacao(cursor.getString(cursor.getColumnIndexOrThrow("data_atualizacao")));
        chamado.setAnexoImagem(cursor.getString(cursor.getColumnIndexOrThrow("anexo_imagem")));

        int idTecnicoIndex = cursor.getColumnIndexOrThrow("id_tecnico");
        if (!cursor.isNull(idTecnicoIndex)) {
            chamado.setIdTecnico(cursor.getInt(idTecnicoIndex));
        }
        return chamado;
    }
}
