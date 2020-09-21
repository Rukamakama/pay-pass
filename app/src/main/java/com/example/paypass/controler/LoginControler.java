package com.example.paypass.controler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.paypass.R;
import com.example.paypass.model.Abonne;
import com.example.paypass.model.Agent;
import com.example.paypass.model.Bus;
import com.example.paypass.model.Card;
import com.example.paypass.model.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.paypass.controler.MainActivity.ID_CLIENT;
import static com.example.paypass.controler.MainActivity.ID_BUS;
import static com.example.paypass.controler.MainActivity.ID_AGENT;
import static com.example.paypass.controler.MainActivity.ID_ABONNE;
import static com.example.paypass.view.IViewIds.LOGIN_VIEW;

class LoginControler extends CardControler {

    private byte loginMode;

    LoginControler(Context context, Card card){
        super(context,card);
    }

    @Override
    public void proces() {
        connexion();
    }

    void setLoginMode(byte loginMode){
        this.loginMode = loginMode;
    }

    private void connexion(){
        //String encrypted_qr = ((ScanQrcodeView)vuePrincipale).getQrCode();
        String encrypted_qr = card.getQr_code();
        String decrypted_qr = (new Lugu()).decrypt(encrypted_qr);
        QrReader reader = new QrReader(decrypted_qr);
        //If the qr_code is good formatted and belong an agent than
        if (reader.isGoodFormat() && reader.getType() == loginMode){
            // Encrypt the registered qr_code
            //encrypted_qr = new Lugu().encrypt(decrypted_qr.substring(2));
            String qr_str = decrypted_qr.substring(2);
            String url = "";
            switch (loginMode){
                case ID_AGENT : url = Urls.LOGIN_AGENT+qr_str;
                    break;
                case ID_BUS : url = Urls.LOGIN_BUS+qr_str;
                    break;
                case ID_CLIENT : url = Urls.LOGIN_CLIENT+qr_str;
                    break;
                case ID_ABONNE: url = Urls.LOGIN_ABONNE+qr_str;
            }

            JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    try {
                        JSONObject response = jsonArray.getJSONObject(0);
                        if (loginMode == ID_CLIENT){
                            card = new Client(response.getString("qr_code"),
                                    response.getString("num_serie"),response.getString("password"),
                                    response.getString("nom"),response.getString("date_creation"),
                                    response.getDouble("solde"), response.getString("active"));
                            endConnection();
                        }else if (loginMode == ID_ABONNE){
                            card = new Abonne(response.getString("qr_code"),
                                    response.getString("num_serie"),response.getString("password"),
                                    response.getString("nom"),response.getString("date_creation"),
                                    response.getDouble("solde"), response.getString("active"),
                                    response.getString("type"));
                            endConnection();
                        }else {
                            if (card.getPassword().equals(response.getString("password"))){
                                if (loginMode == ID_AGENT){
                                    card = new Agent(response.getString("qr_code"),
                                            response.getString("num_serie"),response.getString("password"),
                                            response.getString("nom"),response.getString("date_creation"),
                                            response.getDouble("solde"), response.getString("active"));
                                    endConnection();

                                }else if(loginMode == ID_BUS){
                                    card = new Bus(response.getString("qr_code"),
                                            response.getString("num_serie"),response.getString("password"),
                                            "",response.getString("date_creation"),
                                            response.getDouble("solde"), response.getString("active"));
                                    endConnection();
                                }
                            }else
                                ((ControlerEvents)context).errorDialog(context.getString(R.string.strInvalidCode), LOGIN_VIEW);
                        }

                        } catch (JSONException error) {
                        ((ControlerEvents)context).errorDialog(context.getString(R.string.strConectIssue), LOGIN_VIEW);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ((ControlerEvents)context).errorDialog(context.getString(R.string.strConectIssue), LOGIN_VIEW);
                }
            });
            Singleton.getInstance(context).addToRequestqueue(jsonRequest);
        }else
            ((ControlerEvents)context).errorDialog(context.getString(R.string.strInvalidField,
                    context.getString(R.string.strQrCode)), LOGIN_VIEW);
    }

    private void endConnection(){
        if (card.getActive().equals(Card.YES)){
            done = true;
        }else {
            done = false;
            if (card.getActive().equals(Card.NO))
                ((ControlerEvents)context).errorDialog(context.getString(R.string.strOffline1), LOGIN_VIEW);
            else if(card.getActive().equals(Card.EXP))
                ((ControlerEvents)context).errorDialog(context.getString(R.string.strOffline2), LOGIN_VIEW);
        }
        ((ControlerEvents)context).onProcessFinish(LoginControler.this, card);
    }

}
