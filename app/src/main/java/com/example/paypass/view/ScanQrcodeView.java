package com.example.paypass.view;

import android.content.Context;
import android.view.View;

import com.example.paypass.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrcodeView extends AbstractView implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String qrCode;
    private boolean on;

    public ScanQrcodeView(Context context, byte appType) {
        super(context, R.layout.scan_qr_code, appType);
        on = false;
        initComponents();
    }

    public String getQrCode(){
        return qrCode;
    }

    public void stopCamera(){
        if (on){
            mScannerView.stopCamera();           // Stop camera on pause
            on = false;
        }
    }

    public void startCamera(){
        if(!on){
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();          // Start camera on resume
            on = true;
        }
    }

    @Override
    protected void initComponents() {
        mScannerView = new ZXingScannerView(context);
    }

    @Override
    public View getRootView() {
        startCamera();
        return mScannerView;
    }

    @Override
    public void handleResult(Result result) {
        stopCamera();
        qrCode =  result.getText();
        ((IViewActions)context).onforwardView(this);
    }
}
