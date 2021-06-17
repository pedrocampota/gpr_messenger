package com.ispgaya.messenger.models;

public class ModelConversa {
    String mensagem, destinatario, remetente, timestamp, tipo;

    public ModelConversa() {
    }

    public ModelConversa(String mensagem, String destinatario, String remetente, String timestamp, String tipo) {
        this.mensagem = mensagem;
        this.destinatario = destinatario;
        this.remetente = remetente;
        this.timestamp = timestamp;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
