package org.Sistema;

import org.Usuario.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Locacao extends Cliente {
    private String codigoLocacao;
    private String nomeCliente;


    public void getCodigoLocacao(String pessoa, Connection connection) {
        try{
            String sqlSelect = "SELECT CodigoFilme, Login FROM Carrinho WHERE Login = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(sqlSelect)) {
                selectStmt.setString(1, pessoa);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        String codigoFilme = rs.getString("CodigoFilme");
                        this.codigoLocacao = pessoa + codigoFilme;
                        String sqlInsert = "INSERT INTO Locado(LoginF, CodLocacao) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)) {
                        insertStmt.setString(1, pessoa);
                        insertStmt.setString(2, this.codigoLocacao);
                        insertStmt.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    }
                }
            }
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDataDevolucao(String pessoa, Connection connection) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        Month monthNow = now.getMonth();
        Month monthNext = monthNow.plus(1);
        LocalDateTime mesDevolucao = now.plusMonths(1);

        String dataDevolucao = dtf.format(mesDevolucao);
        String sqlUpdate = "UPDATE Locado SET DataDevolucao = ? WHERE LoginF = ?";
        try (PreparedStatement insertStmt = connection.prepareStatement(sqlUpdate)) {
            insertStmt.setString(1, dataDevolucao);
            insertStmt.setString(2, pessoa);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
