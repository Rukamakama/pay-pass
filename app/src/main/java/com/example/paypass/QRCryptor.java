package com.example.paypass;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paypass.controler.Lugu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class QRCryptor extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] files = {"abonnees.txt", "admins.txt", "agents.txt", "bus.txt", "clients.txt"};

        for (String file : files) {
            try (BufferedReader br = new BufferedReader(new
                    InputStreamReader(getAssets().open(file)))) {

                List<String> plainCodes = new ArrayList<>(getSize(file));
                String strLine;
                while ((strLine = br.readLine()) != null)
                    plainCodes.add(strLine);

                hash(plainCodes, file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("SOLID", "End of process");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void hash(List<String> plainCodes, String fileName) throws IOException {

        // For redundancy removal in the list
        Log.d("SOLID", "Before: " + plainCodes.size());
        Set<String> toRetain = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        toRetain.addAll(plainCodes);
        Set<String> set = new LinkedHashSet<>(plainCodes);
        set.retainAll(new LinkedHashSet<>(toRetain));
        plainCodes = new ArrayList<>(set);
        Log.d("SOLID", "After: " + plainCodes.size());

        List<String> hashedCodes = new ArrayList<>(25000);
        Lugu lugu = new Lugu();
        for (String code : plainCodes)
            hashedCodes.add(lugu.encrypt(code));

        Path path = Paths.get(getApplicationInfo().dataDir + "/"+fileName);
        Files.write(path, hashedCodes, StandardCharsets.UTF_8);

    }

    private int getSize(String file){
        switch (file){
            case "abonnees.txt":
                return 5350;
            case "admins.txt":
                return 4;
            case "agents.txt":
                return 500;
            case "bus.txt":
                return 102;
            case "clients.txt":
                return 95016;
        }
        return 10;
    }
}
