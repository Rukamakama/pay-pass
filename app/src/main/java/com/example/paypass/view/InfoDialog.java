package com.example.paypass.view;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paypass.R;

public abstract class InfoDialog {

    public static final byte ACTION_YES = 1;
    public static final byte ACTION_NO = 2;
    public static boolean YES_NO = true;
    public static boolean YES = false;

    private String titre;
    private String body;
    private boolean isYesNon;
    private Context context;
    private AlertDialog dialog;
    private AppCompatTextView tvBody;

    protected InfoDialog(Context context, String titre, String body, boolean isYesNon) {
        this.titre = titre.toUpperCase();
        this.body = body;
        this.isYesNon = isYesNon;
        this.context = context;
        initComponent();
    }

    private void initComponent(){
        ConstraintLayout layout = (ConstraintLayout)
                View.inflate(context, R.layout.dialogbox_info,null);//Get inflater

        AppCompatTextView tvTitre = layout.findViewById(R.id.tvTitleDialogInfo);
        tvTitre.setText(this.titre);
        tvBody = layout.findViewById(R.id.tvMsgDialogInfo);
        tvBody.setText(this.body);

        layout.findViewById(R.id.btnOkDialogInfo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onExitDialog(ACTION_YES);
            }
        });
        if (isYesNon == YES_NO){
            AppCompatButton btnNon = layout.findViewById(R.id.btnNoDialogInfo);
            btnNon.setVisibility(View.VISIBLE);
            btnNon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onExitDialog(ACTION_NO);
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        dialog = builder.create();
    }

    public void showDialog(){
        if (dialog != null && !dialog.isShowing())
            dialog.show();
    }

    protected void dismissDialog(){
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public void setMessage(CharSequence message){
        body = message.toString();
        tvBody.setText(message);
    }

    public abstract void onExitDialog(byte action);
}
