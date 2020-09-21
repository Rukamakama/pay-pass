package com.example.paypass.controler;

import android.content.Context;

import com.example.paypass.model.Card;

public abstract class Controler {

    protected Context context;
    boolean done; // Permet de savoir si le proces a été effectue avec succès

    Controler(Context context){
        this.context = context;
        this.done = false;
    }

    abstract void proces();

    boolean isDone() {
        return done;
    }

    public interface ControlerEvents {

        void onProcessFinish(Controler controler, Card card);

        void errorDialog(String message, final byte view_id);
    }
}
