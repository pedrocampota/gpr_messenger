package com.ispgaya.messenger;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

public class SplashscreenActivity extends Activity
{
    private ViewSwitcher viewSwitcher;
    private static int SPLASH_SCREEN = 5000;
    Animation nameAnim;
    ImageView name;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new LoadViewTask().execute();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nameAnim = AnimationUtils.loadAnimation(this, R.anim.name_animation);


        name = findViewById(R.id.imageView);


        name.setAnimation(nameAnim);



    }

    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        private TextView tv_progress;
        private ProgressBar pb_progressBar;

        @Override
        protected void onPreExecute()
        {
            viewSwitcher = new ViewSwitcher(SplashscreenActivity.this);

            viewSwitcher.addView(ViewSwitcher.inflate(SplashscreenActivity.this, R.layout.activity_splashscreen, null));

            tv_progress = (TextView) viewSwitcher.findViewById(R.id.tv_progress);
            pb_progressBar = (ProgressBar) viewSwitcher.findViewById(R.id.pb_progressbar);
            pb_progressBar.setMax(100);

            setContentView(viewSwitcher);
        }

        @Override
        protected Void doInBackground(Void... params)
        {

            try
            {
                synchronized (this)
                {
                    int counter = 0;
                    while(counter <= 4)
                    {
                        this.wait(850);
                        counter++;
                        publishProgress(counter*25);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            if(values[0] <= 100)
            {
                tv_progress.setText("Progress: " + Integer.toString(values[0]) + "%");
                pb_progressBar.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Void result)
        {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            String networkid = wifiInfo.getMacAddress();

            if(ssid == "<unknown ssid>"){
                Toast.makeText(SplashscreenActivity.this, "Erro de rede codigo de erro: " + ssid, Toast.LENGTH_LONG).show();
                Intent intent = new Intent (SplashscreenActivity.this,DeadscreenActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(SplashscreenActivity.this, "Connection sucess", Toast.LENGTH_LONG).show();
                Toast.makeText(SplashscreenActivity.this, "ID " + networkid, Toast.LENGTH_LONG).show();
                Toast.makeText(SplashscreenActivity.this, "Nome da rede " + ssid, Toast.LENGTH_LONG).show();
                Intent intent = new Intent (SplashscreenActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }

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