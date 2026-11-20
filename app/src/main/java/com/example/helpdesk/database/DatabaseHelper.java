package com.example.helpdesk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "helpdesk_mobile.db";
    public static final int DB_VERSION = 1;

    public static final String TABELA_USUARIOS = "usuarios";
    public static final String TABELA_CHAMADOS = "chamados";
    public static final String TABELA_HISTORICO = "historico_chamados";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "senha TEXT NOT NULL, " +
                "tipo_usuario TEXT NOT NULL, " +
                "data_cadastro TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE chamados (" +
                "id_chamado INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_usuario INTEGER NOT NULL, " +
                "titulo TEXT NOT NULL, " +
                "descricao TEXT NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "prioridade TEXT NOT NULL, " +
                "local TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "resposta_tecnico TEXT, " +
                "id_tecnico INTEGER, " +
                "data_abertura TEXT NOT NULL, " +
                "data_atualizacao TEXT, " +
                "anexo_imagem TEXT, " +
                "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario), " +
                "FOREIGN KEY (id_tecnico) REFERENCES usuarios(id_usuario)" +
                ")");

        db.execSQL("CREATE TABLE historico_chamados (" +
                "id_historico INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_chamado INTEGER NOT NULL, " +
                "id_usuario_responsavel INTEGER NOT NULL, " +
                "acao TEXT NOT NULL, " +
                "observacao TEXT, " +
                "status_anterior TEXT, " +
                "status_novo TEXT, " +
                "data_acao TEXT NOT NULL, " +
                "FOREIGN KEY (id_chamado) REFERENCES chamados(id_chamado), " +
                "FOREIGN KEY (id_usuario_responsavel) REFERENCES usuarios(id_usuario)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS historico_chamados");
        db.execSQL("DROP TABLE IF EXISTS chamados");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    public void seedDadosIniciais() {
        SQLiteDatabase db = getWritableDatabase();

        long idUsuario = garantirUsuario(db, "João Silva", "joao@email.com", "123456", "usuario");
        long idTecnico = garantirUsuario(db, "Maria Técnica", "tecnico@email.com", "123456", "tecnico");
        garantirUsuario(db, "Admin Sistema", "admin@email.com", "123456", "admin");

        if (contarRegistros(db, TABELA_CHAMADOS) == 0) {
            inserirChamadoInicial(db, idUsuario, "Computador não liga",
                    "O computador do laboratório 2 não está ligando.",
                    "Informática", "Alta", "Laboratório 2", "Aberto", null, null);

            inserirChamadoInicial(db, idUsuario, "Vazamento no banheiro",
                    "Existe um vazamento próximo à pia do banheiro masculino.",
                    "Hidráulica", "Média", "Banheiro térreo", "Em andamento",
                    "Chamado recebido. A equipe técnica irá verificar o problema ainda hoje.", idTecnico);

            inserirChamadoInicial(db, idUsuario, "Lâmpada queimada",
                    "A lâmpada da sala 101 está queimada.",
                    "Elétrica", "Baixa", "Sala 101", "Aberto", null, null);
        }
    }

    public static String dataHoraAtual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private long garantirUsuario(SQLiteDatabase db, String nome, String email, String senha, String tipoUsuario) {
        long idExistente = buscarIdUsuarioPorEmail(db, email);
        if (idExistente > 0) {
            return idExistente;
        }

        ContentValues values = new ContentValues();
        values.put("nome", nome);
        values.put("email", email);
        values.put("senha", senha);
        values.put("tipo_usuario", tipoUsuario);
        values.put("data_cadastro", dataHoraAtual());
        return db.insert(TABELA_USUARIOS, null, values);
    }

    private void inserirChamadoInicial(SQLiteDatabase db, long idUsuario, String titulo, String descricao,
                                       String categoria, String prioridade, String local, String status,
                                       String respostaTecnico, Long idTecnico) {
        ContentValues values = new ContentValues();
        values.put("id_usuario", idUsuario);
        values.put("titulo", titulo);
        values.put("descricao", descricao);
        values.put("categoria", categoria);
        values.put("prioridade", prioridade);
        values.put("local", local);
        values.put("status", status);
        values.put("resposta_tecnico", respostaTecnico);
        if (idTecnico != null) {
            values.put("id_tecnico", idTecnico);
        }
        values.put("data_abertura", dataHoraAtual());
        values.put("data_atualizacao", dataHoraAtual());
        db.insert(TABELA_CHAMADOS, null, values);
    }

    private int contarRegistros(SQLiteDatabase db, String tabela) {
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tabela, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private long buscarIdUsuarioPorEmail(SQLiteDatabase db, String email) {
        try (Cursor cursor = db.rawQuery(
                "SELECT id_usuario FROM usuarios WHERE email = ?",
                new String[]{email})) {
            return cursor.moveToFirst() ? cursor.getLong(0) : -1;
        }
    }
}
