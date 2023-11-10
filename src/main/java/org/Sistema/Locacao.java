package Sistema;

public class Locacao {
    private int codigoLocacao;
    private String nomeCliente;
    private String codigoFilme;
    private String dataLocacao;
    private String dataDevolucao;

    public int getCodigoLocacao() {
        // Mesclar códigos
        // CPF + Código do filme
        return codigoLocacao;
    }

    public String getNomeCliente() {
        // Retorna nome do Cliente e gera um número único
        return nomeCliente;
    }

    public String getCodigoFilme() {
        // Retorna o código do filme
        return codigoFilme;
    }

    public String getDataLocacao() {
        // Salva data do dia alocado
        return dataLocacao;
    }

    public String getDataDevolucao() {
        // Calcula uma data X para devolução (baseado nas regras de negócios)
        return dataDevolucao;
    }
}
