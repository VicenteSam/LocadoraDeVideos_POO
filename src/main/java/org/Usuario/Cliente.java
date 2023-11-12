package org.Usuario;

import com.google.gson.*;
import org.Sistema.FilmeInfo;
import org.Sistema.Filmes;
import org.Sistema.SistemaLogin;
import org.Sistema.SistemaPagamento;

import java.io.FileReader;
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

public class Cliente extends Pessoa implements SistemaLogin, SistemaPagamento {
    private String dataDeNascimento;
    private String cartaoDigito;
    private String cartaoVencimento;
    private String cartaoNomeCompleto;
    private String cartaoRegiao;
    private String cartaoCVC;
    private Connection connection;

    public Cliente() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:E:/IdeaProjects/LocadoraDeVideosTeste/DB/userDB.db");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getCartaoDigito() {
        return cartaoDigito;
    }

    public void setCartaoDigito(String cartaoDigito) {
        this.cartaoDigito = cartaoDigito;
    }

    public String getCartaoVencimento() {
        return cartaoVencimento;
    }

    public void setCartaoVencimento(String cartaoVencimento) {
        this.cartaoVencimento = cartaoVencimento;
    }

    public String getCartaoNomeCompleto() {
        return cartaoNomeCompleto;
    }

    public void setCartaoNomeCompleto(String cartaoNomeCompleto) {
        this.cartaoNomeCompleto = cartaoNomeCompleto;
    }

    public String getCartaoRegiao() {
        return cartaoRegiao;
    }

    public void setCartaoRegiao(String cartaoRegiao) {
        this.cartaoRegiao = cartaoRegiao;
    }

    public String getCartaoCVC() {
        return cartaoCVC;
    }

    public void setCartaoCVC(String cartaoCVC) {
        this.cartaoCVC = cartaoCVC;
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
        Scanner scanner = new Scanner(System.in);
        try{
            System.out.print("Login: ");
            String login = scanner.nextLine();
            setLogin(login);

            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            setSenha(senha);

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT * FROM Pessoa WHERE Pessoa.Login=? and Pessoa.Senha=?")) {
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
        Scanner scanner = new Scanner(System.in);

        try{
            String createTable = "CREATE TABLE IF NOT EXISTS Pessoa (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Nome TEXT NOT NULL," +
                    "CPF TEXT PRIMARY KEY NOT NULL," +
                    "Aniversario TEXT NOT NULL," +
                    "Endereco TEXT NOT NULL," +
                    "Telefone TEXT NOT NULL," +
                    "Email TEXT UNIQUE NOT NULL," +
                    "Login TEXT UNIQUE NOT NULL," +
                    "Senha TEXT NOT NULL)";
            connection.createStatement().execute(createTable);

            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            setNome(nome);

            System.out.print("CPF: ");
            String cpf = scanner.nextLine();
            setCpf(cpf);

            System.out.print("Data de nascimento [DD/MM/AAAA]:  ");
            String aniversario = scanner.nextLine();
            setDataDeNascimento(aniversario);

            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();
            setEndereco(endereco);

            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();
            setTelefone(telefone);

            System.out.print("Email: ");
            String email = scanner.nextLine();
            setEmail(email);

            System.out.print("Login: ");
            String login = scanner.nextLine();
            setLogin(login);

            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            setSenha(senha);

            String sqlInsert = "INSERT INTO Pessoa(Nome, CPF, Aniversario, Endereco, Telefone, Email, Login, Senha)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, getNome());
                pstmt.setString(2, getCpf());
                pstmt.setString(3, getDataDeNascimento());
                pstmt.setString(4, getEndereco());
                pstmt.setString(5, getTelefone());
                pstmt.setString(6, getEmail());
                pstmt.setString(7, getLogin());
                pstmt.setString(8, getSenha());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            String erro = e.getMessage();
            System.out.println(erro);
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

    public void locarFilmes() {
        System.out.println("Carrinho:");

        try (FileReader reader = new FileReader("carrinho.json")) {
            JsonArray listaFilmes = JsonParser.parseReader(reader).getAsJsonArray();
            System.out.println(listaFilmes);

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
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listaDeDesejos(){
        try (FileReader reader = new FileReader("lista_desejos.json")) {
            JsonArray listaFilmes = JsonParser.parseReader(reader).getAsJsonArray();
            System.out.println(listaFilmes);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // Opção de adicionar ao carrinho
    }

    @Override
    public void cadastrarCartao() {
        Scanner scanner = new Scanner(System.in);

        try{
            String createTable = "CREATE TABLE IF NOT EXISTS Pessoa (" +
                    "CartaoDigito TEXT UNIQUE NOT NULL," +
                    "CartaoVencimento TEXT NOT NULL," +
                    "CartaoNomeCompleto TEXT UNIQUE NOT NULL," +
                    "CartaoRegiao TEXT NOT NULL," +
                    "CartaoCVC TEXT UNIQUE NOT NULL)";
            connection.createStatement().execute(createTable);

            System.out.print("Dígitos do cartão: \n");
            System.out.println("[Exemplo: 0000111122223333]");
            System.out.println(scanner.hasNextLine());
            String cartaoDigito = scanner.nextLine();
            setCartaoDigito(cartaoDigito);

            System.out.print("Data de vencimento do cartão: \n");
            System.out.println("[Exemplo: MM/AA]");
            String cartaoVencimento = scanner.nextLine();
            setCartaoVencimento(cartaoVencimento);

            System.out.print("Nome do titular:  \n");
            System.out.println("[Exemplo: João H. S.]");
            String nomeTitular = scanner.nextLine();
            setCartaoNomeCompleto(nomeTitular);

            System.out.print("Região: \n");
            System.out.println("[Exemplo: Brasil]");
            String regiao = scanner.nextLine();
            setCartaoRegiao(regiao);

            System.out.print("CVC: \n");
            System.out.println("[Exemplo: 000]");
            String cvc = scanner.nextLine();
            setCartaoCVC(cvc);


            String sqlInsert = "UPDATE Pessoa SET CartaoDigito=?, CartaoMMAA=?, CartaoNomeCompleto=?, " +
                    "CartaoRegiao=?, CartaoCVC=? WHERE Login=?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                pstmt.setString(1, getCartaoDigito());
                pstmt.setString(2, getCartaoVencimento());
                pstmt.setString(3, getCartaoNomeCompleto());
                pstmt.setString(4, getCartaoRegiao());
                pstmt.setString(5, getCartaoCVC());
                pstmt.setString(6, getLogin());
                pstmt.executeUpdate();
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void efetuarPagamento() {
        // Retornar informações do cartão para confirmação
        // Confirmar pagamento digitando a sua senha do sistema
        // Comparar senha com a senha do BD
    }
}
