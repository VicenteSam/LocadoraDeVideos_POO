package Sistema;

public class Filmes extends Locacao{
    private String titulo;
    private String genero;
    private String info;
    private String disponibilidade;
    private String situacao;
    private String qtdEmEstoque;

    public Filmes(FilmeInfo filmeInfo){
        this.titulo = filmeInfo.title();
        this.genero = filmeInfo.genre();
        this.info = filmeInfo.plot();
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public String getInfo() {
        return info;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getQtdEmEstoque() {
        return qtdEmEstoque;
    }

    @Override
    public String toString() {
        return "Filme \n" +
                "Título: " + titulo +
                "\nGenero: " + genero +
                "\nInfo: " + info;
    }

}
