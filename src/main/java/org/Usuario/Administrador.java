package org.Usuario;

import org.DatabaseProvider.DatabaseProvider;
import org.Sistema.Locacao;


public class Administrador extends Pessoa{
    public Administrador() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DatabaseProvider.getConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarUsuario(Cliente cliente){
        // Opção de alterar data de devolução
        // Opção informar multas
        // Utilizando BD ou arquivo
    }
    public void excluirUsuario(Cliente cliente){
        // Excluir código e informações do usuário através do BD ou arquivo
    }
    public void buscarUsuario(Cliente cliente){
        // Buscar usuário por código
        // Exibir informações sobre
        // Utilizando BD ou arquivo
    }
    public void fazerLogin(){
        // Fazer login (nome/email + senha)
        // Utilizando BD ou arquivo
    }
    public void buscarLocacao(Locacao codigo){
        // Procurar locação através do código
        // Retornar todas as infomações sobre
        // Utilizando BD ou arquivo
    }
    public void receberLocacao(Locacao codigo){
        // Confirmar devolução de filmes
        // Utilizando BD ou arquivo
    }
    public void criarRelatorio(){
        // Relatório de cada cliente
    }
}
