package com.example.paypass.model;

public final class Abonnement extends Operation {

    private int duree;

    public Abonnement(Card carte1, Card carte2) {
        super(carte1, carte2);
        setDuree(30);
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }
}