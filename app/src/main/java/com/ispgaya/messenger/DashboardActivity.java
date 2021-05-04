package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Actionbar com titulo
        actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");

        firebaseAuth = FirebaseAuth.getInstance();

        //navbar de fundo
        BottomNavigationView navigationView = findViewById(R.id.navegacao);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //transação de fragmento (default)
        actionBar.setTitle("Inicio");
        InicioFragment fragment1 = new InicioFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.conteudo, fragment1, "");
        ft1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //gerir clique dos botoes
                    switch (item.getItemId()){
                        case R.id.nav_inicio:
                            //transação de fragmento
                            actionBar.setTitle("Inicio");
                            InicioFragment fragment1 = new InicioFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.conteudo, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_perfil:
                            //transação de fragmento
                            actionBar.setTitle("Perfil");
                            PerfilFragment fragment2 = new PerfilFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.conteudo, fragment2, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_utilizadores:
                            //transação de fragmento
                            actionBar.setTitle("Utilizadores");
                            UtilizadoresFragment fragment3 = new UtilizadoresFragment();
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
        }
        else{
            //utilizador nao esta com a sessao iniciada...ir para a main activity
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
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

    //iniciar menu de opções

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*gerir o click nos itens do menu*/
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