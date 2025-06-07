/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.dao.ClienteDAO;
import com.mycompany.sistemacobranca.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClienteForm extends JInternalFrame {

    private JTextField txtNome, txtEndereco, txtUF, txtTelefone, txtDocumento, txtEmail, txtPesquisar;
    private JButton btnSalvar, btnExcluir, btnPesquisar, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Integer idClienteSelecionado = null;

    public ClienteForm() {
        super("Cadastro de Cliente", true, true, true, true);
        setSize(850, 550);
        initComponents();
        carregarClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel superior - Formulário
        JPanel painelForm = new JPanel(new GridLayout(7, 2, 10, 10));
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));

        txtNome = new JTextField();
        txtEndereco = new JTextField();
        txtUF = new JTextField();
        txtTelefone = new JTextField();
        txtDocumento = new JTextField();
        txtEmail = new JTextField();

        painelForm.add(new JLabel("Nome:"));
        painelForm.add(txtNome);
        painelForm.add(new JLabel("Endereço:"));
        painelForm.add(txtEndereco);
        painelForm.add(new JLabel("UF:"));
        painelForm.add(txtUF);
        painelForm.add(new JLabel("Telefone:"));
        painelForm.add(txtTelefone);
        painelForm.add(new JLabel("Documento:"));
        painelForm.add(txtDocumento);
        painelForm.add(new JLabel("E-mail:"));
        painelForm.add(txtEmail);

        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir Selecionado");

        painelForm.add(btnSalvar);
        painelForm.add(btnExcluir);

        add(painelForm, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Endereço", "UF", "Telefone", "Documento", "E-mail"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
        add(scroll, BorderLayout.CENTER);

        // Painel de consulta
        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por Documento"));

        txtPesquisar = new JTextField(20);
        btnPesquisar = new JButton("Pesquisar");
        btnLimpar = new JButton("Limpar");

        painelConsulta.add(new JLabel("Documento:"));
        painelConsulta.add(txtPesquisar);
        painelConsulta.add(btnPesquisar);
        painelConsulta.add(btnLimpar);

        add(painelConsulta, BorderLayout.SOUTH);

        // Eventos
        btnSalvar.addActionListener(e -> salvarOuAtualizarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnPesquisar.addActionListener(e -> pesquisarCliente());
        btnLimpar.addActionListener(e -> limparCampos());

        tabela.getSelectionModel().addListSelectionListener(e -> carregarDadosSelecionados());
    }

    private void salvarOuAtualizarCliente() {
        try {
            Cliente c = new Cliente();
            c.setNomeCliente(txtNome.getText());
            c.setEndereco(txtEndereco.getText());
            c.setUf(txtUF.getText());
            c.setTelefone(txtTelefone.getText());
            c.setDocumento(txtDocumento.getText());
            c.setEmail(txtEmail.getText());

            ClienteDAO dao = new ClienteDAO();

            if (idClienteSelecionado == null) {
                dao.inserir(c);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            } else {
                c.setIdCliente(idClienteSelecionado);
                dao.atualizar(c);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            }

            limparCampos();
            carregarClientes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void excluirCliente() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    new ClienteDAO().excluir(id);
                    JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
                    limparCampos();
                    carregarClientes();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
        }
    }

    private void carregarClientes() {
        try {
            List<Cliente> lista = new ClienteDAO().listarTodos();
            preencherTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void pesquisarCliente() {
        try {
            String doc = txtPesquisar.getText();
            List<Cliente> lista;
            if (doc.isEmpty()) {
                lista = new ClienteDAO().listarTodos();
            } else {
                lista = new ClienteDAO().buscarPorDocumento(doc);
            }
            preencherTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
        }
    }

    private void preencherTabela(List<Cliente> lista) {
        modeloTabela.setRowCount(0);
        for (Cliente c : lista) {
            modeloTabela.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNomeCliente(),
                    c.getEndereco(),
                    c.getUf(),
                    c.getTelefone(),
                    c.getDocumento(),
                    c.getEmail()
            });
        }
    }

    private void carregarDadosSelecionados() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            idClienteSelecionado = (int) modeloTabela.getValueAt(linha, 0);
            txtNome.setText((String) modeloTabela.getValueAt(linha, 1));
            txtEndereco.setText((String) modeloTabela.getValueAt(linha, 2));
            txtUF.setText((String) modeloTabela.getValueAt(linha, 3));
            txtTelefone.setText((String) modeloTabela.getValueAt(linha, 4));
            txtDocumento.setText((String) modeloTabela.getValueAt(linha, 5));
            txtEmail.setText((String) modeloTabela.getValueAt(linha, 6));
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEndereco.setText("");
        txtUF.setText("");
        txtTelefone.setText("");
        txtDocumento.setText("");
        txtEmail.setText("");
        txtPesquisar.setText("");
        tabela.clearSelection();
        idClienteSelecionado = null;
    }
}











