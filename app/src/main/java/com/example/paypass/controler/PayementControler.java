package com.example.paypass.controler;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.paypass.R;
import com.example.paypass.model.Abonne;
import com.example.paypass.model.Card;
import com.example.paypass.model.Operation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.paypass.controler.MainActivity.ID_ABONNE;
import static com.example.paypass.controler.MainActivity.ID_CLIENT;
import static com.example.paypass.view.IViewIds.LOGIN_VIEW;
import static com.example.paypass.view.IViewIds.PAYEMENT_BUS;

class PayementControler extends OperationControler {

    private double amount;
    private static final double MIN_ABONNEMENT = 10000; // montant unique
    private static final double parLifti = 0.08; // 2% de benefice pour Lifti
    private static final byte END_OK = 1;
    private static final byte END_INV = 2;
    private static final byte END_NO = 3;
    //private static final double MAX_BUS_SOLDE = 200000.0;
    // Les noms de paramètres tel qu'ils sont dans la base de donnees
    private static final String CQR_CODE = "cqr_code";
    private static final String BQR_CODE = "bqr_code";
    private static final String STR_MONTANT = "montant";
    private static final String STR_BSOLD = "bsolde";
    private static final String STR_CSOLD = "csolde";
    private static final String STR_ACTIVE = "active";
    private static final String STR_BEN = "benefice";
    private Card bus;

    PayementControler(Context context, Operation operation) {
        super(context, operation);
        amount = this.operation.getMontant();
    }

    @Override
    void proces() {
        // Get single client and check amount and active
        final Card client = operation.getCarte2();
        bus = operation.getCarte1();
        String decrypted = (new Lugu()).decrypt(client.getQr_code());
        // Checking the formatage of the QR_CODE
        QrReader reader = new QrReader(decrypted);
        if (!reader.isGoodFormat() ||
                (reader.getType() != ID_CLIENT && reader.getType() != ID_ABONNE)){
            endNotification(END_INV);
            return;
        }
        if(reader.getType() == ID_CLIENT){
            final String qr_client = decrypted.substring(2);
            final String qr_bus = bus.getQr_code();
            client.setQr_code(qr_client);
            String url = Urls.SOLDE_BUS_CL; //Pour avoir le solde du bus et de l'agent
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() != 0) {
                            JSONObject resp1 = jsonArray.getJSONObject(0);
                            JSONObject resp2 = jsonArray.getJSONObject(1);
                            double soldeClient = resp1.getDouble(STR_CSOLD);
                            String active = resp1.getString(STR_ACTIVE);
                            double soldeBus = resp2.getDouble(STR_BSOLD);
                            double ben = resp2.getDouble(STR_BEN);
                            if (active.equals(Card.YES)) {
                                if (soldeClient < amount) {
                                    done = false;
                                    endNotification(END_NO);
                                } else {
                                    doPayement(soldeClient, soldeBus, ben, qr_client, qr_bus);
                                }
                            }else
                                endNotification(END_INV);
                        }

                    } catch (JSONException e) {
                        onError(context.getString(R.string.strConectIssue));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(context.getString(R.string.strConectIssue));
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(CQR_CODE, qr_client);
                    params.put(BQR_CODE, qr_bus);
                    return params;
                }
            };
            Singleton.getInstance(context).addToRequestqueue(stringRequest);

        }else{
            final String qr_abonne = decrypted.substring(2);
            String url = Urls.LOGIN_ABONNE+qr_abonne;

            JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    try {
                        JSONObject response = jsonArray.getJSONObject(0);
                        Card abonne = new Abonne(response.getString("qr_code"),
                                response.getString("num_serie"),response.getString("password"),
                                response.getString("nom"),response.getString("date_creation"),
                                response.getDouble("solde"), response.getString("active"),
                                response.getString("type"));
                        if (abonne.getActive().equals(Card.YES)){
                            if (abonne.getSolde() >= MIN_ABONNEMENT){
                                done = true;
                                endNotification(END_OK);
                            }
                            else{
                                done = false;
                                endNotification(END_NO);
                            }
                        }else
                            endNotification(END_INV);

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
        }
    }

    private void doPayement(double csolde, double bsolde, double bene, final String cqr, final String bqr) {
        String url = Urls.PAY_BUS; //Pour l'inscription d'un payement

        final double benefice = bene+ amount *parLifti;
        final double soldeBus = (bsolde + amount) - (amount *parLifti);
        final double soldeClient = csolde - amount;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String payement = new JSONObject(response).getString("payement");
                    if (payement.equals("success")) {
                        done = true;
                        Card bus = operation.getCarte1();
                        bus.setSolde(soldeBus);
                        endNotification(END_OK);
                        ((ControlerEvents)context).onProcessFinish(PayementControler.this, bus);
                    }else if (payement.equals("failed"))
                        onError(context.getString(R.string.strConectIssue));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onError(context.getString(R.string.strConectIssue));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(context.getString(R.string.strConectIssue));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(CQR_CODE, cqr);
                params.put(BQR_CODE, bqr);
                params.put(STR_BSOLD, String.valueOf(soldeBus));
                params.put(STR_CSOLD, String.valueOf(soldeClient));
                params.put(STR_MONTANT, String.valueOf(amount));
                params.put(STR_BEN, String.valueOf(benefice));
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestqueue(stringRequest);
    }

    private void onError(String message) {
        ((ControlerEvents) context).errorDialog(message, PAYEMENT_BUS);
    }

    private void endNotification(byte endState){
        String soundFile;
        String toastMessage;

        switch (endState){
            case END_OK:
                soundFile = "apaye.mp3";
                toastMessage = context.getString(R.string.strPayed);
                break;
            case END_NO:
                soundFile = "nosolde.mp3";
                toastMessage = context.getString(R.string.strSoldPetit);
                break;
            case END_INV:
                soundFile = "invalide.mp3";
                toastMessage = context.getString(R.string.strInvalidField,
                        context.getString(R.string.strQrCode));
                break;
            default:
                soundFile = "invalide.mp3";
                toastMessage = "Innattendus";
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        MediaPlayer mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd;
        try {
            afd = context.getAssets().openFd(soundFile);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((ControlerEvents)context).onProcessFinish(PayementControler.this, bus);
    }

}
