package org.Plataforma;

import org.Usuario.Cliente;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Cliente cliente = new Cliente();
        cliente.sistemaLogin();
        cliente.buscarFilme();
        cliente.locarFilmes();
    }
}