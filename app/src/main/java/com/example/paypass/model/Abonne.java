package com.example.paypass.model;

public class Abonne extends Card {

    private String type;

    public Abonne(String qr_code, String num_serie, String password, String name,
                  String date_creat, double solde, String active, String type) {
        super(qr_code, num_serie, password, name, date_creat, solde, active);
        this.type = type;
    }

    public Abonne(String qr_code, String password, String name) {

        super(qr_code, "", password, name, "", DEFAULT_SOLDE,  NO);
    }

    public Abonne(String qr_code, String password){
        super(qr_code, password);
    }

    public String getType() {
        return type;
    }
}
