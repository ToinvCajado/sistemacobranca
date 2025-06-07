/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.sistemacobranca.view;

import com.mycompany.sistemacobranca.dao.DividaDAO;
import com.mycompany.sistemacobranca.dao.PagamentoDAO;
import com.mycompany.sistemacobranca.model.Divida;
import com.mycompany.sistemacobranca.model.Pagamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PagamentoForm extends JInternalFrame {

    private JComboBox<Divida> cbDivida;
    private JTextField txtValor, txtPesquisar;
    private JFormattedTextField txtData;
    private JButton btnSalvar, btnExcluir, btnPesquisar, btnLimpar, btnConsultarFaturamento;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JFormattedTextField txtDataInicio, txtDataFim;
    private JLabel lblResultadoFaturamento;

    private Integer idPagamentoSelecionado = null;

    public PagamentoForm() {
        super("Registro de Pagamento", true, true, true, true);
        setSize(900, 600);
        initComponents();
        carregarDividas();
        carregarPagamentos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Formulário
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Dados do Pagamento"));

        cbDivida = new JComboBox<>();
        txtData = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtData.setValue(new Date());
        txtValor = new JTextField();

        form.add(new JLabel("Dívida:"));
        form.add(cbDivida);
        form.add(new JLabel("Data do Pagamento (yyyy-MM-dd):"));
        form.add(txtData);
        form.add(new JLabel("Valor Pago:"));
        form.add(txtValor);

        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir Selecionado");

        form.add(btnSalvar);
        form.add(btnExcluir);

        add(form, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Dívida", "Data", "Valor"}, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Pagamentos Cadastrados"));
        add(scroll, BorderLayout.CENTER);

        // Painel inferior
        JPanel painelInferior = new JPanel(new GridLayout(2, 1));

        // Faturamento
        JPanel painelFaturamento = new JPanel(new GridLayout(2, 3, 10, 10));
        painelFaturamento.setBorder(BorderFactory.createTitledBorder("Faturamento por Período"));

        txtDataInicio = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDataInicio.setValue(new Date());
        txtDataFim = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtDataFim.setValue(new Date());

        btnConsultarFaturamento = new JButton("Consultar Faturamento");
        lblResultadoFaturamento = new JLabel("Total: R$ 0.00");

        painelFaturamento.add(new JLabel("Data Início:"));
        painelFaturamento.add(txtDataInicio);
        painelFaturamento.add(new JLabel());

        painelFaturamento.add(new JLabel("Data Fim:"));
        painelFaturamento.add(txtDataFim);
        painelFaturamento.add(btnConsultarFaturamento);

        painelFaturamento.add(new JLabel());
        painelFaturamento.add(lblResultadoFaturamento);
        painelFaturamento.add(new JLabel());

        painelInferior.add(painelFaturamento);

        // Consulta
        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por ID da Dívida"));

        txtPesquisar = new JTextField(20);
        btnPesquisar = new JButton("Pesquisar");
        btnLimpar = new JButton("Limpar");

        painelConsulta.add(new JLabel("ID Dívida:"));
        painelConsulta.add(txtPesquisar);
        painelConsulta.add(btnPesquisar);
        painelConsulta.add(btnLimpar);

        painelInferior.add(painelConsulta);

        add(painelInferior, BorderLayout.SOUTH);

        // Eventos
        btnSalvar.addActionListener(e -> salvarOuAtualizarPagamento());
        btnExcluir.addActionListener(e -> excluirPagamento());
        btnPesquisar.addActionListener(e -> pesquisarPorDivida());
        btnLimpar.addActionListener(e -> limparCampos());
        btnConsultarFaturamento.addActionListener(e -> consultarFaturamento());

        tabela.getSelectionModel().addListSelectionListener(e -> carregarDadosSelecionados());
    }

    private void carregarDividas() {
        try {
            DividaDAO dao = new DividaDAO();
            cbDivida.removeAllItems();
            for (Divida d : dao.listarTodas()) {
                cbDivida.addItem(d);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dívidas.");
        }
    }

    private void carregarPagamentos() {
        try {
            PagamentoDAO dao = new PagamentoDAO();
            preencherTabela(dao.listarTodos());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pagamentos.");
        }
    }

    private void salvarOuAtualizarPagamento() {
        try {
            PagamentoDAO dao = new PagamentoDAO();
            Pagamento p = new Pagamento();

            p.setDataPagamento(new SimpleDateFormat("yyyy-MM-dd").parse(txtData.getText()));
            p.setValorPago(Double.parseDouble(txtValor.getText()));

            if (idPagamentoSelecionado == null) {
                Divida d = (Divida) cbDivida.getSelectedItem();
                if (d == null) {
                    JOptionPane.showMessageDialog(this, "Selecione uma dívida.");
                    return;
                }

                p.setDivida(d);
                dao.inserir(p);
                JOptionPane.showMessageDialog(this, "Pagamento registrado.");
            } else {
                p.setIdpag(idPagamentoSelecionado);
                dao.atualizar(p);
                JOptionPane.showMessageDialog(this, "Pagamento atualizado.");
            }

            limparCampos();
            carregarPagamentos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void excluirPagamento() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este pagamento?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    new PagamentoDAO().excluir(id);
                    JOptionPane.showMessageDialog(this, "Pagamento excluído.");
                    limparCampos();
                    carregarPagamentos();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir pagamento: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
        }
    }

    private void consultarFaturamento() {
        try {
            Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataInicio.getText());
            Date fim = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataFim.getText());

            double total = new PagamentoDAO().consultarFaturamentoPorPeriodo(inicio, fim);
            lblResultadoFaturamento.setText(String.format("Total: R$ %.2f", total));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar faturamento: " + ex.getMessage());
        }
    }

    private void pesquisarPorDivida() {
        try {
            String texto = txtPesquisar.getText();
            List<Pagamento> lista;
            if (texto.isEmpty()) {
                lista = new PagamentoDAO().listarTodos();
            } else {
                int idDivida = Integer.parseInt(texto);
                lista = new PagamentoDAO().listarPorIdDivida(idDivida);
            }
            preencherTabela(lista);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Digite um número válido para ID da Dívida.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
        }
    }

    private void preencherTabela(List<Pagamento> lista) {
        modeloTabela.setRowCount(0);
        for (Pagamento p : lista) {
            modeloTabela.addRow(new Object[]{
                    p.getIdpag(),
                    p.getDivida().getCodigo(),
                    new SimpleDateFormat("yyyy-MM-dd").format(p.getDataPagamento()),
                    p.getValorPago()
            });
        }
    }

    private void carregarDadosSelecionados() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            idPagamentoSelecionado = (int) modeloTabela.getValueAt(linha, 0);
            cbDivida.setSelectedItem(buscarDividaPorCodigo((int) modeloTabela.getValueAt(linha, 1)));
            txtData.setText((String) modeloTabela.getValueAt(linha, 2));
            txtValor.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));

            cbDivida.setEnabled(false);
        }
    }

    private Divida buscarDividaPorCodigo(int codigo) {
        for (int i = 0; i < cbDivida.getItemCount(); i++) {
            Divida d = cbDivida.getItemAt(i);
            if (d.getCodigo() == codigo) {
                return d;
            }
        }
        return null;
    }

    private void limparCampos() {
        txtData.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtValor.setText("");
        txtPesquisar.setText("");
        tabela.clearSelection();
        idPagamentoSelecionado = null;
        cbDivida.setEnabled(true);
    }
}







