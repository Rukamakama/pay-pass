package com.example.paypass.view;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.example.paypass.controler.MainActivity;
import com.example.paypass.R;
import com.example.paypass.model.Card;

import static com.example.paypass.view.IViewIds.RECHARGE_CLIENT;
import static com.example.paypass.view.IViewIds.COMPTE_VIEW;
import static com.example.paypass.view.IViewIds.CREATION_CLIENT;

public class OperationAgentView extends AbstractView {

    private Card agent;
    public OperationAgentView(Context context, byte appType, Card agent) {
        super(context, R.layout.operation_agent, appType);
        this.agent = agent;
        initComponents();
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((MainActivity)context).setToolbar(toolbar);

        AppCompatTextView tvTitle = rootView.findViewById(R.id.tvTitle_agent);
        tvTitle.setText(context.getString(R.string.strAgentTel,agent.getName()));
        //Il faut faire le setting de titre selon le nom de l'agent
        AppCompatButton btnRecharge = rootView.findViewById(R.id.btnRecharge);
        AppCompatButton btnCompte = rootView.findViewById(R.id.btnCompte);
        AppCompatImageButton btnAddClient = rootView.findViewById(R.id.btnAddClient);

        btnRecharge.setOnClickListener(actionBtnListener);
        btnAddClient.setOnClickListener(actionBtnListener);
        btnCompte.setOnClickListener(actionBtnListener);

    }

    private View.OnClickListener actionBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.btnRecharge :
                   requestedAction = RECHARGE_CLIENT;
                   break;
               case R.id.btnCompte :
                   requestedAction = COMPTE_VIEW;
                   break;
               case R.id.btnAddClient :
                   requestedAction = CREATION_CLIENT;
                   break;
           }
            ((MainActivity)context).onforwardView(OperationAgentView.this);
        }
    };
}
