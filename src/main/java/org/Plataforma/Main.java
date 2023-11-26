package org.Plataforma;

import org.Sistema.FuncoesCliente;
import org.Usuario.Administrador;
import org.Usuario.Pessoa;

import java.io.IOException;
import java.util.Scanner;

public class  Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        Pessoa administrador = new Administrador();
        FuncoesCliente cliente = new FuncoesCliente();

        Scanner ler = new Scanner(System.in);
        String opcao, secret;

        System.out.println("""
                |========LOCADORA DE VÍDEOS========|
                |   Bem vindo à locadora JavaFlix  |
                | [Pressione Enter para continuar] |
                |==================================|""");
        secret = ler.nextLine();
        if (secret.equals("POO_UFRR_2023")) {
            if (administrador.sistemaLogin()) {
                Administrador funcoesAdmnistrador = new Administrador();

                while (true) {
                    System.out.println("""
                            \n|==================================|
                            [1] BUSCAR USUÁRIO
                            [2] ALTERAR USUÁRIO
                            [3] EXCLUIR USUÁRIO
                            [4] BUSCAR LOCAÇÃO
                            [5] RECEBER LOCAÇÃO
                            [PRESSIONE ENTER PARA SAIR]
                            |==================================|
                            Opção:""");


                    opcao = ler.nextLine();
                    switch (opcao) {
                        case "1" -> funcoesAdmnistrador.buscarUsuario();
                        case "2" -> funcoesAdmnistrador.alterarUsuario();
                        case "3" -> funcoesAdmnistrador.excluirUsuario();
                        case "4" -> funcoesAdmnistrador.buscarLocacao();
                        case "5" -> funcoesAdmnistrador.receberLocacao();
                        default -> {
                            return;
                        }
                    }
                }
            }
        } else {
            if (cliente.sistemaLogin()) {
                while (true) {
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
}