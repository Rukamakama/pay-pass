package com.example.paypass.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class AbstractView {

    protected byte requestedAction; //view requested by user click
    protected static final byte DEFAULT_VIEW_ID = 0;
    protected ConstraintLayout rootView;
    protected Context context;
    protected byte appType;

    public AbstractView(Context context,int view,byte appType){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //To avoid nullPointerException
        if (inflater != null)
            rootView =  (ConstraintLayout) inflater.inflate(view,null);
        this.context = context;
        this.appType = appType;

    }

    //Pour l'union des composants
    protected abstract void initComponents();


    public View getRootView() {
        return rootView;
    }

    public byte getRequestedAction() {
        return requestedAction;
    }

}
