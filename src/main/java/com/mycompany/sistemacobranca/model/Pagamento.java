/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.model;

import java.util.Date;

public class Pagamento {
    private int idpag;
    private Divida divida;
    private Date dataPagamento;
    private double valorPago;

    public Pagamento() {}

    public Pagamento(int idpag, Divida divida, Date dataPagamento, double valorPago) {
        this.idpag = idpag;
        this.divida = divida;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
    }

    // Getters e Setters
    public int getIdpag() {
        return idpag;
    }
    public void setIdpag(int idpag) {
        this.idpag = idpag;
    }
    public Divida getDivida() {
        return divida;
    }
    public void setDivida(Divida divida) {
        this.divida = divida;
    }
    public Date getDataPagamento() {
        return dataPagamento;
    }
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    public double getValorPago() {
        return valorPago;
    }
    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }
}