//---------------------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.DividaDAO;
//import com.mycompany.sistemacobranca.dao.PagamentoDAO;
//import com.mycompany.sistemacobranca.model.Divida;
//import com.mycompany.sistemacobranca.model.Pagamento;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//public class PagamentoForm extends JInternalFrame {
//
//    private JComboBox<Divida> cbDivida;
//    private JTextField txtValor, txtPesquisar;
//    private JFormattedTextField txtData;
//    private JButton btnSalvar, btnExcluir, btnConsultarFaturamento, btnPesquisar;
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    private JFormattedTextField txtDataInicio, txtDataFim;
//    private JLabel lblResultadoFaturamento;
//
//    public PagamentoForm() {
//        super("Registro de Pagamento", true, true, true, true);
//        setSize(850, 600);
//        initComponents();
//        carregarDividas();
//        carregarPagamentos();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        // Formulário de pagamento
//        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
//        form.setBorder(BorderFactory.createTitledBorder("Novo Pagamento"));
//
//        cbDivida = new JComboBox<>();
//        txtData = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtData.setValue(new Date());
//        txtValor = new JTextField();
//
//        form.add(new JLabel("Dívida:"));
//        form.add(cbDivida);
//        form.add(new JLabel("Data Pagamento (yyyy-MM-dd):"));
//        form.add(txtData);
//        form.add(new JLabel("Valor Pago:"));
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
//        // Tabela de pagamentos
//        modeloTabela = new DefaultTableModel(new String[]{"ID", "Dívida", "Data", "Valor"}, 0) {
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//        tabela = new JTable(modeloTabela);
//        add(new JScrollPane(tabela), BorderLayout.CENTER);
//
//        // Painel de faturamento + consulta
//        JPanel painelInferior = new JPanel(new GridLayout(2, 1));
//
//        // Faturamento
//        JPanel painelFaturamento = new JPanel(new GridLayout(2, 3, 10, 10));
//        painelFaturamento.setBorder(BorderFactory.createTitledBorder("Faturamento por Período"));
//
//        txtDataInicio = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtDataInicio.setValue(new Date());
//        txtDataFim = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtDataFim.setValue(new Date());
//
//        btnConsultarFaturamento = new JButton("Consultar Faturamento");
//        lblResultadoFaturamento = new JLabel("Total: R$ 0.00");
//
//        painelFaturamento.add(new JLabel("Data Início:"));
//        painelFaturamento.add(txtDataInicio);
//        painelFaturamento.add(new JLabel());
//
//        painelFaturamento.add(new JLabel("Data Fim:"));
//        painelFaturamento.add(txtDataFim);
//        painelFaturamento.add(btnConsultarFaturamento);
//
//        painelFaturamento.add(new JLabel());
//        painelFaturamento.add(lblResultadoFaturamento);
//        painelFaturamento.add(new JLabel());
//
//        painelInferior.add(painelFaturamento);
//
//        // Consulta por ID da Dívida
//        JPanel painelConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        painelConsulta.setBorder(BorderFactory.createTitledBorder("Consulta por ID da Dívida"));
//
//        txtPesquisar = new JTextField(20);
//        btnPesquisar = new JButton("Pesquisar");
//
//        painelConsulta.add(new JLabel("ID Dívida:"));
//        painelConsulta.add(txtPesquisar);
//        painelConsulta.add(btnPesquisar);
//
//        painelInferior.add(painelConsulta);
//
//        add(painelInferior, BorderLayout.SOUTH);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvar());
//        btnExcluir.addActionListener(e -> excluir());
//        btnConsultarFaturamento.addActionListener(e -> consultarFaturamento());
//        btnPesquisar.addActionListener(e -> pesquisarPorDivida());
//    }
//
//    private void carregarDividas() {
//        try {
//            DividaDAO dao = new DividaDAO();
//            cbDivida.removeAllItems();
//            for (Divida d : dao.listarTodas()) {
//                cbDivida.addItem(d);
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar dívidas.");
//        }
//    }
//
//    private void carregarPagamentos() {
//        try {
//            PagamentoDAO dao = new PagamentoDAO();
//            preencherTabela(dao.listarTodos());
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar pagamentos.");
//        }
//    }
//
//    private void salvar() {
//        try {
//            Divida d = (Divida) cbDivida.getSelectedItem();
//            if (d == null) {
//                JOptionPane.showMessageDialog(this, "Selecione uma dívida.");
//                return;
//            }
//
//            Pagamento p = new Pagamento();
//            p.setDivida(d);
//            p.setDataPagamento(new SimpleDateFormat("yyyy-MM-dd").parse(txtData.getText()));
//            p.setValorPago(Double.parseDouble(txtValor.getText()));
//
//            new PagamentoDAO().inserir(p);
//            JOptionPane.showMessageDialog(this, "Pagamento registrado.");
//            carregarPagamentos();
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao registrar pagamento: " + ex.getMessage());
//        }
//    }
//
//    private void excluir() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            try {
//                new PagamentoDAO().excluir(id);
//                JOptionPane.showMessageDialog(this, "Pagamento excluído.");
//                carregarPagamentos();
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(this, "Erro ao excluir pagamento: " + ex.getMessage());
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
//        }
//    }
//
//    private void consultarFaturamento() {
//        try {
//            Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataInicio.getText());
//            Date fim = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataFim.getText());
//
//            double total = new PagamentoDAO().consultarFaturamentoPorPeriodo(inicio, fim);
//            lblResultadoFaturamento.setText(String.format("Total: R$ %.2f", total));
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao consultar faturamento: " + ex.getMessage());
//        }
//    }
//
//    private void pesquisarPorDivida() {
//        try {
//            String texto = txtPesquisar.getText();
//            List<Pagamento> lista;
//            if (texto.isEmpty()) {
//                lista = new PagamentoDAO().listarTodos();
//            } else {
//                int idDivida = Integer.parseInt(texto);
//                lista = new PagamentoDAO().listarPorIdDivida(idDivida);
//            }
//            preencherTabela(lista);
//        } catch (NumberFormatException nfe) {
//            JOptionPane.showMessageDialog(this, "Digite um número válido para ID da Dívida.");
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro na pesquisa: " + ex.getMessage());
//        }
//    }
//
//    private void preencherTabela(List<Pagamento> lista) {
//        modeloTabela.setRowCount(0);
//        for (Pagamento p : lista) {
//            modeloTabela.addRow(new Object[]{
//                    p.getIdpag(),
//                    p.getDivida().getCodigo(),
//                    new SimpleDateFormat("yyyy-MM-dd").format(p.getDataPagamento()),
//                    p.getValorPago()
//            });
//        }
//    }
//}












