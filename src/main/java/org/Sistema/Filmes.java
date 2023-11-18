package org.Sistema;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Filmes extends Locacao{
    private static final AtomicLong codigoCount = new AtomicLong(1);
    private final String uniqueID;
    private final String titulo;
    private final String genero;
    private final String info;

    public Filmes(FilmeInfo filmeInfo){
        this.uniqueID = UUID.randomUUID().toString();
        this.titulo = filmeInfo.title();
        this.genero = filmeInfo.genre();
        this.info = filmeInfo.plot();
    }

    public String getUniqueID() {
        return uniqueID;
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
                "ID: " + uniqueID +
                "\nTítulo: " + titulo +
                "\nGenero: " + genero +
                "\nInfo: " + info;
    }

}
