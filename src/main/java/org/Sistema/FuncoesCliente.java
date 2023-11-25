package org.Sistema;

import com.google.gson.*;
import org.Usuario.Cliente;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FuncoesCliente extends Cliente{

    public void buscarFilme() throws IOException, InterruptedException {
        System.out.println("\n|========PESQUISE SEU FILME========|");
        Scanner ler = new Scanner(System.in);
        String busca = "";

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("[DIGITE 'sair' PARA ENCERRAR]\nBUSCAR FILME:");

            busca = ler.nextLine();
            if(busca.equalsIgnoreCase("sair")){
                break;
            }

            String site = "https://www.omdbapi.com/?t=" + busca.replace(" ",
                    "+") + "&apikey=70a27e2e";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(site))
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            FilmeInfo filmeInfo = gson.fromJson(json, FilmeInfo.class);
            Filmes filmes = new Filmes(filmeInfo);
            if (filmes.getTitulo() == null){
                System.out.println("[FILME NÃO ENCONTRADO]");
                break;
            }

            System.out.println(filmes);
            System.out.println("""
                    ====================================
                    [1] ADICIONAR AO CARRINHO
                    [2] ADICIONAR À LISTA DE DESEJOS
                    [PRESSIONE ENTER PARA SAIR]
                    ====================================
                    Opção:\s""");
            String opcao;
            opcao = ler.nextLine();
            if (opcao.equals("1")){
                String sqlInsert = "INSERT INTO Carrinho(Titulo, CodigoFilme, Login) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, filmes.getTitulo());
                    pstmt.setString(2, filmes.getUniqueID());
                    pstmt.setString(3, getLogin());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Filme adicionado no carrinho!");
            } else if (opcao.equals("2")) {
                String sqlInsert = "INSERT INTO Desejado(Titulo, CodigoFilme, Login) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, filmes.getTitulo());
                    pstmt.setString(2, filmes.getUniqueID());
                    pstmt.setString(3, getLogin());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Filme adicionado na lista de desejos!");
            } else {
                break;
            }
        }
    }

    public void locarFilmes() {
        System.out.println("\nCARRINHO:");

        String sqlPrint = "SELECT Titulo FROM Carrinho WHERE Login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlPrint)) {
            pstmt.setString(1, getLogin());

            try(ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("NENHUM FILME NO CARRINHO");
                    return;
                }

                do {
                    String titulo = rs.getString("Titulo");
                    System.out.println(titulo);
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        try {
            String sqlConfirm = "SELECT COUNT(*) FROM Pessoa WHERE Login = ? AND CartaoDigito IS NOT NULL";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlConfirm)) {
                pstmt.setString(1, getLogin());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("==========EFETUAR PAGAMENTO==========");
                    efetuarPagamento();
                } else {
                    System.out.println("==========CADASTRAR CARTÃO==========");
                    if(cadastrarCartao())
                    {
                        efetuarPagamento();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listaDeDesejos() {
        System.out.println("\nLISTA DE DESEJOS:");

        String sqlConfirm = "SELECT COUNT(*) FROM Desejado WHERE Login = ? AND Titulo IS NOT NULL";
        try (PreparedStatement confirmStmt = connection.prepareStatement(sqlConfirm)) {
            confirmStmt.setString(1, getLogin());

            try (ResultSet countResultSet = confirmStmt.executeQuery()) {
                if (countResultSet.next() && countResultSet.getInt(1) > 0) {
                    String sqlPrint = "SELECT ID, Titulo FROM Desejado WHERE Login = ?";
                    try (PreparedStatement printStmt = connection.prepareStatement(sqlPrint)) {
                        printStmt.setString(1, getLogin());

                        try (ResultSet resultSet = printStmt.executeQuery()) {
                            while (resultSet.next()) {
                                int ID = resultSet.getInt("ID");
                                String titulo = resultSet.getString("Titulo");
                                System.out.println("ID: " + ID + " | Título: " + titulo);
                            }

                            System.out.println("""
                            ======================================================
                            DESEJA ADICIONAR ALGUM FILME AO CARRINHO?
                            DIGITE: 'sim'
                            [PRESSIONE ENTER PARA SAIR]
                            ======================================================
                            Opção:""");

                            Scanner scanner = new Scanner(System.in);
                            String opcao = scanner.nextLine();

                            if (opcao.equals("sim")) {
                                System.out.println("DIGITE O ID DO FILME PARA ADICIONAR AO CARRINHO:");
                                int id = scanner.nextInt();

                                if (verificarLista(id)) {
                                    adicionarAoCarrinho(id);
                                    System.out.println("FILME ADICIONADO AO CARRINHO!");
                                } else {
                                    System.out.println("ID INVÁLIDO OU FILME NÃO ESTÁ NA LISTA DE DESEJOS");
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("NENHUM FILME NA LISTA DE DESEJOS.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verificarLista(int filmeId) throws SQLException {
        String sqlVerificar = "SELECT ID FROM Desejado WHERE Login = ? AND ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlVerificar)) {
            pstmt.setString(1, getLogin());
            pstmt.setInt(2, filmeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void adicionarAoCarrinho(int filmeId) throws SQLException {
        String sqlSelect = "SELECT Titulo, CodigoFilme FROM Desejado WHERE Login = ? AND ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSelect)) {
            pstmt.setString(1, getLogin());
            pstmt.setInt(2, filmeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String titulo = rs.getString("Titulo");
                    String uniqueID = rs.getString("CodigoFilme");
                    String sqlInsert = "INSERT INTO Carrinho(Titulo, CodigoFilme, Login) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)) {
                        insertStmt.setString(1, titulo);
                        insertStmt.setString(2, uniqueID);
                        insertStmt.setString(3, getLogin());
                        insertStmt.executeUpdate();
                    }

                    String sqlDeleteDesejado = "DELETE FROM Desejado WHERE Login = ? AND ID = ?";
                    try (PreparedStatement deleteDesejadoStmt = connection.prepareStatement(sqlDeleteDesejado)) {
                        deleteDesejadoStmt.setString(1, getLogin());
                        deleteDesejadoStmt.setInt(2, filmeId);
                        deleteDesejadoStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
