package br.com.wdespesas.wdespesas.model;

import java.io.Serializable;

/**
 * Created by Master on 08/08/2016.
 */
public class Categoria implements Serializable {

    private int codigo;
    private String descricao;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
