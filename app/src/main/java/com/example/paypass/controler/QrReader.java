package com.example.paypass.controler;
import android.util.Log;

import static com.example.paypass.controler.MainActivity.ID_AGENT;
import static com.example.paypass.controler.MainActivity.ID_BUS;
import static com.example.paypass.controler.MainActivity.ID_CLIENT;
import static com.example.paypass.controler.MainActivity.ID_ABONNE;

public class QrReader {

    /*
    * Format TT-RR-DDMMYY-IIIIII
    * TT : type (11:Admin, 22:Bus, 33:Agent, 99:Client, 44:abonne)
    * RR : region (01:Kinshasa, 02:Goma, 03:Bukavu)
    * MMYY : period (04/20)
    * IIIIII : id (014023)
    *
    * Only QR_CODE from card has TT but those from db don't
    * */

    private static final byte QR_LENGTH = 14;
    private static final byte DEFAULT_ID = 0;
    private String qr_code;

    QrReader(String qr_code){
        this.qr_code = qr_code;
    }

    byte getType(){
        switch (qr_code.substring(0,2)){
            case "11": //Faire quelque chose pour l'administrateur
               break;
            case "22": return ID_BUS;
            case "44": return ID_ABONNE;
            case "33": return ID_AGENT;
            case "99": return  ID_CLIENT;
        }
        return DEFAULT_ID;
    }

    boolean isGoodFormat(){
        return qr_code != null && qr_code.matches("^[0-9]{"+QR_LENGTH+"}$");
    }

    public static String[] getExemples(){
        // Returns trail examples
        return new String[] {"11010420000001", "22021219112578", "99021019100364", "44010120050678"};
    }

}
