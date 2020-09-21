package com.example.paypass.controler;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Lugu {

    private static final String CLEF = "MQxnKvm5qFsWOz7CVsqxVVGkNcXpueQ1i3KaIjwbZbc=";
    private SecretKey secretKey;
    private final byte[] iv;
//    String stringKey;

    public Lugu() {
        //Key generation code
        /*try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            //Handle exception
        }
        //Key to String
        if (secretKey != null) {
            stringKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            Log.d("KEY_STRING",stringKey);
        }*/

        //Key loading code
        byte[] encodedKey = Base64.decode(CLEF, Base64.DEFAULT);
        secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length
                , "AES");
        iv = new byte[]{1,0,1,5,6,0,8,9,3,0,1,8,9,40,21,11};
    }

    public String encrypt(String word) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, new IvParameterSpec(iv));
            byte[] bytes = cipher.doFinal(word.getBytes());
            //Parasite characters insertion
            StringBuilder builder = new StringBuilder(Base64.encodeToString(bytes, Base64.NO_WRAP));
            builder.insert(5,"@0@");
            return new String(builder);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String word) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.secretKey, new IvParameterSpec(iv));
            //Parasite characters removing
            StringBuilder builder = new StringBuilder(word);
            builder.delete(5,8);
            byte[] bytes = Base64.decode(new String(builder), Base64.NO_WRAP);

            return new String(cipher.doFinal(bytes));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
