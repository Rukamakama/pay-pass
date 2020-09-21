package com.example.paypass.view;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.example.paypass.R;
import com.example.paypass.controler.MainActivity;

public class HomeView extends AbstractView {


    public HomeView(Context context, byte appType){
        super(context, R.layout.home_page,appType);
        initComponents();
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((MainActivity)context).setToolbar(toolbar);

        AppCompatTextView tvTitre = rootView.findViewById(R.id.tvTitle_home);

        String title = "";
        switch (appType){
            case MainActivity.ID_AGENT :
                title = context.getString(R.string.strAgent)+" "+
                        context.getString(R.string.app_name);
                break;
            case MainActivity.ID_BUS:
                title = context.getString(R.string.strBus)+" "+
                        context.getString(R.string.app_name);
                final AppCompatImageView imgvP = rootView.findViewById(R.id.imgvPerson_home);
                imgvP.setImageResource(R.drawable.ic_bus);
                break;
            case MainActivity.ID_CLIENT:
                title = context.getString(R.string.strClient)+" "+
                        context.getString(R.string.app_name);
                break;
        }
        tvTitre.setText(title);
        AppCompatButton btnConnexion = rootView.findViewById(R.id.btnSignin);

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IViewActions)context).onforwardView(HomeView.this);
            }
        });
    }
}
