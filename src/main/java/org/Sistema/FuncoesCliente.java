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

    public void sistemaLogin(){
        Scanner ler = new Scanner(System.in);
        String opcao;

        System.out.println("""
                ====================================
                [1] Fazer login
                [2] Criar conta
                [Pressione qualquer tecla para sair]
                ====================================
                Opção:""");
        opcao = ler.nextLine();

        if (opcao.equals("1")) {
            fazerLogin();
        } else if (opcao.equals("2")) {
            criarConta();
        }
    }

    public void buscarFilme() throws IOException, InterruptedException {
        Scanner ler = new Scanner(System.in);
        String busca = "";

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("[Digite 'sair' para encerrar]\nBuscar filme:");
            // Se possível, implementar um algoritmo de correção, caso o usuário digite um título errado
            // Lançar Exception caso o usuário informe um filme que não exista, o programa irá sair automaticamente
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
            System.out.println(filmes);
            System.out.println("""
                    ====================================
                    [1] Adicionar no carrinho
                    [2] Adicionar na lista de desejos
                    [Pressione qualquer tecla para sair]
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
        System.out.println("Carrinho:");

        String sqlPrint = "SELECT Titulo FROM Carrinho WHERE Login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlPrint)) {
            pstmt.setString(1, getLogin());
            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String Titulos = rs.getString("Titulo");
                    System.out.println(Titulos);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            String sqlConfirm = "SELECT COUNT(*) FROM Pessoa WHERE Login = ? AND CartaoDigito IS NOT NULL";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlConfirm)) {
                pstmt.setString(1, getLogin());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("==========Efetuar pagamento==========");
                    efetuarPagamento();
                } else {
                    System.out.println("==========Cadastrar cartão==========");
                    cadastrarCartao();
                    efetuarPagamento();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listaDeDesejos() {
        System.out.println("Lista de Desejos:");

        try {
            String sqlPrint = "SELECT ID, Titulo FROM Desejado WHERE Login = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlPrint)) {
                pstmt.setString(1, getLogin());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String Titulo = rs.getString("Titulo");
                        int ID = rs.getInt("ID");
                        System.out.println("ID: " + ID + " | Título: " + Titulo);
                    }
                    System.out.println("""
                            ======================================================
                            Deseja adicionar algum dos filmes na lista de desejos?
                            Digite: 'sim'
                            [Pressione qualquer tecla para sair]
                            ======================================================
                            Opção:""");

                    Scanner scanner = new Scanner(System.in);
                    String opcao = scanner.nextLine();

                    if (opcao.equals("sim")) {
                        System.out.println("Digite o ID do filme para adicionar ao carrinho:");
                        int id = scanner.nextInt();

                        pstmt.setString(1, getLogin());
                        if (verificarLista(id)) {
                            adicionarAoCarrinho(id);
                            System.out.println("Filme adicionado ao carrinho!");
                        } else {
                            System.out.println("ID inválido ou filme não está na lista de desejos.");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
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
