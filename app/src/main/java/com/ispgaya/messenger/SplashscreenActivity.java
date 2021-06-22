package com.ispgaya.messenger;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.util.List;

public class SplashscreenActivity extends Activity {
    private ViewSwitcher viewSwitcher;
    private static int SPLASH_SCREEN = 3000;
    //Animation nameAnim;
    //ImageView name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadViewTask().execute();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //nameAnim = AnimationUtils.loadAnimation(this, R.anim.name_animation);
        //name.setAnimation(nameAnim);

        verificarEstadoWifi();
    }

    private void verificarEstadoWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // wifi is enabled
        }
        else{
            Intent intent = new Intent (SplashscreenActivity.this,DeadscreenActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        private TextView tv_progress;
        private ProgressBar pb_progressBar;

        @Override
        protected void onPreExecute() {
            viewSwitcher = new ViewSwitcher(SplashscreenActivity.this);

            viewSwitcher.addView(ViewSwitcher.inflate(SplashscreenActivity.this, R.layout.activity_splashscreen, null));

            tv_progress = (TextView) viewSwitcher.findViewById(R.id.tv_progress);
            pb_progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.pb_progressbar);
            pb_progressBar.setMax(100);
            pb_progressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));

            setContentView(viewSwitcher);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                synchronized (this) {
                    int counter = 0;
                    while (counter <= 4) {
                        this.wait(850);
                        counter++;
                        publishProgress(counter * 25);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] <= 100) {
                tv_progress.setText("A ligar à rede... " + Integer.toString(values[0]) + "%");
                pb_progressBar.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            List<ScanResult> mScanResults = wifiManager.getScanResults();

            String ssid = wifiInfo.getSSID();
            String ssidChave = "ISPGayaRadius";
            String networkIp = Formatter.formatIpAddress(wifiInfo.getIpAddress());
            String networkMacAddress = wifiInfo.getMacAddress();

            for (ScanResult results : mScanResults){

                if (results.SSID.equals(ssidChave)){
                    Toast.makeText(SplashscreenActivity.this, "Conexão à rede realizada com sucesso.", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SplashscreenActivity.this, "Nome da rede: " + ssid, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SplashscreenActivity.this, "Endereço Ip: " + networkIp, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SplashscreenActivity.this, "Endereço Mac: " + networkMacAddress, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (SplashscreenActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();

                    break;
                }
                else{
                    Toast.makeText(SplashscreenActivity.this, "Impossível conectar à rede: " + ssid, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (SplashscreenActivity.this,DeadscreenActivity.class);
                    startActivity(intent);
                    finish();

                    break;
                }

            }

            /*if(ssid == "<unknown ssid>"){
                Toast.makeText(SplashscreenActivity.this, "Impossível conectar à rede: " + ssid, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (SplashscreenActivity.this,DeadscreenActivity.class);
                startActivity(intent);
                finish();

            }
            else{
                Toast.makeText(SplashscreenActivity.this, "Conexão realizada com sucesso.", Toast.LENGTH_SHORT).show();
                Toast.makeText(SplashscreenActivity.this, "Nome da rede: " + ssid, Toast.LENGTH_SHORT).show();
                Toast.makeText(SplashscreenActivity.this, "Endereço Ip: " + networkIp, Toast.LENGTH_SHORT).show();
                Toast.makeText(SplashscreenActivity.this, "Endereço Mac: " + networkMacAddress, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (SplashscreenActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }*/
        }
    }

    @Override
    public void onBackPressed()
    {
        if(viewSwitcher.getDisplayedChild() == 0)
        {
            return;
        }
        else
        {
            super.onBackPressed();
        }
    }
}