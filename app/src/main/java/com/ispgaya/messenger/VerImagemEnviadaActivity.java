package com.ispgaya.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;


public class VerImagemEnviadaActivity extends AppCompatActivity {

    private String imageUrl;
    private PhotoView photoView;

    //firebase auth
    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;

    ActionBar actionBar;

    //info do utilizador
    String email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagem_enviada);

        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        //ativar o botao de voltar atras na action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //iniciar
        firebaseAuth = FirebaseAuth.getInstance();

        photoView = findViewById(R.id.post_image_view);
        imageUrl = getIntent().getStringExtra("url");

        Picasso.get().load(imageUrl).into(photoView);
    }

    @Override
    public void finish() {
        verificarEstadoUtilizador();
        super.finish();
    }

    private void verificarEstadoUtilizador(){
        //obter o utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            //utilizador conectado
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            //utilizador nao conectacto, ir para a main activity
            startActivity(new Intent(this, MainActivity.class));
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


        menu.findItem(R.id.action_pesquisar).setVisible(false);
        menu.findItem(R.id.action_ajuda).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //obter ids dos itens
        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        return super.onOptionsItemSelected(item);
    }
}