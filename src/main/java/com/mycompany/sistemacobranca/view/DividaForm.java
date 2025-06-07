/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.dao.ClienteDAO;
import com.mycompany.sistemacobranca.dao.DividaDAO;
import com.mycompany.sistemacobranca.dao.PagamentoDAO;
import com.mycompany.sistemacobranca.model.Cliente;
import com.mycompany.sistemacobranca.model.Divida;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DividaForm extends JInternalFrame {

    private JComboBox<Cliente> cbCredor, cbDevedor;
    private JTextField txtValor, txtPesquisar;
    private JFormattedTextField txtData;
    private JButton btnSalvar, btnExcluir, btnPesquisar, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Integer idDividaSelecionada = null;

    public DividaForm() {
        super("Cadastro de Dívida", true, true, true, true);
        setSize(950, 600);
        initComponents();
        carregarClientes();
        carregarDividas();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Formulário
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Dados da Dívida"));

        cbCredor = new JComboBox<>();
        cbDevedor = new JComboBox<>();
        txtData = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtData.setValue(new Date());
        txtValor = new JTextField();

        form.add(new JLabel("Credor:"));
        form.add(cbCredor);
        form.add(new JLabel("Devedor:"));
        form.add(cbDevedor);
        form.add(new JLabel("Data de Atualização (yyyy-MM-dd):"));
        form.add(txtData);
        form.add(new JLabel("Valor da Dívida:"));
        form.add(txtValor);

        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir Selecionado");

        form.add(btnSalvar);
        form.add(btnExcluir);

        add(form, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(
                new String[]{"Código", "Credor", "Devedor", "Data", "Valor"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Dívidas Cadastradas"));
        add(scroll, BorderLayout.CENTER);

        // Painel de Consulta
        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por Código da Dívida"));

        txtPesquisar = new JTextField(20);
        btnPesquisar = new JButton("Pesquisar");
        btnLimpar = new JButton("Limpar");

        painelConsulta.add(new JLabel("Código:"));
        painelConsulta.add(txtPesquisar);
        painelConsulta.add(btnPesquisar);
        painelConsulta.add(btnLimpar);

        add(painelConsulta, BorderLayout.SOUTH);

        // Eventos
        btnSalvar.addActionListener(e -> salvarOuAtualizarDivida());
        btnExcluir.addActionListener(e -> excluirDivida());
        btnPesquisar.addActionListener(e -> pesquisarDivida());
        btnLimpar.addActionListener(e -> limparCampos());

        tabela.getSelectionModel().addListSelectionListener(e -> carregarDadosSelecionados());
    }

    private void carregarClientes() {
        try {
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> clientes = dao.listarTodos();
            cbCredor.removeAllItems();
            cbDevedor.removeAllItems();
            for (Cliente c : clientes) {
                cbCredor.addItem(c);
                cbDevedor.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes.");
        }
    }

    private void carregarDividas() {
        try {
            DividaDAO dao = new DividaDAO();
            preencherTabela(dao.listarTodas());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dívidas.");
        }
    }

    private void salvarOuAtualizarDivida() {
        try {
            DividaDAO dao = new DividaDAO();
            Divida d = new Divida();
            d.setDataAtualizacao(new SimpleDateFormat("yyyy-MM-dd").parse(txtData.getText()));
            d.setValorDivida(Double.parseDouble(txtValor.getText()));

            if (idDividaSelecionada == null) {
                // Novo cadastro
                Cliente credor = (Cliente) cbCredor.getSelectedItem();
                Cliente devedor = (Cliente) cbDevedor.getSelectedItem();

                if (credor == null || devedor == null) {
                    JOptionPane.showMessageDialog(this, "Credor e Devedor são obrigatórios.");
                    return;
                }
                if (credor.getIdCliente() == devedor.getIdCliente()) {
                    JOptionPane.showMessageDialog(this, "Credor e Devedor devem ser diferentes.");
                    return;
                }

                d.setCredor(credor);
                d.setDevedor(devedor);

                dao.inserir(d);
                JOptionPane.showMessageDialog(this, "Dívida cadastrada com sucesso!");
            } else {
                // Edição
                d.setCodigo(idDividaSelecionada);

                if (dao.temPagamentoVinculado(idDividaSelecionada)) {
                    JOptionPane.showMessageDialog(this, "Não é possível alterar esta dívida, pois possui pagamentos vinculados.");
                    return;
                }

                dao.atualizar(d);
                JOptionPane.showMessageDialog(this, "Dívida atualizada com sucesso!");
            }

            limparCampos();
            carregarDividas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void excluirDivida() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int codigo = (int) modeloTabela.getValueAt(linha, 0);
            try {
                DividaDAO dao = new DividaDAO();
                if (dao.temPagamentoVinculado(codigo)) {
                    JOptionPane.showMessageDialog(this, "Não é possível excluir. Existem pagamentos vinculados.");
                    return;
                }

                dao.excluir(codigo);
                JOptionPane.showMessageDialog(this, "Dívida excluída com sucesso.");
                limparCampos();
                carregarDividas();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma dívida.");
        }
    }

    private void pesquisarDivida() {
        try {
            String texto = txtPesquisar.getText();
            List<Divida> lista;
            if (texto.isEmpty()) {
                lista = new DividaDAO().listarTodas();
            } else {
                int codigo = Integer.parseInt(texto);
                lista = List.of(new DividaDAO().buscarPorId(codigo));
            }
            preencherTabela(lista);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
        }
    }

    private void preencherTabela(List<Divida> lista) {
        modeloTabela.setRowCount(0);
        for (Divida d : lista) {
            modeloTabela.addRow(new Object[]{
                    d.getCodigo(),
                    d.getCredor().getNomeCliente(),
                    d.getDevedor().getNomeCliente(),
                    new SimpleDateFormat("yyyy-MM-dd").format(d.getDataAtualizacao()),
                    d.getValorDivida()
            });
        }
    }

    private void carregarDadosSelecionados() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            idDividaSelecionada = (int) modeloTabela.getValueAt(linha, 0);
            txtData.setText((String) modeloTabela.getValueAt(linha, 3));
            txtValor.setText(String.valueOf(modeloTabela.getValueAt(linha, 4)));

            // Desabilitar mudança de credor e devedor
            cbCredor.setEnabled(false);
            cbDevedor.setEnabled(false);
        }
    }

    private void limparCampos() {
        txtData.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtValor.setText("");
        txtPesquisar.setText("");
        tabela.clearSelection();
        idDividaSelecionada = null;
        cbCredor.setEnabled(true);
        cbDevedor.setEnabled(true);
    }
}
    








//----------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.ClienteDAO;
//import com.mycompany.sistemacobranca.dao.DividaDAO;
//import com.mycompany.sistemacobranca.model.Cliente;
//import com.mycompany.sistemacobranca.model.Divida;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//public class DividaForm extends JInternalFrame {
//
//    private JComboBox<Cliente> cbCredor, cbDevedor;
//    private JTextField txtValor;
//    private JFormattedTextField txtData;
//    private JButton btnSalvar, btnExcluir;
//
//    // Componentes para consultas
//    private JButton btnDividasNaoPagas;
//    private JTextField txtDocumento;
//    private JButton btnConsultarPorDocumento;
//
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public DividaForm() {
//        super("Cadastro de Dívida", true, true, true, true);
//        setSize(900, 600);
//        initComponents();
//        carregarClientes();
//        carregarDividas();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        // Painel do formulário de cadastro
//        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
//        form.setBorder(BorderFactory.createTitledBorder("Nova Dívida"));
//
//        cbCredor = new JComboBox<>();
//        cbDevedor = new JComboBox<>();
//        txtData = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtData.setValue(new Date());
//        txtValor = new JTextField();
//
//        form.add(new JLabel("Credor:"));
//        form.add(cbCredor);
//        form.add(new JLabel("Devedor:"));
//        form.add(cbDevedor);
//        form.add(new JLabel("Data de Atualização (yyyy-MM-dd):"));
//        form.add(txtData);
//        form.add(new JLabel("Valor da Dívida:"));
//        form.add(txtValor);
//
//        btnSalvar = new JButton("Salvar");
//        btnExcluir = new JButton("Excluir Selecionado");
//
//        form.add(btnSalvar);
//        form.add(btnExcluir);
//
//        add(form, BorderLayout.NORTH);
//
//        // Tabela
//        modeloTabela = new DefaultTableModel(
//                new String[]{"Código", "Credor", "Devedor", "Data", "Valor"}, 0) {
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        add(new JScrollPane(tabela), BorderLayout.CENTER);
//
//        // Painel de consultas
//        JPanel painelConsultas = new JPanel(new GridLayout(2, 3, 10, 10));
//        painelConsultas.setBorder(BorderFactory.createTitledBorder("Consultas"));
//
//        btnDividasNaoPagas = new JButton("Listar Dívidas Não Pagas");
//        txtDocumento = new JTextField();
//        btnConsultarPorDocumento = new JButton("Consultar por Documento");
//
//        painelConsultas.add(btnDividasNaoPagas);
//        painelConsultas.add(new JLabel("Documento:"));
//        painelConsultas.add(txtDocumento);
//        painelConsultas.add(new JLabel());
//        painelConsultas.add(btnConsultarPorDocumento);
//        painelConsultas.add(new JLabel());
//
//        add(painelConsultas, BorderLayout.SOUTH);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvar());
//        btnExcluir.addActionListener(e -> excluir());
//        btnDividasNaoPagas.addActionListener(e -> listarDividasNaoPagas());
//        btnConsultarPorDocumento.addActionListener(e -> consultarPorDocumento());
//    }
//
//    private void carregarClientes() {
//        try {
//            ClienteDAO dao = new ClienteDAO();
//            List<Cliente> clientes = dao.listarTodos();
//            cbCredor.removeAllItems();
//            cbDevedor.removeAllItems();
//            for (Cliente c : clientes) {
//                cbCredor.addItem(c);
//                cbDevedor.addItem(c);
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes.");
//        }
//    }
//
//    private void carregarDividas() {
//        try {
//            DividaDAO dao = new DividaDAO();
//            List<Divida> lista = dao.listarTodas();
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar dívidas.");
//        }
//    }
//
//    private void salvar() {
//        Cliente credor = (Cliente) cbCredor.getSelectedItem();
//        Cliente devedor = (Cliente) cbDevedor.getSelectedItem();
//
//        if (credor == null || devedor == null) {
//            JOptionPane.showMessageDialog(this, "Credor e Devedor são obrigatórios.");
//            return;
//        }
//
//        if (credor.getIdCliente() == devedor.getIdCliente()) {
//            JOptionPane.showMessageDialog(this, "Credor e Devedor devem ser diferentes.");
//            return;
//        }
//
//        try {
//            Divida d = new Divida();
//            d.setCredor(credor);
//            d.setDevedor(devedor);
//            d.setDataAtualizacao(new SimpleDateFormat("yyyy-MM-dd").parse(txtData.getText()));
//            d.setValorDivida(Double.parseDouble(txtValor.getText()));
//
//            new DividaDAO().inserir(d);
//            JOptionPane.showMessageDialog(this, "Dívida registrada com sucesso.");
//            carregarDividas();
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
//        }
//    }
//
//    private void excluir() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int codigo = (int) modeloTabela.getValueAt(linha, 0);
//            try {
//                new DividaDAO().excluir(codigo);
//                JOptionPane.showMessageDialog(this, "Dívida excluída com sucesso.");
//                carregarDividas();
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione uma dívida.");
//        }
//    }
//
//    private void listarDividasNaoPagas() {
//        try {
//            List<Divida> lista = new DividaDAO().listarDividasNaoPagas();
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
//        }
//    }
//
//    private void consultarPorDocumento() {
//        try {
//            String documento = txtDocumento.getText();
//            if (documento.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Informe o documento.");
//                return;
//            }
//            List<Divida> lista = new DividaDAO().listarDividasPorDocumento(documento);
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
//        }
//    }
//
//    private void preencherTabela(List<Divida> lista) {
//        modeloTabela.setRowCount(0);
//        for (Divida d : lista) {
//            modeloTabela.addRow(new Object[]{
//                    d.getCodigo(),
//                    d.getCredor().getNomeCliente(),
//                    d.getDevedor().getNomeCliente(),
//                    new SimpleDateFormat("yyyy-MM-dd").format(d.getDataAtualizacao()),
//                    d.getValorDivida()
//            });
//        }
//    }
//}
//
