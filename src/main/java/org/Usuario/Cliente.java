package Usuario;

import Sistema.FilmeInfo;
import Sistema.Filmes;
import Sistema.SistemaLogin;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public void fazerLogin(){
        // Ler login e senha do usuário e fazer uma busca no arquivo Usuario_BD.json para validar seu login
    }

    @Override
    public void criarConta() {
        Scanner ler = new Scanner(System.in);
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
