package com.example.mcdonalds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.AbstractList;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            }
        }, 4000);
        dialog.dismiss();
    }
}