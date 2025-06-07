/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.dao.UsuarioDAO;
import com.mycompany.sistemacobranca.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsuarioForm extends JInternalFrame {

    private JTextField txtNome, txtCargo, txtLogin, txtEmail, txtPesquisar;
    private JPasswordField txtSenha;
    private JButton btnSalvar, btnExcluir, btnPesquisar, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Integer idUsuarioSelecionado = null; // 👉 Controla se está em modo edição

    public UsuarioForm() {
        super("Cadastro de Usuário", true, true, true, true);
        setSize(750, 550);
        initComponents();
        carregarUsuarios();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Painel superior - Formulário
        JPanel painelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));

        txtNome = new JTextField();
        txtCargo = new JTextField();
        txtLogin = new JTextField();
        txtSenha = new JPasswordField();
        txtEmail = new JTextField();

        painelForm.add(new JLabel("Nome:"));
        painelForm.add(txtNome);
        painelForm.add(new JLabel("Cargo:"));
        painelForm.add(txtCargo);
        painelForm.add(new JLabel("Login:"));
        painelForm.add(txtLogin);
        painelForm.add(new JLabel("Senha:"));
        painelForm.add(txtSenha);
        painelForm.add(new JLabel("E-mail:"));
        painelForm.add(txtEmail);

        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir Selecionado");
        btnLimpar = new JButton("Limpar");

        painelForm.add(btnSalvar);
        painelForm.add(btnExcluir);

        add(painelForm, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Cargo", "Login", "E-mail"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));
        add(scroll, BorderLayout.CENTER);

        // Painel de consulta
        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por Nome"));

        txtPesquisar = new JTextField(20);
        btnPesquisar = new JButton("Pesquisar");

        painelConsulta.add(new JLabel("Nome:"));
        painelConsulta.add(txtPesquisar);
        painelConsulta.add(btnPesquisar);
        painelConsulta.add(btnLimpar);

        add(painelConsulta, BorderLayout.SOUTH);

        // Eventos
        btnSalvar.addActionListener(e -> salvarOuAtualizarUsuario());
        btnExcluir.addActionListener(e -> excluirUsuario());
        btnPesquisar.addActionListener(e -> pesquisarUsuarios());
        btnLimpar.addActionListener(e -> limparCampos());

        tabela.getSelectionModel().addListSelectionListener(e -> carregarDadosSelecionados());
    }

    private void salvarOuAtualizarUsuario() {
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(txtNome.getText());
            usuario.setCargo(txtCargo.getText());
            usuario.setLogin(txtLogin.getText());
            usuario.setSenha(new String(txtSenha.getPassword()));
            usuario.setEmail(txtEmail.getText());

            UsuarioDAO dao = new UsuarioDAO();

            if (idUsuarioSelecionado == null) {
                dao.inserir(usuario);
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            } else {
                usuario.setId(idUsuarioSelecionado);
                dao.atualizar(usuario);
                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            }

            limparCampos();
            carregarUsuarios();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void excluirUsuario() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    new UsuarioDAO().excluir(id);
                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.");
                    limparCampos();
                    carregarUsuarios();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
        }
    }

    private void carregarUsuarios() {
        try {
            List<Usuario> lista = new UsuarioDAO().listarTodos();
            preencherTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ex.getMessage());
        }
    }

    private void pesquisarUsuarios() {
        try {
            String nome = txtPesquisar.getText();
            List<Usuario> lista;
            if (nome.isEmpty()) {
                lista = new UsuarioDAO().listarTodos();
            } else {
                lista = new UsuarioDAO().buscarPorNome(nome);
            }
            preencherTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
        }
    }

    private void preencherTabela(List<Usuario> lista) {
        modeloTabela.setRowCount(0);
        for (Usuario u : lista) {
            modeloTabela.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getCargo(),
                    u.getLogin(),
                    u.getEmail()
            });
        }
    }

    private void carregarDadosSelecionados() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            idUsuarioSelecionado = (int) modeloTabela.getValueAt(linha, 0);
            txtNome.setText((String) modeloTabela.getValueAt(linha, 1));
            txtCargo.setText((String) modeloTabela.getValueAt(linha, 2));
            txtLogin.setText((String) modeloTabela.getValueAt(linha, 3));
            txtEmail.setText((String) modeloTabela.getValueAt(linha, 4));
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCargo.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        txtEmail.setText("");
        txtPesquisar.setText("");
        tabela.clearSelection();
        idUsuarioSelecionado = null;
    }
}








