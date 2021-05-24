package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AjudaActivity extends AppCompatActivity {

    //FireBase auth
    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    ActionBar actionBar;

    LinearLayout obter_ajuda, pf_ajuda, termos_politica_privacidade_ajuda, infos_ajuda;

    //user info
    String email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Ajuda");
        //enable back button in action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        obter_ajuda = findViewById(R.id.obter_ajuda);
        pf_ajuda = findViewById(R.id.pf_ajuda);
        termos_politica_privacidade_ajuda = findViewById(R.id.termos_politica_privacidade_ajuda);
        infos_ajuda = findViewById(R.id.infos_ajuda);

        //handle terms get_help text view click
        obter_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //handle terms fqa_help text view click
        pf_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //handle terms text view click
        termos_politica_privacidade_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        infos_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AjudaActivity.this, AjudaInfosActivity.class));
            }
        });
    }

    private void verificarEstadoUtilizador(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity

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