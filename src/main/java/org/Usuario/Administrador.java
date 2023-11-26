package org.Usuario;

import org.DatabaseProvider.DatabaseProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Administrador extends Pessoa{
    public Administrador() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DatabaseProvider.getConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarUsuario(){
        Scanner ler = new Scanner(System.in);
        System.out.println("Usuário:");
        String usuario = ler.nextLine();
        setLogin(usuario);
        System.out.println("Nova data de Locação:");
        String novaData = ler.nextLine();
        System.out.println("Mensagem:");
        String mensagem = ler.nextLine();


        String sqlUpdate = "UPDATE Locado SET DataDevolucao=?, Mensagem=? WHERE LoginF=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, novaData);
            pstmt.setString(2, mensagem);
            pstmt.setString(3, usuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void excluirUsuario(){
        Scanner ler = new Scanner(System.in);
        System.out.println("Usuário:");
        String usuario = ler.nextLine();

        String sqlUpdate = "DELETE FROM Pessoa WHERE Login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, usuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void buscarUsuario(){
        Cliente cliente = new Cliente();
        Scanner ler = new Scanner(System.in);
        System.out.println("Usuário:");
        String usuario = ler.nextLine();
        cliente.setLogin(usuario);

        String sqlSelect = "SELECT Nome, CPF, Aniversario, Endereco, Telefone, CartaoDigito, CartaoMMAA," +
                "CartaoNomeCompleto, CartaoRegiao, CartaoCVC FROM Pessoa WHERE Login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSelect)) {
            pstmt.setString(1, cliente.getLogin());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cliente.setNome(rs.getString("Nome"));
                    cliente.setCpf(rs.getString("CPF"));
                    cliente.setDataDeNascimento(rs.getString("Aniversario"));
                    cliente.setEndereco(rs.getString("Endereco"));
                    cliente.setTelefone(rs.getString("Telefone"));
                    cliente.setCartaoDigito(rs.getString("CartaoDigito"));
                    cliente.setCartaoVencimento(rs.getString("CartaoMMAA"));
                    cliente.setCartaoNomeCompleto(rs.getString("CartaoNomeCompleto"));
                    cliente.setCartaoRegiao(rs.getString("CartaoRegiao"));
                    cliente.setCartaoCVC(rs.getString("CartaoCVC"));

                    System.out.println("""
                            \n|==================================|
                            [Informações Pessoais]
                            Nome:""" + cliente.getNome() + """
                            \nCPF:""" + cliente.getCpf() + """
                            \nAniversário:""" + cliente.getDataDeNascimento() + """
                            \nEndereço:""" + cliente.getEndereco() + """
                            \nTelefone:""" + cliente.getTelefone() + """
                            \n
                            [Informações do Cartão]
                            Dígitos do cartão:""" + cliente.getCartaoDigito() + """
                            \nVencimento do cartão:""" + cliente.getCartaoVencimento() + """
                            \nNome do titular:""" + cliente.getCartaoNomeCompleto() + """
                            \nRegião do Cartão:""" + cliente.getCartaoRegiao() + """
                            \nCVC:""" + cliente.getCartaoCVC() + """
                            """);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void buscarLocacao(){
        Scanner ler = new Scanner(System.in);
        System.out.println("Código de Locação:");
        String pesquisa = ler.nextLine();

        String sqlSelect = "SELECT LoginF, Titulo, DataLocacao, DataDevolucao, Nome, Mensagem" +
                " FROM Locado WHERE CodLocacao=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlSelect)) {
            pstmt.setString(1, pesquisa);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String login = rs.getString("LoginF");
                    String titulo = rs.getString("Titulo");
                    String dataLocacao = rs.getString("DataLocacao");
                    String dataDevolucao = rs.getString("DataDevolucao");
                    String nome = rs.getString("Nome");
                    String mensagem = rs.getString("Mensagem");

                    System.out.println("""
                            \n|==================================|
                            [Informações da Locação]
                            Nome:""" + nome + """
                            \nLogin:""" + login + """
                            \nTítulo:""" + titulo + """
                            \nData de Locação:""" + dataLocacao + """
                            \nData de Devolução:""" + dataDevolucao + """
                            \nMensagem:""" + mensagem + """
                            """);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void receberLocacao(){
        Cliente cliente = new Cliente();
        Scanner ler = new Scanner(System.in);
        System.out.println("Usuário:");
        String usuario = ler.nextLine();
        System.out.println("Código de Locação:");
        String codLocacao = ler.nextLine();
        cliente.setLogin(usuario);

        String sqlUpdate = "DELETE FROM Locado WHERE LoginF=? AND CodLocacao=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, codLocacao);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean fazerLogin() {
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
                        System.out.println("LOGIN EFETUADO COM SUCESSO.");
                        return true;
                    } else {
                        System.out.println("LOGIN OU SENHA INCORRETOS");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean criarConta() {
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
            int indexCPF = erro.indexOf("Pessoa.CPF");
            int indexLogin = erro.indexOf("Pessoa.Login");
            int indexEmail = erro.indexOf("Pessoa.Email");
            if (indexCPF == 81){
                System.out.println("[Este CPF já existe. Tente outro.]");
            } else if (indexEmail == 81) {
                System.out.println("[Este email já existe. Tente outro.]");
            } else if (indexLogin == 81) {
                System.out.println("[Este login já existe. Tente outro.]");
            } else {
                System.out.println("Conta criada com sucesso!");
                return true;
            }
        }
        return true;
    }

    public boolean sistemaLogin(){
        Scanner ler = new Scanner(System.in);
        String opcao;

        System.out.println("""
                \n[ADMINISTRADOR]
                ====================================
                [1] FAZER LOGIN
                [2] CRIAR CONTA
                [PRESSIONE ENTER PARA SAIR]
                ====================================
                Opção:""");
        opcao = ler.nextLine();

        if (opcao.equals("1")) {
            return fazerLogin();
        } else if (opcao.equals("2")) {
            return criarConta();
        }
        return false;
    }

}
