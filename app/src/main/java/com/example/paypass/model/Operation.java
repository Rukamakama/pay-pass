package com.example.paypass.model;

public abstract class Operation {

    private double montant;
    private Card carte1; //Carte de base
    private Card carte2; //Carte secondaire

    Operation(Card carte1, Card carte2) {
        this.carte1 = carte1;
        this.carte2 = carte2;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public double getMontant() {
        return montant;
    }

    public Card getCarte1() {
        return carte1;
    }

    public Card getCarte2() {
        return carte2;
    }
}
