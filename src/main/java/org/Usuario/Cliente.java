package org.Usuario;

import org.DatabaseProvider.DatabaseProvider;
import org.Exception.EntradaInvalidaException;
import org.Sistema.Locacao;
import org.Sistema.SistemaPagamento;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente extends Pessoa implements SistemaPagamento{
    private String cartaoDigito;
    private String cartaoVencimento;
    private String cartaoNomeCompleto;
    private String cartaoRegiao;
    private String cartaoCVC;

    public Cliente() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DatabaseProvider.getConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

        try {
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

            boolean valida = false;

            while (!valida){
                try {
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    setNome(nome);
                    if (!nome.matches("[a-zA-Z\\s]+")) {
                        throw new EntradaInvalidaException("Nome deve conter apenas letras.");
                    }

                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();
                    setCpf(cpf);
                    if (!cpf.matches("\\d+")) {
                        throw new EntradaInvalidaException("CPF deve conter apenas números.");
                    }

                    System.out.print("Data de nascimento [DD/MM/AAAA]:  ");
                    String aniversario = scanner.nextLine();
                    setDataDeNascimento(aniversario);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        dateFormat.parse(aniversario);
                    } catch (ParseException e) {
                        throw new EntradaInvalidaException("Formato de data de nascimento inválido. Use o formato [DD/MM/AAAA].");
                    }

                    System.out.print("Endereço: ");
                    String endereco = scanner.nextLine();
                    setEndereco(endereco);

                    System.out.print("Telefone [DDD91111000]: ");
                    String telefone = scanner.nextLine();
                    setTelefone(telefone);
                    if (!telefone.matches("\\d+")) {
                        throw new EntradaInvalidaException("Telefone deve conter apenas números.");
                    }

                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    setEmail(email);
                    if (!validaEmail(email)) {
                        throw new EntradaInvalidaException("Email inválido.");
                    }

                    System.out.print("Login: ");
                    String login = scanner.nextLine();
                    setLogin(login);

                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();
                    setSenha(senha);

                    if (nome.isEmpty() || cpf.isEmpty() || aniversario.isEmpty() || endereco.isEmpty() ||
                            telefone.isEmpty() || email.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                        throw new EntradaInvalidaException("Nenhum campo pode ficar em branco.");
                    }
                    valida = true;
                } catch (EntradaInvalidaException e) {
                    System.out.println("Entrada inválida: " + e.getMessage());
                    return false;
                }
            }

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
            if (indexCPF == 81) {
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

    private boolean validaEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean sistemaLogin(){
        Scanner ler = new Scanner(System.in);
        String opcao;

        System.out.println("""
                ENTRE COM SUA CONTA OU CRIE UMA!
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

    @Override
    public boolean cadastrarCartao() {
        Scanner scanner = new Scanner(System.in);

        try{
            String createTable = "CREATE TABLE IF NOT EXISTS Pessoa (" +
                    "CartaoDigito TEXT UNIQUE NOT NULL," +
                    "CartaoVencimento TEXT NOT NULL," +
                    "CartaoNomeCompleto TEXT UNIQUE NOT NULL," +
                    "CartaoRegiao TEXT NOT NULL," +
                    "CartaoCVC TEXT UNIQUE NOT NULL)";
            connection.createStatement().execute(createTable);

            boolean valida = false;

            while (!valida) {
                try {
                    System.out.print("Dígitos do cartão: \n");
                    System.out.println("[Exemplo: 0000111122223333]");
                    String cartaoDigito = scanner.nextLine();
                    setCartaoDigito(cartaoDigito);
                    if (!cartaoDigito.matches("\\d+")) {
                        System.out.println("Dígitos do cartão devem conter apenas números.");
                        throw new EntradaInvalidaException("Dígitos do cartão devem conter apenas números.");
                    }

                    System.out.print("Data de vencimento do cartão: \n");
                    System.out.println("[Exemplo: MM/AA]");
                    String cartaoVencimento = scanner.nextLine();
                    setCartaoVencimento(cartaoVencimento);
                    if (!cartaoVencimento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                        throw new EntradaInvalidaException("Formato de data de vencimento inválido. Use o formato MM/AA.");
                    }

                    System.out.print("Nome do titular:  \n");
                    System.out.println("[Exemplo: João H. S.]");
                    String nomeTitular = scanner.nextLine();
                    setCartaoNomeCompleto(nomeTitular);
                    if (!nomeTitular.matches("[a-zA-Z\\s.]+")) {
                        throw new EntradaInvalidaException("Nome do titular deve conter apenas letras, espaços e pontos.");
                    }

                    System.out.print("Região: \n");
                    System.out.println("[Exemplo: Brasil]");
                    String regiao = scanner.nextLine();
                    setCartaoRegiao(regiao);
                    if (!regiao.matches("[a-zA-Z]+")) {
                        throw new EntradaInvalidaException("Região deve conter apenas letras.");
                    }

                    System.out.print("CVC: \n");
                    System.out.println("[Exemplo: 000]");
                    String cvc = scanner.nextLine();
                    setCartaoCVC(cvc);
                    if (!cvc.matches("\\d+")) {
                        throw new EntradaInvalidaException("CVC deve conter apenas números.");
                    }

                    valida = true;
                } catch (EntradaInvalidaException e) {
                    System.out.println("Entrada inválida: " + e.getMessage());
                    return false;
                }
            }


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
            System.out.println("DADOS JÁ CADASTRADOS.");
            return false;
        }
        return true;
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
                        \n|==================================|
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

                    System.out.println("|==================================|" +
                            "\n[Digite sua senha novamente para confirmar locação]");
                    String senha = scanner.nextLine();
                    while (!getSenha().equals(senha)) {
                        System.out.println("Senha incorreta. Tente novamente");
                        senha = scanner.nextLine();
                    }
                    System.out.println("\nLOCAÇÃO CONFIRMADA!");
                    System.out.println("""
                            |==================================|
                            [NOTA FISCAL]""");
                    Locacao locacao = new Locacao();
                    locacao.getCodigoLocacao(getLogin(), connection);
                    locacao.getNomeCliente(getLogin(), connection);
                    locacao.getDataLocacao(getLogin(), connection);
                    locacao.getDataDevolucao(getLogin() ,connection);
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