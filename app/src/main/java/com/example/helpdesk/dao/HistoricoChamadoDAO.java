package com.example.helpdesk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.helpdesk.database.DatabaseHelper;

public class HistoricoChamadoDAO {
    private final DatabaseHelper dbHelper;

    public HistoricoChamadoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long registrar(int idChamado, int idUsuarioResponsavel, String acao, String observacao,
                          String statusAnterior, String statusNovo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id_chamado", idChamado);
        values.put("id_usuario_responsavel", idUsuarioResponsavel);
        values.put("acao", acao);
        values.put("observacao", observacao);
        values.put("status_anterior", statusAnterior);
        values.put("status_novo", statusNovo);
        values.put("data_acao", DatabaseHelper.dataHoraAtual());

        return db.insert(DatabaseHelper.TABELA_HISTORICO, null, values);
    }
}
