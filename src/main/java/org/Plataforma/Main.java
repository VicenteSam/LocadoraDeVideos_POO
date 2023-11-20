package org.Plataforma;

import org.Sistema.FuncoesCliente;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("|========LOCADORA DE VÍDEOS========|");
        FuncoesCliente cliente = new FuncoesCliente();
        if(cliente.sistemaLogin()){
            Scanner ler = new Scanner(System.in);
            String opcao;
            while (true){
                System.out.println("""
                        \n|==================================|
                        [1] BUSCAR FILME
                        [2] VISITAR SUA LISTA DE DESEJOS
                        [3] LOCAR FILME
                        [PRESSIONE ENTER PARA SAIR]
                        |==================================|
                        Opção:""");


                opcao = ler.nextLine();
                switch (opcao) {
                    case "1" -> cliente.buscarFilme();
                    case "2" -> cliente.listaDeDesejos();
                    case "3" -> cliente.locarFilmes();
                    default -> {
                        return;
                    }
                }
            }
        }
    }
}