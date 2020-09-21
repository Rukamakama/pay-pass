package com.example.paypass.view;

import android.content.Context;
import android.text.Editable;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;

import static com.example.paypass.view.IViewIds.QR_VIEW;

import com.example.paypass.R;

public final class RechargeView extends AbstractView {

    private double montant;
    private boolean isAbonne;

    public RechargeView(Context context, byte appType) {
        super(context, R.layout.recharge, appType);
        this.isAbonne = false;
        initComponents();
    }

    @Override
    protected void initComponents() {
        AppCompatTextView tvTitle = rootView.findViewById(R.id.tvTitle_recharge);
        final AppCompatEditText edtMontant = rootView.findViewById(R.id.edtMontant);
        AppCompatButton btnScan = rootView.findViewById(R.id.btnScanRecharge);
        tvTitle.setText(context.getString(R.string.strRechClient));
        final AppCompatRadioButton rbtnAbonne = rootView.findViewById(R.id.rbtnAbonne);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable edt = edtMontant.getText();
                isAbonne = rbtnAbonne.isChecked();
                if (edt != null)
                    montant = Double.parseDouble(edt.toString());
                requestedAction = QR_VIEW;
                ((IViewActions)context).onforwardView(RechargeView.this);
            }
        });



    }

    public double getMontant(){
        return montant;
    }

    public boolean isAbonne() {
        return isAbonne;
    }

}
