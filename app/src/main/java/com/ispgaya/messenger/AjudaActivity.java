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

    LinearLayout obter_ajuda, pf_ajuda, termos_politica_privacidade_ajuda, infos_ajuda, infos_conexao;

    //info dos utilizadores
    String email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Ajuda");
        //ativar o botao de voltar atras na actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //iniciar auth
        firebaseAuth = FirebaseAuth.getInstance();

        obter_ajuda = findViewById(R.id.obter_ajuda);
        pf_ajuda = findViewById(R.id.pf_ajuda);
        termos_politica_privacidade_ajuda = findViewById(R.id.termos_politica_privacidade_ajuda);
        infos_conexao = findViewById(R.id.infos_conexao);
        infos_ajuda = findViewById(R.id.infos_ajuda);

        //clique no obter ajuda
        obter_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //clique no perguntas frequentes
        pf_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //clique nos termos e privacidade
        termos_politica_privacidade_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.ispgaya.pt/site/")));
            }
        });

        //clique nos informações de conectividade
        infos_conexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AjudaActivity.this, InformacoesConexao.class));
            }
        });

        //clique na mais info
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