package com.example.helpdesk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.helpdesk.models.Usuario;

public class SessaoUsuario {
    private static final String PREF_NAME = "sessao_helpdesk";
    private static final String KEY_LOGADO = "logado";
    private static final String KEY_ID = "id_usuario";
    private static final String KEY_NOME = "nome";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TIPO = "tipo_usuario";

    private final SharedPreferences preferences;

    public SessaoUsuario(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void salvar(Usuario usuario) {
        preferences.edit()
                .putBoolean(KEY_LOGADO, true)
                .putInt(KEY_ID, usuario.getIdUsuario())
                .putString(KEY_NOME, usuario.getNome())
                .putString(KEY_EMAIL, usuario.getEmail())
                .putString(KEY_TIPO, usuario.getTipoUsuario())
                .apply();
    }

    public boolean estaLogado() {
        return preferences.getBoolean(KEY_LOGADO, false);
    }

    public Usuario getUsuario() {
        if (!estaLogado()) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(preferences.getInt(KEY_ID, -1));
        usuario.setNome(preferences.getString(KEY_NOME, ""));
        usuario.setEmail(preferences.getString(KEY_EMAIL, ""));
        usuario.setTipoUsuario(preferences.getString(KEY_TIPO, "usuario"));
        return usuario;
    }

    public void limpar() {
        preferences.edit().clear().apply();
    }
}
