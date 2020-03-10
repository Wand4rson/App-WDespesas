package br.com.wdespesas.wdespesas.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Master on 08/08/2016.
 */
public class Despesa implements Serializable{

    private int codigo;
    private String descricao;
    private int categoria;
    private int parcela;
    private double valorparcela;
    private Date datalancamento;
    private Date datavencimento;
    private Date datapagamento;
    private String pago;
    private String chavedocparcelas;
    private int qtdeparcelas;


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getParcela() {
        return parcela;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela;
    }

    public double getValorparcela() {
        return valorparcela;
    }

    public void setValorparcela(double valorparcela) {
        this.valorparcela = valorparcela;
    }

    public Date getDatalancamento() {
        return datalancamento;
    }

    public void setDatalancamento(Date datalancamento) {
        this.datalancamento = datalancamento;
    }

    public Date getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = datavencimento;
    }

    public Date getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(Date datapagamento) {
        this.datapagamento = datapagamento;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getChavedocparcelas() {
        return chavedocparcelas;
    }

    public void setChavedocparcelas(String chavedocparcelas) {
        this.chavedocparcelas = chavedocparcelas;
    }

    public int getQtdeparcelas() {
        return qtdeparcelas;
    }

    public void setQtdeparcelas(int qtdeparcelas) {
        this.qtdeparcelas = qtdeparcelas;
    }
}
