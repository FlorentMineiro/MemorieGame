package com.example.memoriegame;

public class Carte {
    private String nomCarte ;
    private String urlCarte;

    public Carte(String nomCarte, String urlCarte) {
        this.nomCarte = nomCarte;
        this.urlCarte = urlCarte;
    }

    public String getNomCarte() {
        return nomCarte;
    }

    public String getUrlCarte() {
        return urlCarte;
    }
}
