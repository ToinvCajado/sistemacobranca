/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.dao;

import com.mycompany.sistemacobranca.model.Pagamento;
import com.mycompany.sistemacobranca.model.Divida;
import com.mycompany.sistemacobranca.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; // Import específico para java.sql.Date
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagamentoDAO {

    private final DividaDAO dividaDAO = new DividaDAO();

    // ✔ Inserir novo pagamento
    public void inserir(Pagamento pagamento) throws SQLException {
        String sql = "INSERT INTO pagamento (idDivida, dataPagamento, valorPago) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pagamento.getDivida().getCodigo());
            stmt.setDate(2, new Date(pagamento.getDataPagamento().getTime())); // Convertendo para java.sql.Date
            stmt.setDouble(3, pagamento.getValorPago());
            stmt.executeUpdate();
        }
    }

    // ✔ Listar todos os pagamentos
    public List<Pagamento> listarTodos() throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagamento";

        // Melhorando a busca de dívidas — cache de dívidas
        List<Divida> dividas = dividaDAO.listarTodas();
        Map<Integer, Divida> mapaDividas = dividas.stream()
                .collect(Collectors.toMap(Divida::getCodigo, d -> d));

        try (Connection conn = Conexao.getConexao(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idDivida = rs.getInt("idDivida");
                Divida d = mapaDividas.get(idDivida);

                Pagamento p = new Pagamento(
                        rs.getInt("idpag"),
                        d,
                        rs.getDate("dataPagamento"),
                        rs.getDouble("valorPago")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    // ✔ Excluir pagamento
    public void excluir(int idpag) throws SQLException {
        String sql = "DELETE FROM pagamento WHERE idpag = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idpag);
            stmt.executeUpdate();
        }
    }

    // ✔ Consultar faturamento por período
    public double consultarFaturamentoPorPeriodo(java.util.Date inicio, java.util.Date fim) throws SQLException {
        String sql = "SELECT SUM(valorPago) AS total FROM pagamento WHERE dataPagamento BETWEEN ? AND ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new Date(inicio.getTime())); // Conversão correta
            stmt.setDate(2, new Date(fim.getTime()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public List<Pagamento> listarPorIdDivida(int idDivida) throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagamento WHERE idDivida = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDivida);
            ResultSet rs = stmt.executeQuery();

            DividaDAO dividaDAO = new DividaDAO();
            Divida divida = dividaDAO.buscarPorId(idDivida);

            while (rs.next()) {
                Pagamento p = new Pagamento(
                        rs.getInt("idpag"),
                        divida,
                        rs.getDate("dataPagamento"),
                        rs.getDouble("valorPago")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    public void atualizar(Pagamento pagamento) throws SQLException {
        String sql = "UPDATE pagamento SET dataPagamento = ?, valorPago = ? WHERE idpag = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(pagamento.getDataPagamento().getTime()));
            stmt.setDouble(2, pagamento.getValorPago());
            stmt.setInt(3, pagamento.getIdpag());

            stmt.executeUpdate();
        }
    }

}
