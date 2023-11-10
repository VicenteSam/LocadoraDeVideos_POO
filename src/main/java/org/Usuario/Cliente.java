package org.Usuario;

import org.Sistema.FilmeInfo;
import org.Sistema.Filmes;
import org.Sistema.SistemaLogin;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cliente extends Pessoa implements SistemaLogin {
    private String dataDeNascimento;
    private String debito;
    public String getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getDebito() {
        return debito;
    }

    public void setDebito(String debito) {
        this.debito = debito;
    }

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

    @Override
    public void fazerLogin() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:E:/IdeaProjects/LocadoraDeVideosTeste/DB/userDB.db")) {

            System.out.print("Login: ");
            String login = scanner.nextLine();

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Pessoa WHERE Pessoa.Login=? and Pessoa.Senha=?")) {
                pstmt.setString(1, login);
                pstmt.setString(2, senha);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Login efetuado com sucesso!");
                    } else {
                        System.out.println("Login ou senha incorretos.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void criarConta() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:E:/IdeaProjects/LocadoraDeVideosTeste/DB/userDB.db")) {
            String createTable = "CREATE TABLE IF NOT EXISTS Pessoa (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Nome TEXT," +
                    "CPF TEXT," +
                    "Endereco TEXT," +
                    "Telefone TEXT," +
                    "Email TEXT," +
                    "Login TEXT," +
                    "Senha TEXT)";
            connection.createStatement().execute(createTable);

            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("CPF: ");
            String cpf = scanner.nextLine();

            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();

            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Login: ");
            String login = scanner.nextLine();

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            String sqlInsert = "INSERT INTO Pessoa(Nome, CPF, Endereco, Telefone, Email, Login, Senha) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, nome);
                pstmt.setString(2, cpf);
                pstmt.setString(3, endereco);
                pstmt.setString(4, telefone);
                pstmt.setString(5, email);
                pstmt.setString(6, login);
                pstmt.setString(7, senha);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            String erro = e.getMessage();
            int indexCPF = erro.indexOf("Pessoa.CPF");
            int indexLogin = erro.indexOf("Pessoa.Login");
            int indexEmail = erro.indexOf("Pessoa.Email");
            if (indexCPF == 81){
                System.out.println("[Este CPF já existe. Tente outro.]");
            } else if (indexEmail == 81) {
                System.out.println("[Este email já existe. Tente outro.]");
            } else if (indexLogin == 81) {
                System.out.println("[Este login já existe. Tente outro.]");
            }
        }
    }

    public void buscarFilme() throws IOException, InterruptedException {
        Scanner ler = new Scanner(System.in);
        String busca = "";
        List<String> listaCarrinho = new ArrayList<>();
        List<String> listaDesejo = new ArrayList<>();

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) {
            System.out.println("[Digite 'sair' para encerrar]\nBuscar:");
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
                FileWriter carrinho = new FileWriter("carrinho.json");
                listaCarrinho.add(filmes.getTitulo());
                carrinho.write(gson.toJson(listaCarrinho));
                carrinho.close();
                System.out.println("Filme adicionado no carrinho!");
            } else if (opcao.equals("2")) {
                FileWriter desejos = new FileWriter("lista_desejos.json");
                listaDesejo.add(filmes.getTitulo());
                desejos.write(gson.toJson(listaDesejo));
                desejos.close();
                System.out.println("Filme adicionado na lista de desejos!");
            } else {
                break;
            }
        }
    }

    public void locarFilmes(){
        // Verifica todos os filmes que estão no carrinho
        // Exibe painel de pagamento
    }

    public void listaDeDesejos(){
        // Exibe lista de desejos
        // Opção de adicionar ao carrinho
    }

}