//------------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.UsuarioDAO;
//import com.mycompany.sistemacobranca.model.Usuario;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class UsuarioForm extends JInternalFrame {
//
//    private JTextField txtNome, txtCargo, txtLogin, txtEmail, txtPesquisar;
//    private JPasswordField txtSenha;
//    private JButton btnSalvar, btnExcluir, btnPesquisar;
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public UsuarioForm() {
//        super("Cadastro de Usuário", true, true, true, true);
//        setSize(700, 550);
//        initComponents();
//        carregarUsuarios();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        // Painel superior - formulário
//        JPanel painelForm = new JPanel(new GridLayout(6, 2, 10, 10));
//        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
//
//        txtNome = new JTextField();
//        txtCargo = new JTextField();
//        txtLogin = new JTextField();
//        txtSenha = new JPasswordField();
//        txtEmail = new JTextField();
//
//        painelForm.add(new JLabel("Nome:"));
//        painelForm.add(txtNome);
//        painelForm.add(new JLabel("Cargo:"));
//        painelForm.add(txtCargo);
//        painelForm.add(new JLabel("Login:"));
//        painelForm.add(txtLogin);
//        painelForm.add(new JLabel("Senha:"));
//        painelForm.add(txtSenha);
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
//        // Painel do meio - Tabela
//        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Cargo", "Login", "E-mail"}, 0) {
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        JScrollPane scroll = new JScrollPane(tabela);
//        scroll.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));
//        add(scroll, BorderLayout.CENTER);
//
//        // Painel inferior - Consulta
//        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por Nome"));
//
//        txtPesquisar = new JTextField(20);
//        btnPesquisar = new JButton("Pesquisar");
//
//        painelConsulta.add(new JLabel("Nome:"));
//        painelConsulta.add(txtPesquisar);
//        painelConsulta.add(btnPesquisar);
//
//        add(painelConsulta, BorderLayout.SOUTH);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvarUsuario());
//        btnExcluir.addActionListener(e -> excluirUsuario());
//        btnPesquisar.addActionListener(e -> pesquisarUsuarios());
//    }
//
//    private void salvarUsuario() {
//        try {
//            Usuario usuario = new Usuario();
//            usuario.setNome(txtNome.getText());
//            usuario.setCargo(txtCargo.getText());
//            usuario.setLogin(txtLogin.getText());
//            usuario.setSenha(new String(txtSenha.getPassword()));
//            usuario.setEmail(txtEmail.getText());
//
//            new UsuarioDAO().inserir(usuario);
//            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
//            limparCampos();
//            carregarUsuarios();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário: " + ex.getMessage());
//        }
//    }
//
//    private void excluirUsuario() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
//            if (opcao == JOptionPane.YES_OPTION) {
//                try {
//                    new UsuarioDAO().excluir(id);
//                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.");
//                    carregarUsuarios();
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
//        }
//    }
//
//    private void carregarUsuarios() {
//        try {
//            List<Usuario> lista = new UsuarioDAO().listarTodos();
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ex.getMessage());
//        }
//    }
//
//    private void pesquisarUsuarios() {
//        try {
//            String nome = txtPesquisar.getText();
//            List<Usuario> lista;
//            if (nome.isEmpty()) {
//                lista = new UsuarioDAO().listarTodos();
//            } else {
//                lista = new UsuarioDAO().buscarPorNome(nome);
//            }
//            preencherTabela(lista);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
//        }
//    }
//
//    private void preencherTabela(List<Usuario> lista) {
//        modeloTabela.setRowCount(0);
//        for (Usuario u : lista) {
//            modeloTabela.addRow(new Object[]{
//                    u.getId(),
//                    u.getNome(),
//                    u.getCargo(),
//                    u.getLogin(),
//                    u.getEmail()
//            });
//        }
//    }
//
//    private void limparCampos() {
//        txtNome.setText("");
//        txtCargo.setText("");
//        txtLogin.setText("");
//        txtSenha.setText("");
//        txtEmail.setText("");
//    }
//}










