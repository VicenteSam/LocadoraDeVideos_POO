package org.Sistema;

import org.Usuario.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Locacao extends Cliente {
    private String codigoLocacao;
    private String nomeCliente;


    public void getCodigoLocacao(String pessoa, Connection connection) {
        try{
            String sqlSelect = "SELECT CodigoFilme, Login, Titulo FROM Carrinho WHERE Login = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(sqlSelect)) {
                selectStmt.setString(1, pessoa);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        String titulo = rs.getString("Titulo");
                        String codigoFilme = rs.getString("CodigoFilme");
                        this.codigoLocacao = pessoa + codigoFilme;


                        String sqlInsert = "INSERT INTO Locado(LoginF, Titulo, CodLocacao) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)) {
                        insertStmt.setString(1, pessoa);
                        insertStmt.setString(2, titulo);
                        insertStmt.setString(3, this.codigoLocacao);
                        insertStmt.executeUpdate();

                        String sqlDeleteDesejado = "DELETE FROM Carrinho WHERE Login = ? AND CodigoFilme = ?";
                        try (PreparedStatement deleteDesejadoStmt = connection.prepareStatement(sqlDeleteDesejado)) {
                            deleteDesejadoStmt.setString(1, pessoa);
                            deleteDesejadoStmt.setString(2, codigoFilme);
                            deleteDesejadoStmt.executeUpdate();
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    }
                }
            }
            System.out.println("CÓDIGO DE LOCAÇÃO: " + this.codigoLocacao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getNomeCliente(String pessoa, Connection connection) {
        try{
            String sqlSelect = "SELECT Nome FROM Pessoa WHERE Login = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(sqlSelect)) {
                selectStmt.setString(1, pessoa);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        this.nomeCliente = rs.getString("Nome");
                    }
                }
                String sqlUpdate = "UPDATE Locado SET Nome = ? WHERE LoginF = ?";
                try (PreparedStatement insertStmt = connection.prepareStatement(sqlUpdate)) {
                    insertStmt.setString(1, this.nomeCliente);
                    insertStmt.setString(2, pessoa);
                    insertStmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("NOME: " + this.nomeCliente);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDataLocacao(String pessoa, Connection connection) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String dataLocacao = dtf.format(now);

        String sqlUpdate = "UPDATE Locado SET DataLocacao = ? WHERE LoginF = ?";
        try (PreparedStatement insertStmt = connection.prepareStatement(sqlUpdate)) {
            insertStmt.setString(1, dataLocacao);
            insertStmt.setString(2, pessoa);
            insertStmt.executeUpdate();
            System.out.println("Data de Locação: " + dataLocacao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDataDevolucao(String pessoa, Connection connection) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime mesDevolucao = now.plusMonths(1);

        String dataDevolucao = dtf.format(mesDevolucao);
        String sqlUpdate = "UPDATE Locado SET DataDevolucao = ? WHERE LoginF = ?";
        try (PreparedStatement insertStmt = connection.prepareStatement(sqlUpdate)) {
            insertStmt.setString(1, dataDevolucao);
            insertStmt.setString(2, pessoa);
            insertStmt.executeUpdate();
            System.out.println("Data de Devolução: " + dataDevolucao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
