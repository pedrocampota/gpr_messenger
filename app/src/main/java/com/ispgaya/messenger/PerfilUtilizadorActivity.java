package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PerfilUtilizadorActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    //views do xml
    ImageView imagemIv, capaIv;
    TextView nomeTv, emailTv, estadoPerfilTv, nomeCursoTv, numAlunoTv, anoCursoTv, generoTv, idadeTv, semRedesSociaisTv;
    ImageButton facebookBt, twitterBt, instagramBt, linkedinBt, githubBt;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_utilizador);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //iniciar views
        imagemIv = findViewById(R.id.avatarIv);
        capaIv = findViewById(R.id.capaIv);
        nomeTv = findViewById(R.id.nomeTv);
        emailTv = findViewById(R.id.emailTv);
        estadoPerfilTv = findViewById(R.id.estadoPerfilTv);
        nomeCursoTv = findViewById(R.id.nomeCursoTv);
        numAlunoTv = findViewById(R.id.numAlunoTv);
        anoCursoTv = findViewById(R.id.anoCursoTv);
        idadeTv = findViewById(R.id.idadeTv);
        generoTv = findViewById(R.id.generoTv);
        facebookBt = findViewById(R.id.facebookBt);
        twitterBt = findViewById(R.id.twitterBt);
        instagramBt = findViewById(R.id.instagramBt);
        linkedinBt = findViewById(R.id.linkedinBt);
        githubBt = findViewById(R.id.githubBt);
        semRedesSociaisTv = findViewById(R.id.semRedesSociaisTv);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");


        Query query = FirebaseDatabase.getInstance().getReference("Utilizadores").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data

                    String nome = ""+ ds.child("nome").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String telemovel = ""+ ds.child("telemovel").getValue();
                    String estadoPerfil = ""+ ds.child("estadoPerfil").getValue();
                    String idade = ""+ ds.child("idade").getValue();
                    String genero = ""+ ds.child("genero").getValue();
                    String nomeCurso = ""+ ds.child("nomeCurso").getValue();
                    String numAluno = ""+ ds.child("numAluno").getValue();
                    String anoCurso = ""+ ds.child("anoCurso").getValue();
                    String imagem = ""+ ds.child("imagem").getValue();
                    String capa = ""+ ds.child("capa").getValue();
                    String facebook = ""+ ds.child("facebook").getValue();
                    String twitter = ""+ ds.child("twitter").getValue();
                    String instagram = ""+ ds.child("instagram").getValue();
                    String linkedin = ""+ ds.child("linkedin").getValue();
                    String github = ""+ ds.child("github").getValue();

                    //set data
                    nomeTv.setText(nome);
                    emailTv.setText(email);
                    estadoPerfilTv.setText(estadoPerfil);
                    idadeTv.setText(idade);
                    generoTv.setText(genero);
                    nomeCursoTv.setText(nomeCurso);
                    numAlunoTv.setText(numAluno);
                    anoCursoTv.setText(anoCurso);
                    //facebookBt.setText(facebook);
                    //twitterBt.setText(twitter);
                    //instagramBt.setText(instagram);
                    //linkedinBt.setText(linkedin);
                    //githubBt.setText(github);

                    if (facebook.equals("") && twitter.equals("") && instagram.equals("") && linkedin.equals("") &&  github.equals("")){
                        semRedesSociaisTv.setVisibility(View.VISIBLE);
                    }
                    else{
                        semRedesSociaisTv.setVisibility(View.GONE);
                    }
                    if (facebook.equals("")){
                        facebookBt.setVisibility(View.GONE);
                    }
                    else{
                        facebookBt.setVisibility(View.VISIBLE);
                    }

                    if (twitter.equals("")){
                        twitterBt.setVisibility(View.GONE);
                    }
                    else{
                        twitterBt.setVisibility(View.VISIBLE);
                    }

                    if (instagram.equals("")){
                        instagramBt.setVisibility(View.GONE);
                    }
                    else{
                        instagramBt.setVisibility(View.VISIBLE);
                    }

                    if (linkedin.equals("")){
                        linkedinBt.setVisibility(View.GONE);
                    }
                    else{
                        linkedinBt.setVisibility(View.VISIBLE);
                    }

                    if (github.equals("")){
                        githubBt.setVisibility(View.GONE);
                    }
                    else{
                        githubBt.setVisibility(View.VISIBLE);
                    }


                    facebookBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(facebook)));
                        }
                    });

                    twitterBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(twitter)));
                        }
                    });

                    instagramBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(instagram)));
                        }
                    });

                    linkedinBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(linkedin)));
                        }
                    });

                    githubBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(github)));
                        }
                    });

                    try {
                        // se a imagem for recebida, defina
                        Picasso.get().load(imagem).resize(350, 350).centerCrop().into(imagemIv);
                    }
                    catch (Exception e){
                        // se houver alguma exceção ao obter a imagem, defina o padrão
                        Picasso.get().load(R.drawable.img_default_utilizador).placeholder(R.drawable.img_default_utilizador).into(imagemIv);
                    }

                    try {
                        // se a imagem for recebida, defina
                        Picasso.get().load(capa).resize(1000, 700).centerCrop().into(capaIv);
                    }
                    catch (Exception e){
                        // se houver alguma exceção ao obter a imagem, defina o padrão
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        verificarEstadoUtilizador();

    }

    private void verificarEstadoUtilizador(){
        //obter o utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            // o utilizador está conectado
            //mPerfilTv.setText(user.getEmail());
        }
        else {
            // utilizador não conectado, vá para a atividade principal
            startActivity(new Intent(this, IniciarSessaoActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_pesquisar).setVisible(false); //esconder o botao de pesquisa

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        else if (id == R.id.action_ajuda){
            startActivity(new Intent(this , AjudaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}