//------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.ClienteDAO;
//import com.mycompany.sistemacobranca.model.Cliente;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ClienteForm extends JInternalFrame {
//
//    private JTextField txtNome, txtEndereco, txtUF, txtTelefone, txtDocumento, txtEmail, txtPesquisar;
//    private JButton btnSalvar, btnExcluir, btnPesquisar;
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public ClienteForm() {
//        super("Cadastro de Cliente", true, true, true, true);
//        setSize(850, 550);
//        initComponents();
//        carregarClientes();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        // Painel do formulário
//        JPanel painelForm = new JPanel(new GridLayout(7, 2, 10, 10));
//        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
//
//        txtNome = new JTextField();
//        txtEndereco = new JTextField();
//        txtUF = new JTextField();
//        txtTelefone = new JTextField();
//        txtDocumento = new JTextField();
//        txtEmail = new JTextField();
//
//        painelForm.add(new JLabel("Nome:"));
//        painelForm.add(txtNome);
//        painelForm.add(new JLabel("Endereço:"));
//        painelForm.add(txtEndereco);
//        painelForm.add(new JLabel("UF:"));
//        painelForm.add(txtUF);
//        painelForm.add(new JLabel("Telefone:"));
//        painelForm.add(txtTelefone);
//        painelForm.add(new JLabel("Documento:"));
//        painelForm.add(txtDocumento);
//        painelForm.add(new JLabel("E-mail:"));
//        painelForm.add(txtEmail);
//
//        btnSalvar = new JButton("Salvar");
//        btnExcluir = new JButton("Excluir Selecionado");
//
//        painelForm.add(btnSalvar);
//        painelForm.add(btnExcluir);
//
//        add(painelForm, BorderLayout.NORTH);
//
//        // Tabela
//        modeloTabela = new DefaultTableModel(
//                new String[]{"ID", "Nome", "Endereço", "UF", "Telefone", "Documento", "E-mail"}, 0) {
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        JScrollPane scroll = new JScrollPane(tabela);
//        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
//        add(scroll, BorderLayout.CENTER);
//
//        // Painel de consulta
//        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por Documento"));
//
//        txtPesquisar = new JTextField(20);
//        btnPesquisar = new JButton("Pesquisar");
//
//        painelConsulta.add(new JLabel("Documento:"));
//        painelConsulta.add(txtPesquisar);
//        painelConsulta.add(btnPesquisar);
//
//        add(painelConsulta, BorderLayout.SOUTH);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvarCliente());
//        btnExcluir.addActionListener(e -> excluirCliente());
//        btnPesquisar.addActionListener(e -> pesquisarCliente());
//    }
//
//    private void salvarCliente() {
//        try {
//            Cliente c = new Cliente();
//            c.setNomeCliente(txtNome.getText());
//            c.setEndereco(txtEndereco.getText());
//            c.setUf(txtUF.getText());
//            c.setTelefone(txtTelefone.getText());
//            c.setDocumento(txtDocumento.getText());
//            c.setEmail(txtEmail.getText());
//
//            new ClienteDAO().inserir(c);
//            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
//            limparCampos();
//            carregarClientes();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente: " + ex.getMessage());
//        }
//    }
//
//    private void excluirCliente() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
//            if (opcao == JOptionPane.YES_OPTION) {
//                try {
//                    new ClienteDAO().excluir(id);
//                    JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
//                    carregarClientes();
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage());
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
//        }
//    }
//
//    private void carregarClientes() {
//        try {
//            List<Cliente> lista = new ClienteDAO().listarTodos();
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
//        }
//    }
//
//    private void pesquisarCliente() {
//        try {
//            String doc = txtPesquisar.getText();
//            List<Cliente> lista;
//            if (doc.isEmpty()) {
//                lista = new ClienteDAO().listarTodos();
//            } else {
//                lista = new ClienteDAO().buscarPorDocumento(doc);
//            }
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
//        }
//    }
//
//    private void preencherTabela(List<Cliente> lista) {
//        modeloTabela.setRowCount(0);
//        for (Cliente c : lista) {
//            modeloTabela.addRow(new Object[]{
//                    c.getIdCliente(),
//                    c.getNomeCliente(),
//                    c.getEndereco(),
//                    c.getUf(),
//                    c.getTelefone(),
//                    c.getDocumento(),
//                    c.getEmail()
//            });
//        }
//    }
//
//    private void limparCampos() {
//        txtNome.setText("");
//        txtEndereco.setText("");
//        txtUF.setText("");
//        txtTelefone.setText("");
//        txtDocumento.setText("");
//        txtEmail.setText("");
//    }
//}












