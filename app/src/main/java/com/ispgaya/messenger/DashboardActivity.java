package com.ispgaya.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ispgaya.messenger.notificacoes.Token;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    String meuUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Actionbar e o titulo
        actionBar = getSupportActionBar();
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.app_bar_layout);

        firebaseAuth = FirebaseAuth.getInstance();

        //navbar de fundo
        BottomNavigationView navigationView = findViewById(R.id.navegacao);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //transação de fragmento (default)
        ConversasListaFragment fragment1 = new ConversasListaFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.conteudo, fragment1, "");
        ft1.commit();

        verificarEstadoUtilizador();
    }

    @Override
    protected void onResume() {
        verificarEstadoUtilizador();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(meuUid).setValue(mToken);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //gerir clique dos botoes
                    switch (item.getItemId()){
                        case R.id.nav_conversas:
                            //transação de fragmento
                            //actionBar.setTitle("Conversas");
                            ConversasListaFragment fragment1 = new ConversasListaFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.conteudo, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_utilizadores:
                            //transação de fragmento
                            // actionBar.setTitle("Utilizadores");
                            UtilizadoresFragment fragment2 = new UtilizadoresFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.conteudo, fragment2, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_perfil:
                            //transação de fragmento
                            //actionBar.setTitle("Perfil");
                            PerfilFragment fragment3 = new PerfilFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.conteudo, fragment3, "");
                            ft3.commit();
                            return true;
                    }

                    return false;
                }
            };

    private void verificarEstadoUtilizador(){
        //obter utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            //utilizador com sessao iniciada
            meuUid = user.getUid();

            //save uid of currently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", meuUid);
            editor.apply();


            //atualizar o token
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
        else{
            //utilizador nao esta com a sessao iniciada...ir para a main activity
            startActivity(new Intent(DashboardActivity.this, IniciarSessaoActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        //verificar no inicio da app
        verificarEstadoUtilizador();
        super.onStart();
    }
}