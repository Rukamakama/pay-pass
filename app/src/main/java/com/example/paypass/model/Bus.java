package com.example.paypass.model;


public final class Bus extends Card {

    public Bus(String qr_code, String num_serie, String password, String name, String date_creat,
               double solde, String active) {
        super(qr_code, num_serie, password, name, date_creat, solde,active);
    }

    public Bus(String qr_code, String password){
        super(qr_code, password);
    }

}