//-------------------------------

//package com.mycompany.sistemacobranca.view;
//
//import com.mycompany.sistemacobranca.dao.DividaDAO;
//import com.mycompany.sistemacobranca.dao.PagamentoDAO;
//import com.mycompany.sistemacobranca.model.Divida;
//import com.mycompany.sistemacobranca.model.Pagamento;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.sql.SQLException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//public class PagamentoForm extends JInternalFrame {
//
//    private JComboBox<Divida> cbDivida;
//    private JTextField txtValor;
//    private JFormattedTextField txtData;
//    private JButton btnSalvar, btnExcluir;
//
//    // Componentes de faturamento
//    private JFormattedTextField txtDataInicio, txtDataFim;
//    private JButton btnConsultarFaturamento;
//    private JLabel lblResultadoFaturamento;
//
//    private JTable tabela;
//    private DefaultTableModel modeloTabela;
//
//    public PagamentoForm() {
//        super("Registro de Pagamento", true, true, true, true);
//        setSize(800, 600);
//        initComponents();
//        carregarDividas();
//        carregarPagamentos();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout());
//
//        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
//        form.setBorder(BorderFactory.createTitledBorder("Novo Pagamento"));
//
//        cbDivida = new JComboBox<>();
//        txtData = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtData.setValue(new Date());
//        txtValor = new JTextField();
//
//        form.add(new JLabel("Dívida:"));
//        form.add(cbDivida);
//        form.add(new JLabel("Data Pagamento (yyyy-MM-dd):"));
//        form.add(txtData);
//        form.add(new JLabel("Valor Pago:"));
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
//        modeloTabela = new DefaultTableModel(new String[]{"ID", "Dívida", "Data", "Valor"}, 0) {
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//        tabela = new JTable(modeloTabela);
//        add(new JScrollPane(tabela), BorderLayout.CENTER);
//
//        // Painel de faturamento
//        JPanel painelFaturamento = new JPanel(new GridLayout(2, 3, 10, 10));
//        painelFaturamento.setBorder(BorderFactory.createTitledBorder("Faturamento por Período"));
//
//        txtDataInicio = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtDataInicio.setValue(new Date());
//        txtDataFim = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
//        txtDataFim.setValue(new Date());
//
//        btnConsultarFaturamento = new JButton("Consultar Faturamento");
//        lblResultadoFaturamento = new JLabel("Total: R$ 0.00");
//
//        painelFaturamento.add(new JLabel("Data Início (yyyy-MM-dd):"));
//        painelFaturamento.add(txtDataInicio);
//        painelFaturamento.add(new JLabel());
//
//        painelFaturamento.add(new JLabel("Data Fim (yyyy-MM-dd):"));
//        painelFaturamento.add(txtDataFim);
//        painelFaturamento.add(btnConsultarFaturamento);
//
//        painelFaturamento.add(new JLabel());
//        painelFaturamento.add(lblResultadoFaturamento);
//        painelFaturamento.add(new JLabel());
//
//        add(painelFaturamento, BorderLayout.SOUTH);
//
//        // Eventos
//        btnSalvar.addActionListener(e -> salvar());
//        btnExcluir.addActionListener(e -> excluir());
//        btnConsultarFaturamento.addActionListener(e -> consultarFaturamento());
//    }
//
//    private void carregarDividas() {
//        try {
//            DividaDAO dao = new DividaDAO();
//            cbDivida.removeAllItems();
//            for (Divida d : dao.listarTodas()) {
//                cbDivida.addItem(d);
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar dívidas.");
//        }
//    }
//
//    private void carregarPagamentos() {
//        try {
//            PagamentoDAO dao = new PagamentoDAO();
//            modeloTabela.setRowCount(0);
//            for (Pagamento p : dao.listarTodos()) {
//                modeloTabela.addRow(new Object[]{
//                    p.getIdpag(),
//                    p.getDivida().getCodigo(),
//                    new SimpleDateFormat("yyyy-MM-dd").format(p.getDataPagamento()),
//                    p.getValorPago()
//                });
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao carregar pagamentos.");
//        }
//    }
//
//    private void salvar() {
//        try {
//            Divida d = (Divida) cbDivida.getSelectedItem();
//            if (d == null) {
//                JOptionPane.showMessageDialog(this, "Selecione uma dívida.");
//                return;
//            }
//
//            Pagamento p = new Pagamento();
//            p.setDivida(d);
//            p.setDataPagamento(new SimpleDateFormat("yyyy-MM-dd").parse(txtData.getText()));
//            p.setValorPago(Double.parseDouble(txtValor.getText()));
//
//            new PagamentoDAO().inserir(p);
//            JOptionPane.showMessageDialog(this, "Pagamento registrado.");
//            carregarPagamentos();
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao registrar pagamento: " + ex.getMessage());
//        }
//    }
//
//    private void excluir() {
//        int linha = tabela.getSelectedRow();
//        if (linha >= 0) {
//            int id = (int) modeloTabela.getValueAt(linha, 0);
//            try {
//                new PagamentoDAO().excluir(id);
//                JOptionPane.showMessageDialog(this, "Pagamento excluído.");
//                carregarPagamentos();
//            } catch (SQLException ex) {
//                JOptionPane.showMessageDialog(this, "Erro ao excluir pagamento: " + ex.getMessage());
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
//        }
//    }
//
//    private void consultarFaturamento() {
//        try {
//            Date inicio = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataInicio.getText());
//            Date fim = new SimpleDateFormat("yyyy-MM-dd").parse(txtDataFim.getText());
//
//            double total = new PagamentoDAO().consultarFaturamentoPorPeriodo(inicio, fim);
//            lblResultadoFaturamento.setText(String.format("Total: R$ %.2f", total));
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Erro ao consultar faturamento: " + ex.getMessage());
//        }
//    }
//}
//
//
