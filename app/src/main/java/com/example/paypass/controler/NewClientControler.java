package com.example.paypass.controler;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.paypass.R;
import com.example.paypass.model.Card;
import com.example.paypass.model.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.paypass.controler.MainActivity.ID_CLIENT;
import static com.example.paypass.view.IViewIds.CREATION_CLIENT;

public class NewClientControler extends CardControler {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;
    // Les attributs tel qu'établie dans la base de données
    private static final String QR_CODE = "qr_code";
    private static final String NOM = "nom";
    private static final String QR_AGENT = "qr_agent";
    private static final String PASSWORD = "password";

    NewClientControler(Context context, Card card) {
        super(context, card);
    }

    @Override
    public void proces() {
        creer();
    }

    private void creer() {
        String encrypted_qr = card.getQr_code();
        final String decrypted_qr = (new Lugu()).decrypt(encrypted_qr);
        final String qr_code = decrypted_qr.substring(2);
        final String name = card.getName();
        final String password = card.getPassword();
        final String qr_agent = ((Client) card).getQr_agent();

        if (noTextError(decrypted_qr, name, password)) {
            //On prend un simple client avec le qr_code
            String url = Urls.CLIENT_ACTIVE + qr_code;

            StringRequest jsonRequest = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String active = jsonObject.getString("active");
                        switch (active) {
                            case Card.NO:
                                activerCarte(qr_code, qr_agent, name, password);
                                break;
                            case Card.EXP:
                                onError(context.getString(R.string.strOffline2));
                                break;
                            case Card.YES:
                                onError(context.getString(R.string.strOnline));
                                break;
                            default:
                                invalidText(context.getString(R.string.strQrCode));
                                break;
                        }
                    } catch (JSONException error) {
                        Log.d("SOLID","JSON");
                        error.printStackTrace();
                        onError(context.getString(R.string.strConectIssue));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError(context.getString(R.string.strConectIssue));
                }
            });
            Singleton.getInstance(context).addToRequestqueue(jsonRequest);
        }

    }

    private void activerCarte(final String qr_code,final String qr_agent,
                              final String nom, final String password) {

        String url = Urls.CREATE_CLIENT; //Pour l'activation d'une carte client

        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String newClient = new JSONObject(response).getString("newClient");
                    if (newClient.equals("success")) {
                        done = true;
                        Toast.makeText(context, context.getString(
                                R.string.strCardAttr), Toast.LENGTH_LONG).show();
                        ((ControlerEvents)context).onProcessFinish(NewClientControler.this, card);
                    }else if (newClient.equals("failed"))
                        onError(context.getString(R.string.strConectIssue));
                } catch (JSONException e) {
                    Log.d("SOLID","JSON");
                    e.printStackTrace();
                    onError(context.getString(R.string.strConectIssue));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SOLID","RQST");
                error.printStackTrace();
                onError(context.getString(R.string.strConectIssue));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(QR_CODE, qr_code);
                params.put(NOM, nom);
                params.put(QR_AGENT, qr_agent);
                params.put(PASSWORD, password);
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestqueue(stringRequest);
    }

    private boolean noTextError(String qr_code, String name, String password) {
        QrReader reader = new QrReader(qr_code);
        if (name.length() < MIN_LENGTH) // Facke name checking
            emptyTextError(context.getString(R.string.strName));
        else if (name.length() > MAX_LENGTH)
            longTexError(context.getString(R.string.strName));
        else if (!name.matches("^[a-zA-Z_]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$"))
            invalidText(context.getString(R.string.strName));
        else if (password.length() < MIN_LENGTH) //Fack password checking
            emptyTextError(context.getString(R.string.strCode));
        else if (password.length() > MAX_LENGTH)
            longTexError(context.getString(R.string.strCode));
            //else if (serial.matches("^\\w{4}-\\w{4}-\\w{4}-\\w{4}$"))
        else if (!reader.isGoodFormat() || reader.getType() != ID_CLIENT)
            invalidText(context.getString(R.string.strQrCode));
        else
            return true;

        return false;
    }

    private void emptyTextError(String fieldName) {
        String messsage = context.getString(R.string.strEmptyField, fieldName);
        ((ControlerEvents) context).errorDialog(messsage, CREATION_CLIENT);
    }

    private void longTexError(String fieldName) {
        String message = context.getString(R.string.strLongField, fieldName);
        ((ControlerEvents) context).errorDialog(message, CREATION_CLIENT);
    }

    private void invalidText(String fieldName) {
        String message = context.getString(R.string.strInvalidField, fieldName);
        ((ControlerEvents) context).errorDialog(message, CREATION_CLIENT);
    }

    private void onError(String message) {
        ((ControlerEvents) context).errorDialog(message, CREATION_CLIENT);
    }

}
