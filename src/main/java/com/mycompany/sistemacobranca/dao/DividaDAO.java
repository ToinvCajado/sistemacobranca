/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.dao;

import com.mycompany.sistemacobranca.model.Cliente;
import com.mycompany.sistemacobranca.model.Divida;
import com.mycompany.sistemacobranca.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DividaDAO {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    // ✔ Inserir dívida
    public void inserir(Divida divida) throws SQLException {
        String sql = "INSERT INTO divida (idCredor, idDevedor, dataAtualizacao, valorDivida) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, divida.getCredor().getIdCliente());
            stmt.setInt(2, divida.getDevedor().getIdCliente());
            stmt.setDate(3, new java.sql.Date(divida.getDataAtualizacao().getTime()));
            stmt.setDouble(4, divida.getValorDivida());
            stmt.executeUpdate();
        }
    }

    // ✔ Listar todas as dívidas
    public List<Divida> listarTodas() throws SQLException {
        String sql = "SELECT * FROM divida";
        return obterDividasPorQuery(sql, null);
    }

    // ✔ Excluir dívida
    public void excluir(int codigo) throws SQLException {
        String sql = "DELETE FROM divida WHERE codigo = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }

    // ✔ Listar dívidas não pagas
    public List<Divida> listarDividasNaoPagas() throws SQLException {
        String sql = """
            SELECT d.* FROM divida d
            LEFT JOIN pagamento p ON d.codigo = p.idDivida
            WHERE p.idpag IS NULL
        """;
        return obterDividasPorQuery(sql, null);
    }

    // ✔ Listar dívidas por documento
    public List<Divida> listarDividasPorDocumento(String documento) throws SQLException {
        String sql = """
            SELECT d.* FROM divida d
            INNER JOIN cliente c1 ON d.idCredor = c1.idCliente
            INNER JOIN cliente c2 ON d.idDevedor = c2.idCliente
            WHERE c1.documento = ? OR c2.documento = ?
        """;

        return obterDividasPorQuery(sql, ps -> {
            ps.setString(1, documento);
            ps.setString(2, documento);
        });
    }

    // 🔧 Método interno que executa qualquer SELECT de dívidas
    private List<Divida> obterDividasPorQuery(String sql, ParamSetter paramSetter) throws SQLException {
        List<Divida> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (paramSetter != null) {
                paramSetter.setParameters(stmt);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cliente credor = clienteDAO.buscarPorDocumentoPorId(rs.getInt("idCredor"));
                    Cliente devedor = clienteDAO.buscarPorDocumentoPorId(rs.getInt("idDevedor"));
                    Divida d = new Divida(
                            rs.getInt("codigo"),
                            credor,
                            devedor,
                            rs.getDate("dataAtualizacao"),
                            rs.getDouble("valorDivida")
                    );
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    // 🔧 Interface funcional simples para parametrizar consultas
    @FunctionalInterface
    private interface ParamSetter {

        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    public Divida buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM divida WHERE codigo = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cliente credor = clienteDAO.buscarPorDocumentoPorId(rs.getInt("idCredor"));
                Cliente devedor = clienteDAO.buscarPorDocumentoPorId(rs.getInt("idDevedor"));
                return new Divida(
                        rs.getInt("codigo"),
                        credor,
                        devedor,
                        rs.getDate("dataAtualizacao"),
                        rs.getDouble("valorDivida")
                );
            }
        }
        return null;
    }

    public void atualizar(Divida divida) throws SQLException {
        String sql = "UPDATE divida SET dataAtualizacao = ?, valorDivida = ? WHERE codigo = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(divida.getDataAtualizacao().getTime()));
            stmt.setDouble(2, divida.getValorDivida());
            stmt.setInt(3, divida.getCodigo());

            stmt.executeUpdate();
        }
    }

    public boolean temPagamentoVinculado(int idDivida) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM pagamento WHERE idDivida = ?";
        try (Connection conn = Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDivida);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }

}
