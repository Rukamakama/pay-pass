package com.example.paypass.controler;


import android.content.Context;

import com.example.paypass.model.Card;

public abstract class CardControler extends Controler{

    protected Card card;

    CardControler(Context context, Card card) {
        super(context);
        this.card = card;
    }
}
