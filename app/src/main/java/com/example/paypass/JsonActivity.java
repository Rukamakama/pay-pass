package com.example.paypass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.paypass.controler.QrReader;
import com.example.paypass.controler.Lugu;

public class JsonActivity extends AppCompatActivity {
    String jsonUrl = "http://192.168.43.172/voley.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponent1();
    }

    private void initComponent1(){
        setContentView(R.layout.activity_json);

        Button btn = findViewById(R.id.btnInfo);
        final TextView tvName = findViewById(R.id.tvName);
        final TextView tvEmail = findViewById(R.id.tvEmail);
        final TextView tvTel = findViewById(R.id.tvNumTel);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] before = QrReader.getExemples();
                Lugu lugu = new Lugu();
                for (String strid : before){
                    String crypted = lugu.encrypt(strid);
                    Log.d("SOLEIL",crypted);
                }
                //Simulate encryption
                /*Lugu lugu = new Lugu();
                String before = "1101041219000001";
                tvName.setText("Initial :"+before);
                String crypted = lugu.encrypt(before);
                Log.d("crypted",crypted);
                tvEmail.setText("Encrypted : "+crypted);
                tvTel.setText("decrypted : "+lugu.decrypt(crypted));*/


               /* JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        jsonUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvName.setText(response.getString("noms"));
                            tvEmail.setText(response.getString("mail"));
                            tvTel.setText(response.getString("numTel"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                Singleton.getInstance(JsonActivity.this).addToRequestqueue(jsonRequest);
                */
            }
        });

    }

    private void initComponent2(){
        setContentView(R.layout.activity_json_list);


    }
}