//------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.ClienteDAO;
//import com.mycompany.sistemacobranca.model.Cliente;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class ClienteForm extends JInternalFrame {
//
//    private JTextField txtNome, txtEndereco, txtUF, txtTelefone, txtDocumento, txtEmail;
//    private JButton btnSalvar, btnExcluir;
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public ClienteForm() {
//        super("Cadastro de Cliente", true, true, true, true);
//        setSize(800, 500);
//        initComponents();
//        carregarClientes();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        JPanel painelForm = new JPanel(new GridLayout(7, 2, 10, 10));
//        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
//
//        txtNome = new JTextField();
//        txtEndereco = new JTextField();
//        txtUF = new JTextField();
//        txtTelefone = new JTextField();
//        txtDocumento = new JTextField();
//        txtEmail = new JTextField();
//
//        painelForm.add(new JLabel("Nome:"));
//        painelForm.add(txtNome);
//        painelForm.add(new JLabel("Endereço:"));
//        painelForm.add(txtEndereco);
//        painelForm.add(new JLabel("UF:"));
//        painelForm.add(txtUF);
//        painelForm.add(new JLabel("Telefone:"));
//        painelForm.add(txtTelefone);
//        painelForm.add(new JLabel("Documento:"));
//        painelForm.add(txtDocumento);
//        painelForm.add(new JLabel("E-mail:"));
//        painelForm.add(txtEmail);
//
//        btnSalvar = new JButton("Salvar");
//        btnExcluir = new JButton("Excluir Selecionado");
//
//        painelForm.add(btnSalvar);
//        painelForm.add(btnExcluir);
//
//        add(painelForm, BorderLayout.NORTH);
//
//        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "UF", "Telefone", "Documento", "E-mail"}, 0) {
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        JScrollPane scroll = new JScrollPane(tabela);
//        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
//        add(scroll, BorderLayout.CENTER);
//
//        btnSalvar.addActionListener(e -> salvarCliente());
//        btnExcluir.addActionListener(e -> excluirCliente());
//    }
//
//    private void salvarCliente() {
//        try {
//            Cliente c = new Cliente();
//            c.setNomeCliente(txtNome.getText());
//            c.setEndereco(txtEndereco.getText());
//            c.setUf(txtUF.getText());
//            c.setTelefone(txtTelefone.getText());
//            c.setDocumento(txtDocumento.getText());
//            c.setEmail(txtEmail.getText());
//
//            new ClienteDAO().inserir(c);
//            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
//            limparCampos();
//            carregarClientes();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente: " + ex.getMessage());
//        }
//    }
//
//    private void excluirCliente() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
//            if (opcao == JOptionPane.YES_OPTION) {
//                try {
//                    new ClienteDAO().excluir(id);
//                    JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
//                    carregarClientes();
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage());
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
//        }
//    }
//
//    private void carregarClientes() {
//        try {
//            List<Cliente> lista = new ClienteDAO().listarTodos();
//            modeloTabela.setRowCount(0);
//            for (Cliente c : lista) {
//                modeloTabela.addRow(new Object[]{
//                    c.getIdCliente(),
//                    c.getNomeCliente(),
//                    c.getEndereco(),
//                    c.getUf(),
//                    c.getTelefone(),
//                    c.getDocumento(),
//                    c.getEmail()
//                });
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
//        }
//    }
//
//    private void limparCampos() {
//        txtNome.setText("");
//        txtEndereco.setText("");
//        txtUF.setText("");
//        txtTelefone.setText("");
//        txtDocumento.setText("");
//        txtEmail.setText("");
//    }
//}
//
