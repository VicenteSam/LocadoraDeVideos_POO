package org.Usuario;

import org.Sistema.SistemaLogin;
import org.Sistema.SistemaPagamento;
import java.sql.*;
import java.util.Scanner;

public class Cliente extends Pessoa implements SistemaLogin, SistemaPagamento {
    private String dataDeNascimento;
    private String cartaoDigito;
    private String cartaoVencimento;
    private String cartaoNomeCompleto;
    private String cartaoRegiao;
    private String cartaoCVC;

    public Cliente() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(
                    "jdbc:sqlite:E:/IdeaProjects/LocadoraDeVideosTeste/DB/userDB.db"); // Indicar Path para DB
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
        String sqlSelect = "SELECT Nome, CPF, Aniversario, Endereco, Telefone, CartaoDigito, CartaoMMAA," +
                "CartaoNomeCompleto, CartaoRegiao, CartaoCVC FROM Pessoa WHERE Login=?";
        Scanner scanner = new Scanner(System.in);
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSelect)) {
            pstmt.setString(1, getLogin());
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    setNome(rs.getString("Nome"));
                    setCpf(rs.getString("CPF"));
                    setDataDeNascimento(rs.getString("Aniversario"));
                    setEndereco(rs.getString("Endereco"));
                    setTelefone(rs.getString("Telefone"));
                    setCartaoDigito(rs.getString("CartaoDigito"));
                    setCartaoVencimento(rs.getString("CartaoMMAA"));
                    setCartaoNomeCompleto(rs.getString("CartaoNomeCompleto"));
                    setCartaoRegiao(rs.getString("CartaoRegiao"));
                    setCartaoCVC(rs.getString("CartaoCVC"));

                    System.out.println("""
                        [Informações Pessoais]
                        Nome:""" + getNome() + """
                        \nCPF:""" + getCpf() + """
                        \nAniversário:""" + getDataDeNascimento() + """
                        \nEndereço:""" + getEndereco() + """
                        \nTelefone:""" + getTelefone() + """
                        \n
                        [Informações do Cartão]
                        Dígitos do cartão:""" + getCartaoDigito() + """
                        \nVencimento do cartão:""" + getCartaoVencimento() + """
                        \nNome do titular:""" + getCartaoNomeCompleto() + """
                        \nRegião do Cartão:""" + getCartaoRegiao() + """
                        \nCVC:""" + getCartaoCVC() + """
                    """);

                    System.out.println("[Digite sua senha novamente para confirmar locação]");
                    String senha = scanner.nextLine();
                    while (!getSenha().equals(senha)) {
                        System.out.println("Senha incorreta. Tente novamente");
                        senha = scanner.nextLine();
                    }
                    System.out.println("Locação confirmada!");
                } else {
                    System.out.println("Nenhum resultado encontrado para o Login: " + getLogin());
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}