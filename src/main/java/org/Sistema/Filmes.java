package org.Sistema;

import java.util.concurrent.atomic.AtomicLong;

public class Filmes extends Locacao{
    private static final AtomicLong codigoCount = new AtomicLong(1);

    private long codigo;
    private String titulo;
    private String genero;
    private String info;

    public Filmes(FilmeInfo filmeInfo){
        this.codigo = codigoCount.getAndIncrement();
        this.titulo = filmeInfo.title();
        this.genero = filmeInfo.genre();
        this.info = filmeInfo.plot();
    }

    public long getCodigo() {
        return codigo;
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

    @Override
    public String toString() {
        return "Filme \n" +
                "Código: " + codigo +
                "\nTítulo: " + titulo +
                "\nGenero: " + genero +
                "\nInfo: " + info;
    }

}
