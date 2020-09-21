package com.example.paypass.model;

import androidx.annotation.NonNull;

public abstract class Card {
    public static final String YES = "YES"; //Card online
    public static final String NO = "NO"; //Card not attributed
    public static final String EXP = "EXP"; //Card no longer online
    protected static final double DEFAULT_SOLDE = 0.0;
    protected String qr_code;
    protected String num_serie;
    protected String password;
    protected String name;
    protected String date_creat;
    protected String active; //YES, NO, EXP
    protected double solde;

    public Card(String qr_code, String num_serie, String password, String name, String date_creat,
                double solde, String active) {
        this.qr_code = qr_code;
        this.num_serie = num_serie;
        this.password = password;
        this.name = name;
        this.date_creat = date_creat;
        this.solde = solde;
        this.active = active;
    }

    public Card(String qr_code,String password){
        this.qr_code = qr_code;
        this.password = password;
    }

    public String getQr_code() {
        return qr_code;
    }

    public String getNum_serie() {
        return num_serie;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getDate_creat() {
        return date_creat;
    }

    public double getSolde() {
        return solde;
    }

    public String getActive(){
        return this.active;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name : "+this.getName()+", solde : "+this.getSolde();
    }
}
