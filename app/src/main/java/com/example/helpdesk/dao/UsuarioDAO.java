package com.example.helpdesk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.helpdesk.database.DatabaseHelper;
import com.example.helpdesk.models.Usuario;

public class UsuarioDAO {
    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long cadastrarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nome", usuario.getNome());
        values.put("email", usuario.getEmail());
        values.put("senha", usuario.getSenha());
        values.put("tipo_usuario", usuario.getTipoUsuario());
        values.put("data_cadastro", DatabaseHelper.dataHoraAtual());

        return db.insert(DatabaseHelper.TABELA_USUARIOS, null, values);
    }

    public Usuario autenticar(String email, String senha) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE email = ? AND senha = ?",
                new String[]{email, senha})) {
            return cursor.moveToFirst() ? cursorParaUsuario(cursor) : null;
        }
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT id_usuario FROM usuarios WHERE email = ?",
                new String[]{email})) {
            return cursor.moveToFirst();
        }
    }

    private Usuario cursorParaUsuario(Cursor cursor) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
        usuario.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
        usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        usuario.setSenha(cursor.getString(cursor.getColumnIndexOrThrow("senha")));
        usuario.setTipoUsuario(cursor.getString(cursor.getColumnIndexOrThrow("tipo_usuario")));
        usuario.setDataCadastro(cursor.getString(cursor.getColumnIndexOrThrow("data_cadastro")));
        return usuario;
    }
}
