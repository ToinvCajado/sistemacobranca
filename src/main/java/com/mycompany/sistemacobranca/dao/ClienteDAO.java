/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.dao;

import com.mycompany.sistemacobranca.model.Cliente;
import com.mycompany.sistemacobranca.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nomeCliente, endereco, uf, telefone, documento, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNomeCliente());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getUf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getDocumento());
            stmt.setString(6, cliente.getEmail());
            stmt.executeUpdate();
        }
    }

    public List<Cliente> buscarPorDocumento(String documento) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE documento LIKE ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + documento + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("nomeCliente"),
                        rs.getString("endereco"),
                        rs.getString("uf"),
                        rs.getString("telefone"),
                        rs.getString("documento"),
                        rs.getString("email")
                );
                lista.add(c);
            }
        }
        return lista;
    }
//    public Cliente buscarPorDocumento(String documento) throws SQLException {
//        String sql = "SELECT * FROM cliente WHERE documento = ?";
//        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1, documento);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return mapCliente(rs);
//            }
//        }
//        return null;
//    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection conn = Conexao.getConexao(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        }
        return lista;
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE idCliente = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("idCliente"),
                rs.getString("nomeCliente"),
                rs.getString("endereco"),
                rs.getString("uf"),
                rs.getString("telefone"),
                rs.getString("documento"),
                rs.getString("email")
        );
    }

    public Cliente buscarPorDocumentoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE idCliente = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCliente(rs);
            }
        }
        return null;
    }

    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nomeCliente = ?, endereco = ?, uf = ?, telefone = ?, documento = ?, email = ? WHERE idCliente = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNomeCliente());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getUf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getDocumento());
            stmt.setString(6, cliente.getEmail());
            stmt.setInt(7, cliente.getIdCliente());

            stmt.executeUpdate();
        }
    }
}
