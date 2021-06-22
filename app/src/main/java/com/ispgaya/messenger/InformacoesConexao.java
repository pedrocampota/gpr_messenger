package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class InformacoesConexao extends AppCompatActivity {

    //FireBase auth
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;


    //info dos utilizadores
    String email, uid;
    TextView ssidTV, ipTv, macAddressTv, freqTv, velocidadeTv, velocidadeTv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_conexao);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Estado da ligação");
        //ativar o botao de voltar atras na actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //iniciar auth
        firebaseAuth = FirebaseAuth.getInstance();

        ssidTV = findViewById(R.id.ssidTV);
        ipTv = findViewById(R.id.ipTv);
        macAddressTv = findViewById(R.id.macAddressTv);
        freqTv = findViewById(R.id.freqTv);
        velocidadeTv = findViewById(R.id.velocidadeTv);
        velocidadeTv2 = findViewById(R.id.velocidadeTv2);

        estadoLigacao();
    }

    private void estadoLigacao() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID();
        String networkIp = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        String networkMacAddress = wifiInfo.getMacAddress();
        String networkFreq = String.valueOf(wifiInfo.getFrequency());

        // Connectivity Manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        // Network Capabilities of Active Network
        NetworkCapabilities nc = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        }

        // DownSpeed in MBPS
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        String downSpeed2 = "Download: " + (((double)downSpeed/8)/1000)/8 + "Mb/s";

        // UpSpeed  in MBPS
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
        String upSpeed2 = "Upload: " + (((double)upSpeed/8)/1000)/8 + "Mb/s";

        String networkFreq2 = String.valueOf(Integer.parseInt(Integer.toString(Integer.parseInt(networkFreq)).substring(0, 2)));

        String string_temp = new Double(networkFreq2).toString();
        String string_form = string_temp.substring(0,string_temp.indexOf('.'));
        double t = Double.valueOf(string_form);

        ssidTV.setText(ssid);
        ipTv.setText(networkIp);
        macAddressTv.setText(networkMacAddress);
        freqTv.setText(networkFreq2.replaceAll("(\\d{1})(\\d+)", "$1.$2") + " Ghz");
        velocidadeTv.setText(downSpeed2);
        velocidadeTv2.setText(upSpeed2);

    }

    private void verificarEstadoUtilizador(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            startActivity(new Intent(this, IniciarSessaoActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //ir para a activity anterior

        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_ajuda).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get ITEM ID
        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        return super.onOptionsItemSelected(item);
    }
}