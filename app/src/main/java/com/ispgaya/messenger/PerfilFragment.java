package com.ispgaya.messenger;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class PerfilFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
    //caminho onde as imagens do perfil do utilizador e da capa serão armazenadas
    String storagePath = "Utilizadores-Foto-Capa-Perfil/";

    //views do xml
    ImageView avatarIv, capaIv;
    ImageButton facebookBt, twitterBt, instagramBt, linkedinBt, githubBt;
    TextView nomeTv, emailTv, telemovelTv, estadoPerfilTv, nomeCursoTv, numAlunoTv, anoCursoTv, generoTv, idadeTv, semRedesSociaisTv;
    FloatingActionButton fab;
    LinearLayout perfilLN2;

    //diálogo de progresso
    ProgressDialog pd;

    //constantes de permissões
    private static final  int CAMERA_REQUEST_CODE = 100;
    private static final  int STORAGE_REQUEST_CODE = 200;
    private static final  int IMAGE_PICK_GALLERY_CODE = 300;
    private static final  int IMAGE_PICK_CAMERA_CODE = 400;
    //arrays de permissoes que serao requisitadas
    String cameraPermissions[];
    String storagePermissions[];

    String uid;

    //uri da imagem escolhida
    Uri image_uri;

    //verificar se a foto é de perfil ou de capa
    String imagemOuCapa;

    public PerfilFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        //iniciar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Utilizadores");
        storageReference = getInstance().getReference();//referencia firebase storage

        //iniciar permissoes
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //iniciar views
        avatarIv = view.findViewById(R.id.avatarIv);
        capaIv = view.findViewById(R.id.capaIv);
        nomeTv = view.findViewById(R.id.nomeTv);
        emailTv = view.findViewById(R.id.emailTv);
        telemovelTv = view.findViewById(R.id.numTelemovelTv);
        estadoPerfilTv = view.findViewById(R.id.estadoPerfilTv);
        nomeCursoTv = view.findViewById(R.id.nomeCursoTv);
        numAlunoTv = view.findViewById(R.id.numAlunoTv);
        anoCursoTv = view.findViewById(R.id.anoCursoTv);
        idadeTv = view.findViewById(R.id.idadeTv);
        generoTv = view.findViewById(R.id.generoTv);
        facebookBt = view.findViewById(R.id.facebookBt);
        twitterBt = view.findViewById(R.id.twitterBt);
        instagramBt = view.findViewById(R.id.instagramBt);
        linkedinBt = view.findViewById(R.id.linkedinBt);
        githubBt = view.findViewById(R.id.githubBt);
        semRedesSociaisTv = view.findViewById(R.id.semRedesSociaisTv);
        perfilLN2 = view.findViewById(R.id.perfilLN2);


        fab = view.findViewById(R.id.fab);

        //initciar dialogo de progresso
        pd = new ProgressDialog(getActivity());

        /*/ * Precisamos obter informações do utilizador conectado no momento. Podemos obtê-lo usando o e-mail dos usuários ou uid **
        /* Usando a consulta orderByChild, mostraremos o detalhe de um nó cuja chave chamada email tem valor igual ao atualmente assinado no email.
         * Ele pesquisará todos os nós, onde as correspondências de chave que obterá são detalhes */
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // verificar até que os dados necessários sejam obtidos
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //obter dados
                    String nome = ""+ ds.child("nome").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String telemovel = ""+ ds.child("telemovel").getValue();
                    String estadoPerfil = ""+ ds.child("estadoPerfil").getValue();
                    String idade = ""+ ds.child("idade").getValue();
                    String genero = ""+ ds.child("genero").getValue();
                    String nomeCurso = ""+ ds.child("nomeCurso").getValue();
                    String numAluno = ""+ ds.child("numAluno").getValue();
                    String anoCurso = ""+ ds.child("anoCurso").getValue();
                    String facebook = ""+ ds.child("facebook").getValue();
                    String twitter = ""+ ds.child("twitter").getValue();
                    String instagram = ""+ ds.child("instagram").getValue();
                    String linkedin = ""+ ds.child("linkedin").getValue();
                    String github = ""+ ds.child("github").getValue();

                    String imagem = ""+ ds.child("imagem").getValue();
                    String capa = ""+ ds.child("capa").getValue();

                    //definir dados
                    nomeTv.setText(nome);
                    emailTv.setText(email);
                    telemovelTv.setText(telemovel);
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
                        Picasso.get().load(imagem).resize(350, 350).centerCrop().into(avatarIv);
                    }
                    catch (Exception e){
                        // se houver alguma exceção ao obter a imagem, defina o padrão
                        Picasso.get().load(R.drawable.img_default_utilizador).placeholder(R.drawable.img_default_utilizador).into(avatarIv);
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



        /*fab clique*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DefinicoesPerfilActivity.class);
                startActivity(intent);
            }
        });


        verificarEstadoUtilizador();

        return view;
    }

    private void verificarEstadoUtilizador(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            //PerfilTv.setText(user.getEmail());
            uid = user.getUid();
        }
        else {
            startActivity(new Intent(getActivity(), IniciarSessaoActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //carregar menu
        inflater.inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_pesquisar).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //obter id dos itens
        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        else if (id == R.id.action_ajuda){
            startActivity(new Intent(getActivity(), AjudaActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}