//---------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.UsuarioDAO;
//import com.mycompany.sistemacobranca.model.Usuario;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//
//public class UsuarioForm extends JInternalFrame {
//
//    private JTextField txtNome, txtCargo, txtLogin, txtEmail;
//    private JPasswordField txtSenha;
//    private JButton btnSalvar, btnExcluir;
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public UsuarioForm() {
//        super("Cadastro de Usuário", true, true, true, true);
//        setSize(700, 500);
//        initComponents();
//        carregarUsuarios();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        // Painel superior - formulário
//        JPanel painelForm = new JPanel(new GridLayout(6, 2, 10, 10));
//        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
//
//        txtNome = new JTextField();
//        txtCargo = new JTextField();
//        txtLogin = new JTextField();
//        txtSenha = new JPasswordField();
//        txtEmail = new JTextField();
//
//        painelForm.add(new JLabel("Nome:"));
//        painelForm.add(txtNome);
//        painelForm.add(new JLabel("Cargo:"));
//        painelForm.add(txtCargo);
//        painelForm.add(new JLabel("Login:"));
//        painelForm.add(txtLogin);
//        painelForm.add(new JLabel("Senha:"));
//        painelForm.add(txtSenha);
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
//        // Painel inferior - tabela
//        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Cargo", "Login", "E-mail"}, 0) {
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        JScrollPane scroll = new JScrollPane(tabela);
//        scroll.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));
//        add(scroll, BorderLayout.CENTER);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvarUsuario());
//        btnExcluir.addActionListener(e -> excluirUsuario());
//    }
//
//    private void salvarUsuario() {
//        try {
//            Usuario usuario = new Usuario();
//            usuario.setNome(txtNome.getText());
//            usuario.setCargo(txtCargo.getText());
//            usuario.setLogin(txtLogin.getText());
//            usuario.setSenha(new String(txtSenha.getPassword()));
//            usuario.setEmail(txtEmail.getText());
//
//            new UsuarioDAO().inserir(usuario);
//            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
//            limparCampos();
//            carregarUsuarios();
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário: " + ex.getMessage());
//        }
//    }
//
//    private void excluirUsuario() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir?", "Confirmação", JOptionPane.YES_NO_OPTION);
//            if (opcao == JOptionPane.YES_OPTION) {
//                try {
//                    new UsuarioDAO().excluir(id);
//                    JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.");
//                    carregarUsuarios();
//                } catch (SQLException ex) {
//                    JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
//                }
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
//        }
//    }
//
//    private void carregarUsuarios() {
//        try {
//            List<Usuario> lista = new UsuarioDAO().listarTodos();
//            modeloTabela.setRowCount(0);
//            for (Usuario u : lista) {
//                modeloTabela.addRow(new Object[]{
//                    u.getId(),
//                    u.getNome(),
//                    u.getCargo(),
//                    u.getLogin(),
//                    u.getEmail()
//                });
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar usuários: " + ex.getMessage());
//        }
//    }
//
//    private void limparCampos() {
//        txtNome.setText("");
//        txtCargo.setText("");
//        txtLogin.setText("");
//        txtSenha.setText("");
//        txtEmail.setText("");
//    }
//}
//
