package com.example.helpdesk.utils;

import android.util.Patterns;

public final class Validador {
    private Validador() {
    }

    public static boolean campoVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean emailValido(String email) {
        return !campoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 6;
    }

    public static boolean senhasConferem(String senha, String confirmarSenha) {
        return senha != null && senha.equals(confirmarSenha);
    }
}
