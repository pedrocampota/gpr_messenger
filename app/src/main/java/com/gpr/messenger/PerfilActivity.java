package com.gpr.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    TextView mPerfilTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Actionbar com titulo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");

        firebaseAuth = FirebaseAuth.getInstance();

        mPerfilTV = findViewById(R.id.perfilTV);
    }

    private void verificarEstadoUtilizador(){
        //obter utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //utilizador com sessao iniciada
            mPerfilTV.setText(user.getEmail());
        }
        else{
            //utilizador nao esta com a sessao iniciada...ir para a main activity
            startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        //verificar no inicio da app
        verificarEstadoUtilizador();
        super.onStart();
    }

    //iniciar menu de opções

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }

        return super.onOptionsItemSelected(item);
    }
}