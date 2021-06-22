package com.ispgaya.messenger;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class DeadscreenActivity extends Activity {
    private Button refresh;
    ImageView imageview;
    TextView erroWifiTV, erroRedeTv2, contactar_centro_suporte, saberMaisErroRedeTv;
    LinearLayout mensagemErroLN1, mensagemErroLN2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadscreen);

        imageview = findViewById(R.id.imageView2);
        mensagemErroLN1 = findViewById(R.id.mensagemErroLN1);
        mensagemErroLN2 = findViewById(R.id.mensagemErroLN2);

        erroWifiTV = findViewById(R.id.erroWifiTV);
        erroRedeTv2 = findViewById(R.id.erroRedeTv2);
        saberMaisErroRedeTv = findViewById(R.id.saberMaisErroRedeTv);

        contactar_centro_suporte = findViewById(R.id.contactar_centro_suporte);

        refresh = (Button) findViewById(R.id.button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeadscreenActivity.this, SplashscreenActivity.class));
            }
        });

        verificarEstadoWifi();

        //clique no contactar_centro_suporte
        contactar_centro_suporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //clique no saberMaisErroRedeTv
        saberMaisErroRedeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });
    }

    private void verificarEstadoWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // wifi is enabled
            mensagemErroLN1.setVisibility(View.GONE);
            mensagemErroLN2.setVisibility(View.VISIBLE);
        }
        else{
            mensagemErroLN1.setVisibility(View.VISIBLE);
            mensagemErroLN2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
