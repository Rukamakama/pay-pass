package com.example.paypass.model;


public final class Agent extends Card {

    private String telephone;
    private String zone;
    private String adresse;

    public Agent(String qr_code, String num_serie, String password, String name, String date_creat,
                 double solde, String active) {
        super(qr_code, num_serie, password, name, date_creat, solde, active);
    }

    public Agent(String qr_code, String num_serie, String password, String name, String date_creat,
                 double solde, String active, String telephone, String zone, String adresse) {
        super(qr_code, num_serie, password, name, date_creat, solde, active);
        this.telephone = telephone;
        this.zone = zone;
        this.adresse = adresse;
    }

    public Agent(String qr_code, String password){
        super(qr_code, password);
    }

    public String getTelephone() {
        return telephone;
    }

    public String getZone() {
        return zone;
    }

    public String getAdresse() {
        return adresse;
    }
}
