/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.model;

import java.util.Date;

public class Divida {
    private int codigo;
    private Cliente credor;
    private Cliente devedor;
    private Date dataAtualizacao;
    private double valorDivida;

    public Divida() {}

    public Divida(int codigo, Cliente credor, Cliente devedor, Date dataAtualizacao, double valorDivida) {
        this.codigo = codigo;
        this.credor = credor;
        this.devedor = devedor;
        this.dataAtualizacao = dataAtualizacao;
        this.valorDivida = valorDivida;
    }

    // Getters e Setters
    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public Cliente getCredor() {
        return credor;
    }
    public void setCredor(Cliente credor) {
        this.credor = credor;
    }
    public Cliente getDevedor() {
        return devedor;
    }
    public void setDevedor(Cliente devedor) {
        this.devedor = devedor;
    }
    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    public double getValorDivida() {
        return valorDivida;
    }
    public void setValorDivida(double valorDivida) {
        this.valorDivida = valorDivida;
    }
    @Override
public String toString() {
    return "Cód: " + codigo +
           " | Data: " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(dataAtualizacao) +
           " | Valor: R$ " + String.format("%.2f", valorDivida);
}
}

