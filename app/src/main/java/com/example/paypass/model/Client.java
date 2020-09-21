package com.example.paypass.model;


public final class Client extends Card {

    private String qr_agent;

    public Client(String qr_code, String num_serie, String password, String name,
                  String date_creat, double solde, String active) {
        super(qr_code, num_serie, password, name, date_creat, solde, active);
    }

    public Client(String qr_code, String password, String name, String qr_agent) {

        super(qr_code, "", password, name, "", DEFAULT_SOLDE,  NO);
        this.qr_agent = qr_agent;
    }

    public Client(String qr_code, String password){
        super(qr_code, password);
    }

    public String getQr_agent(){
        return this.qr_agent;
    }

}
