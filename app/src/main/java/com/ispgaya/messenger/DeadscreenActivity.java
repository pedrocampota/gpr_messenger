package com.ispgaya.messenger;

import android.content.Intent;
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
import android.widget.Toast;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class DeadscreenActivity extends Activity {
    private Button refresh;
    ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_deadscreen);

        imageview = findViewById(R.id.imageView2);

        refresh = (Button) findViewById(R.id.button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeadscreenActivity.this, SplashscreenActivity.class));
            }
        });
    }
}
