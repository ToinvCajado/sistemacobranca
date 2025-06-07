/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.model.Usuario;

import javax.swing.*;

public class TelaPrincipal extends JFrame {

    private JDesktopPane desktop;

    public TelaPrincipal(Usuario usuario) {
        setTitle("Sistema de Cobrança - Usuário: " + usuario.getNome());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        desktop = new JDesktopPane();
        setContentPane(desktop);

        JMenuBar menuBar = new JMenuBar();

        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem miUsuarios = new JMenuItem("Usuários");
        JMenuItem miClientes = new JMenuItem("Clientes");
        JMenuItem miDividas = new JMenuItem("Dívidas");
        JMenuItem miPagamentos = new JMenuItem("Pagamentos");

        miUsuarios.addActionListener(e -> abrirJanela("Usuário"));
        miClientes.addActionListener(e -> abrirJanela("Cliente"));
        miDividas.addActionListener(e -> abrirJanela("Dívida"));
        miPagamentos.addActionListener(e -> abrirJanela("Pagamento"));

        menuCadastros.add(miUsuarios);
        menuCadastros.add(miClientes);
        menuCadastros.add(miDividas);
        menuCadastros.add(miPagamentos);

        // Menu Consultas (em breve)


        // As ações das consultas serão implementadas depois
        // Exemplo: miFaturamento.addActionListener(e -> abrirConsultaFaturamento());

        menuBar.add(menuCadastros);


        setJMenuBar(menuBar);
    }

    private void abrirJanela(String titulo) {
        JInternalFrame janela = null;

        switch (titulo) {
            case "Usuário":
                janela = new UsuarioForm();
                break;
            case "Cliente":
                janela = new ClienteForm();
                break;
            case "Dívida":
                janela = new DividaForm();
                break;
            case "Pagamento":
                janela = new PagamentoForm();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Tela não encontrada: " + titulo);
                return;
        }

        desktop.add(janela);
        janela.setVisible(true);
        try {
            janela.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}

