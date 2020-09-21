package com.example.paypass.controler;

import android.content.Context;

import com.example.paypass.model.Operation;

abstract class OperationControler extends Controler {

    protected Operation operation;

    OperationControler(Context context, Operation operation) {
        super(context);
        this.operation = operation;
    }
}
