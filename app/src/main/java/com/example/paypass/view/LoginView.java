package com.example.paypass.view;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.example.paypass.controler.MainActivity;
import com.example.paypass.R;

import java.util.HashMap;
import java.util.Map;

import static com.example.paypass.view.IViewIds.*;

public class LoginView extends AbstractView {

    private static final int NBR_CHAMP = 3;
    private boolean isCreation; //False=login, true=creation; utiliser pout le client
    private byte mode = LOGIN_AGENT;
    private Map<String,String> mapInfo;
    private boolean isAbonne;

    public LoginView(Context context, byte appType) {
        super(context, R.layout.login, appType);
        this.isCreation = false;
        this.isAbonne = false;
        initComponents();
    }

    public LoginView(Context context, byte appType, boolean isCreation) {
        super(context, R.layout.login, appType);
        this.isCreation = isCreation;
        initComponents();
    }


    @Override
    protected void initComponents() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((MainActivity) context).setToolbar(toolbar);

        AppCompatTextView tvTitle = rootView.findViewById(R.id.tvTitle_login);
        final AppCompatEditText edtTxNom = rootView.findViewById(R.id.edtNom);
        final AppCompatEditText edtTxCode = rootView.findViewById(R.id.edtCode);
        AppCompatImageView imgvNom = rootView.findViewById(R.id.imgvNom);
        AppCompatImageView imgvCode = rootView.findViewById(R.id.imgvCode);
        AppCompatButton btnScan = rootView.findViewById(R.id.btnScanLogin);
        AppCompatButton btnRecovery = rootView.findViewById(R.id.btnRecovery);

        RadioGroup rdgrpLogin = rootView.findViewById(R.id.rdgrpLogin);

        String title = "";
        switch (appType) {
            case MainActivity.ID_AGENT:
                if (isCreation) {// C'est l'agent qui creer le client
                    btnRecovery.setVisibility(View.GONE);
                    title = context.getString(R.string.strCreation) + " " +
                            context.getString(R.string.strClient);
                    mode = CREATION_CLIENT;
                } else {
                    edtTxNom.setVisibility(View.GONE);
                    imgvNom.setVisibility(View.GONE);
                    title = context.getString(R.string.strConnect) + " " +
                            context.getString(R.string.strAgent);
                    mode = LOGIN_AGENT;
                }
                break;
            case MainActivity.ID_BUS:
                edtTxNom.setVisibility(View.GONE);
                imgvNom.setVisibility(View.GONE);
                title = context.getString(R.string.strConnect) + " " +
                        context.getString(R.string.strBus);
                mode = LOGIN_BUS;
                break;
            case MainActivity.ID_CLIENT:
                edtTxNom.setVisibility(View.GONE);
                imgvNom.setVisibility(View.GONE);
                btnRecovery.setVisibility(View.GONE);
                edtTxCode.setVisibility(View.GONE);
                imgvCode.setVisibility(View.GONE);
                rdgrpLogin.setVisibility(View.VISIBLE);
                title = context.getString(R.string.strConnect) + " " +
                        context.getString(R.string.strClient);
                mode = LOGIN_CLIENT;
                break;
        }
        tvTitle.setText(title);

        final AppCompatRadioButton radioButton = rootView.findViewById(R.id.rbtnAbonneLogin);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable edtNom = edtTxNom.getText();
                Editable edtCode = edtTxCode.getText();
                String name = "", code = "";
                switch (mode) {
                    case LOGIN_CLIENT:
                        isAbonne = radioButton.isChecked();
                        break;
                    case CREATION_CLIENT:
                        if (edtNom != null && edtCode != null) {
                            name = edtNom.toString();
                            code = edtCode.toString();
                        }
                        break;
                    default:
                        if (edtCode != null)
                            code = edtCode.toString();
                }
                mapInfo = new HashMap<>(NBR_CHAMP);
                mapInfo.put(context.getString(R.string.strName),name); //We put the name
                mapInfo.put(context.getString(R.string.strCode),code); //We put the code
                requestedAction = QR_VIEW;
                ((IViewActions) context).onforwardView(LoginView.this);
            }
        });
    }

    public Map<String, String> getMapInfo() {
        return mapInfo;
    }

    public byte getMode() {
        return this.mode;
    }

    public boolean isAbonne() {
        return isAbonne;
    }
}
