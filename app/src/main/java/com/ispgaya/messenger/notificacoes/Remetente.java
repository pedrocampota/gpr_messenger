package com.ispgaya.messenger.notificacoes;

public class Remetente {

    private Dados dados;
    private String para;

    public Remetente() {
    }

    public Remetente(Dados dados, String para) {
        this.dados = dados;
        this.para = para;
    }

    public Dados getDados() {
        return dados;
    }

    public void setDados(Dados dados) {
        this.dados = dados;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }
}
