package com.example.paypass.controler;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.paypass.R;
import com.example.paypass.view.HistoriqueView;
import com.example.paypass.view.IViewIds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.paypass.view.HistoriqueView.COLOMN_COUNT;

import static com.example.paypass.view.HistoriqueView.ROW_COUNT;

class HistoriqueControler extends Controler {

    private String[][] tabData;
    private byte type;
    // Les noms des colonnes tels que dans la base
    private static final String STR_ID = "id";
    private static final String STR_DATE = "dat";
    private static final String STR_MONTANT = "amount";
    private static final String STR_NOM = "nom";

    HistoriqueControler(Context context, byte type) {
        super(context);
        this.type = type;
    }

    @Override
    void proces() {
        String url="";
        switch (type){
            case HistoriqueView.HIST_RECH:
                url = Urls.HIST_RECHARGE;
                tabData = new String[ROW_COUNT][COLOMN_COUNT+1];
                break;
            case HistoriqueView.HIST_RETR:
                url = Urls.HIST_RETRAIT;
                tabData = new String[ROW_COUNT][COLOMN_COUNT];
                break;
        }

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    JSONObject response;
                    for (int i=0; i<jsonArray.length(); i++){
                        response = jsonArray.getJSONObject(i);
                        tabData[i][0] = response.getString(STR_ID);
                        tabData[i][1] = response.getString(STR_DATE);
                        tabData[i][2] = String.valueOf(response.getDouble(STR_MONTANT));
                        if (type == HistoriqueView.HIST_RECH)
                            tabData[i][3] = response.getString(STR_NOM);
                    }
                    ((ControlerEvents)context).onProcessFinish(HistoriqueControler.this,null);

                } catch (JSONException error) {
                    ((ControlerEvents)context).errorDialog(
                            context.getString(R.string.strConectIssue), IViewIds.HIST_RECHARGE_AG);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((ControlerEvents)context).errorDialog(
                        context.getString(R.string.strConectIssue), IViewIds.HIST_RECHARGE_AG);
            }
        });
        Singleton.getInstance(context).addToRequestqueue(jsonRequest);
    }

    String[][] getTabData() {
        return tabData;
    }

    byte getType() {
        return type;
    }
}
