package com.example.paypass.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import static com.example.paypass.view.IViewIds.*;
import static com.example.paypass.controler.MainActivity.ID_AGENT;
import static com.example.paypass.controler.MainActivity.ID_CLIENT;
import static com.example.paypass.controler.MainActivity.ID_BUS;
import com.example.paypass.R;
import com.example.paypass.model.Card;

public final class CompteView extends AbstractView {

    private Card card;
    private double price;

    public CompteView(Context context, byte appType, Card card) {
        super(context, R.layout.compte, appType);
        this.card = card;
        initComponents();
    }

    @Override
    protected void initComponents() {
        AppCompatTextView tvSolde = rootView.findViewById(R.id.tvSoldeAgt);
        AppCompatTextView tvName = rootView.findViewById(R.id.tvComptAgt);
        tvSolde.setText(context.getString(R.string.strSolde, String.valueOf(card.getSolde())));
        tvName.setText(card.getName());

        AppCompatButton btnHretr = rootView.findViewById(R.id.btnHretr);
        AppCompatButton btnHrech = rootView.findViewById(R.id.btnHrech);
        AppCompatButton btnQuitter = rootView.findViewById(R.id.btnQuitter);//For clients
        //Button to start bus payment
        AppCompatButton btnCommencer = rootView.findViewById(R.id.btnCommencer);
        //Spinner and TextView to choose the price in bus payment
        final Spinner spnPriceChooser = rootView.findViewById(R.id.spnPriceChooser);
        AppCompatTextView tvPrice = rootView.findViewById(R.id.tvPrice);

        //Disable or enable some buttons
        switch (appType){
            case ID_AGENT :
                btnCommencer.setVisibility(View.INVISIBLE);
                btnQuitter.setVisibility(View.INVISIBLE);
                spnPriceChooser.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);
                btnHrech.setOnClickListener(btnListener);
                btnHretr.setOnClickListener(btnListener);
                break;
            case ID_BUS :
                btnHrech.setVisibility(View.INVISIBLE);
                btnHretr.setVisibility(View.INVISIBLE);
                btnQuitter.setVisibility(View.INVISIBLE);
                btnCommencer.setOnClickListener(btnListener);
                break;
            case ID_CLIENT :
                btnHrech.setVisibility(View.INVISIBLE);
                btnHretr.setVisibility(View.INVISIBLE);
                spnPriceChooser.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);
                btnCommencer.setVisibility(View.INVISIBLE);
                btnQuitter.setOnClickListener(btnListener);
                break;
        }
        //Price chooser setting for buses
        if(appType == ID_BUS){
            //Creating a Adapter from a string array
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    context,R.array.strPrices,android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnPriceChooser.setAdapter(adapter);
            spnPriceChooser.setSelected(true);
            price = Double.valueOf(spnPriceChooser.getSelectedItem().toString());
            spnPriceChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    price = Double.valueOf(spnPriceChooser.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Nothing to do
                }
            });
        }
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnHretr :
                    requestedAction = HIST_RETRAIT_AG;
                    break;
                case R.id.btnHrech :
                    requestedAction = HIST_RECHARGE_AG;
                    break;
                case R.id.btnCommencer :
                    requestedAction = QR_VIEW;
                    break;
                case  R.id.btnQuitter :
                    requestedAction = HOME_VIEW;
            }
            ((IViewActions)context).onforwardView(CompteView.this);
        }
    };

    /**
     * Methode getPrice()
     * @return the price selected by user for bus payment
     */
    public double getPrice(){
        return price;
    }
}
