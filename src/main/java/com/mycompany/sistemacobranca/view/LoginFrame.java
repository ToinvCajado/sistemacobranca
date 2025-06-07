/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.dao.UsuarioDAO;
import com.mycompany.sistemacobranca.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public LoginFrame() {
        setTitle("Login - Sistema de Cobrança");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        painel.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        painel.add(txtLogin);

        painel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        painel.add(txtSenha);

        btnEntrar = new JButton("Entrar");
        painel.add(new JLabel()); // espaço vazio
        painel.add(btnEntrar);

        btnEntrar.addActionListener(e -> realizarLogin());

        add(painel);
    }

    private void realizarLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.buscarPorLoginESenha(login, senha);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome() + "!");
                dispose(); // Fecha a tela de login
                new TelaPrincipal(usuario).setVisible(true); // Abre a tela principal
            } else {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados.");
        }
    }
}

