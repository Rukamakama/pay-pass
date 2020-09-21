
package com.example.paypass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.paypass.controler.Lugu;
import com.example.paypass.controler.Singleton;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeTest extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView mScannerView;

    public static final byte ID_AGENT = 2;
    public static final byte ID_CLIENT = 3;
    public static final byte ID_BUS = 4;


    String server_url = "http://192.168.43.172/voley.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScannerView = new ZXingScannerView(this);
//        runThisView();

//        Intent intent = new Intent(this,JsonActivity.class);
//        startActivity(intent);
        runQrCodeScan();

    }

    private void runQrCodeScan() {
        final Button btn = findViewById(R.id.btnGetServer);
        btn.setText("SCAN");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(mScannerView);
            }
        });
    }

    private void runThisView() {
        final Button btn = findViewById(R.id.btnGetServer);
        final TextView tv = findViewById(R.id.tvServerResp);

        //For image
        final String imgUrl = "http://192.168.43.172/kivu_lake.gif";
        final ImageView imgv = findViewById(R.id.imgv);

        //For text request
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tv.setText(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv.setText("An error occurs ...");
                        error.printStackTrace();
                    }
                });
                Singleton.getInstance(QRCodeTest.this).addToRequestqueue(stringRequest);
            }
        });

        //For image request
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageRequest imageRequest = new ImageRequest(imgUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                imgv.setImageBitmap(response);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                tv.setText("An error occurs ...");
                                Toast.makeText(MainActivity.this,"Something wrong",
                                        Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        });
                Singleton.getInstance(MainActivity.this).addToRequestqueue(imageRequest);
            }
        });*/

    }

    @Override
    public void handleResult(Result result) {
        String crypted = result.getText();
        String decrypted = new Lugu().decrypt(crypted);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QR CODE RESULT");
        builder.setMessage("CACHE : " + crypted + "CLAIR : " + decrypted);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(QRCodeTest.this);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }
}

