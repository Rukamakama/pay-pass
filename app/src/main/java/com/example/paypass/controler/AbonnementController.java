package com.example.paypass.controler;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.paypass.R;
import com.example.paypass.model.Abonnement;
import com.example.paypass.model.Card;
import com.example.paypass.model.Operation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.paypass.controler.MainActivity.ID_ABONNE;
import static com.example.paypass.view.IViewIds.RECHARGE_CLIENT;

class AbonnementController extends OperationControler {

    private final double montant;
    // Les noms de paramètres tel qu'ils sont dans la base de donnees
    private static final String CQR_CODE = "abqr_code";
    private static final String AQR_CODE = "agqr_code";
    private static final String STR_MONTANT = "montant";
    private static final String STR_ASOLD = "agsolde";
    private static final String STR_CSOLD = "absolde";
    private static final String STR_ACTIVE = "active";
    private static final String STR_DUREE = "duree";

    AbonnementController(Context context, Operation operation) {
        super(context, operation);
        this.montant = this.operation.getMontant();
    }

    @Override
    void proces() {
        // Get single client and check amount and active
        final Card abonne = operation.getCarte2();
        final Card agent = operation.getCarte1();
        String decrypted = (new Lugu()).decrypt(abonne.getQr_code());
        // Checking the formatage of the QR_CODE
        QrReader reader = new QrReader(decrypted);
        if (!reader.isGoodFormat() || reader.getType() != ID_ABONNE){
            onError(context.getString(R.string.strInvalidField,
                    context.getString(R.string.strQrCode)));
            return;
        }
        final String qr_abonne = decrypted.substring(2);
        final String qr_agent = agent.getQr_code();
        abonne.setQr_code(qr_abonne);
        String url = Urls.SOLDE_AGT_ABN; //Pour avoir le solde de l'agent et de l'abonne
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() != 0) {
                        JSONObject resp1 = jsonArray.getJSONObject(0);
                        JSONObject resp2 = jsonArray.getJSONObject(1);
                        double soldeAbonne = resp1.getDouble(STR_CSOLD);
                        double soldeAgent = resp2.getDouble(STR_ASOLD);
                        String active = resp1.getString(STR_ACTIVE);
                        if (active.equals(Card.YES)) {
                            if (soldeAgent < montant) {
                                onError(context.getString(R.string.strSoldPetit));
                                ((ControlerEvents)context).onProcessFinish(
                                        AbonnementController.this, agent);
                            } else {
                                int duree = ((Abonnement)operation).getDuree();
                                doRecharge(qr_abonne,qr_agent,soldeAbonne,soldeAgent, duree);
                            }
                        }
                    }

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
                params.put(CQR_CODE, qr_abonne);
                params.put(AQR_CODE, qr_agent);
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestqueue(stringRequest);
    }

    private void doRecharge(final String cqr, final String aqr,
                            double csolde, double asolde, final int duree) {
        String url = Urls.ABONNEMENT; //Pour le recharge du client
        //Il faudra calculer le pourcentage de payement des agents ici
        final double soldeAgent = asolde - montant;
        final double soldeClient = csolde + montant;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String payement = new JSONObject(response).getString("abonnement");
                    if (payement.equals("success")) {
                        done = true;
                        Toast.makeText(context, context.getString(
                                R.string.strRechSuccess), Toast.LENGTH_LONG).show();
                        Card agent = operation.getCarte1();
                        agent.setSolde(soldeAgent);
                        ((ControlerEvents)context).onProcessFinish(AbonnementController.this, agent);
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
                error.printStackTrace();
                onError(context.getString(R.string.strConectIssue));
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(CQR_CODE, cqr);
                params.put(AQR_CODE, aqr);
                params.put(STR_ASOLD, String.valueOf(soldeAgent));
                params.put(STR_CSOLD, String.valueOf(soldeClient));
                params.put(STR_MONTANT, String.valueOf(montant));
                params.put(STR_DUREE, String.valueOf(duree));
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestqueue(stringRequest);
    }

    private void onError(String message) {
        ((ControlerEvents) context).errorDialog(message, RECHARGE_CLIENT);
    }
}

