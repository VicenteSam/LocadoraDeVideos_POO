package org.Plataforma;

import org.Sistema.FuncoesCliente;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        FuncoesCliente cliente = new FuncoesCliente();
        cliente.sistemaLogin();
        cliente.locarFilmes();
    }
}