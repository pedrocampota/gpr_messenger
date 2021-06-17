package com.ispgaya.messenger.notificacoes;

public class Dados {

    private String utilizador, corpo, titulo, enviado, tipoNotificacao;
    private Integer icone;

    public Dados() {
    }

    public Dados(String utilizador, String corpo, String titulo, String enviado, String tipoNotificacao, Integer icone) {
        this.utilizador = utilizador;
        this.corpo = corpo;
        this.titulo = titulo;
        this.enviado = enviado;
        this.tipoNotificacao = tipoNotificacao;
        this.icone = icone;
    }

    public String getUtilizador() {
        return utilizador;
    }

    public void setUtilizador(String utilizador) {
        this.utilizador = utilizador;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public String getTipoNotificacao() {
        return tipoNotificacao;
    }

    public void setTipoNotificacao(String tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }

    public Integer getIcone() {
        return icone;
    }

    public void setIcone(Integer icone) {
        this.icone = icone;
    }
}
