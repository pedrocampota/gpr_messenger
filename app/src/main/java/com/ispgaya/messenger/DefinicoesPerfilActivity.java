package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class DefinicoesPerfilActivity extends AppCompatActivity {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;

    ActionBar actionBar;

    //permissoes
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //constantes das picks das imagens
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //array de permissoes
    String[] cameraPermissions;
    String[] storagePermissions;

    String imagemOuCapaFoto;

    //views
    EditText nomeEtDefinicoesPerfil, estadoEtDefinicoesPerfil, numTelemovelEtDefinicoesPerfil, nomeCursoEtDefinicoesPerfil, anoCursoEtDefinicoesPerfil, numAlunoEtDefinicoesPerfil, idadeEtDefinicoesPerfil, generoEtDefinicoesPerfil, facebookEtDefinicoesPerfil, twitterEtDefinicoesPerfil, instagramEtDefinicoesPerfil, linkedinEtDefinicoesPerfil, githubEtDefinicoesPerfil;
    ImageButton atualizarImagemBtn, atualizarCapaBtn;
    Button atualizarDefinicoesPerfilBtn;
    ImageView imagemIvDefinicoesPerfil, capaIvDefinicoesPerfil;
    TextView emailTvDefinicoesPerfil, meuUtilizadorIdDefinicoesPerfil;

    //info utilizador
    String nome, email, telemovel, imagem, estado, uid, capa, nomeCurso, anoCurso, numAluno, idade, genero, facebook, twitter, instagram, linkedin, github;

    //imagem escolhida vai ser salva neste uri
    Uri image_rui = null;

    ProgressDialog pd;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;

    String storagePath = "Utilizadores-Foto-Capa-Perfil/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes_perfil);


        actionBar = getSupportActionBar();
        actionBar.setTitle("Definições de Perfil");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Utilizadores");
        storageReference = getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        verificarEstadoUtilizador();

        //iniciar views
        nomeEtDefinicoesPerfil = findViewById(R.id.nomeEtDefinicoesPerfil);
        estadoEtDefinicoesPerfil = findViewById(R.id.estadoEtDefinicoesPerfil);
        numTelemovelEtDefinicoesPerfil = findViewById(R.id.numTelemovelEtDefinicoesPerfil);
        nomeCursoEtDefinicoesPerfil = findViewById(R.id.nomeCursoEtDefinicoesPerfil);
        anoCursoEtDefinicoesPerfil = findViewById(R.id.anoCursoEtDefinicoesPerfil);
        numAlunoEtDefinicoesPerfil = findViewById(R.id.numAlunoEtDefinicoesPerfil);
        idadeEtDefinicoesPerfil = findViewById(R.id.idadeEtDefinicoesPerfil);
        generoEtDefinicoesPerfil = findViewById(R.id.generoEtDefinicoesPerfil);
        facebookEtDefinicoesPerfil = findViewById(R.id.facebookEtDefinicoesPerfil);
        twitterEtDefinicoesPerfil = findViewById(R.id.twitterEtDefinicoesPerfil);
        instagramEtDefinicoesPerfil = findViewById(R.id.instagramEtDefinicoesPerfil);
        linkedinEtDefinicoesPerfil = findViewById(R.id.linkedinEtDefinicoesPerfil);
        githubEtDefinicoesPerfil = findViewById(R.id.githubEtDefinicoesPerfil);

        atualizarImagemBtn = findViewById(R.id.atualizarImagemBtn);
        atualizarDefinicoesPerfilBtn = findViewById(R.id.atualizarDefinicoesPerfilBtn);
        atualizarCapaBtn = findViewById(R.id.atualizarCapaBtn);
        imagemIvDefinicoesPerfil = findViewById(R.id.imagemIvDefinicoesPerfil);
        capaIvDefinicoesPerfil = findViewById(R.id.capaIvDefinicoesPerfil);
        emailTvDefinicoesPerfil = findViewById(R.id.emailTvDefinicoesPerfil);
        meuUtilizadorIdDefinicoesPerfil = findViewById(R.id.meuUtilizadorIdDefinicoesPerfil);

        //obter dados por meio da intenção do adaptador de atividades anterior
        Intent intent = getIntent();
        final String isUpdateKey = ""+intent.getStringExtra("key");
        final String editProfileAction = ""+intent.getStringExtra("updateCUP");

        lerInfoUtilizador();

        //obter imagem da camera//galeria ao clicar
        atualizarImagemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
                imagemOuCapaFoto = "imagem";
            }
        });

        //obter imagem da camera//galeria ao clicar
        atualizarCapaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
                imagemOuCapaFoto = "capa";

            }
        });

        //atualizar definiçoes de perfil btn
        atualizarDefinicoesPerfilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtualizarDefinicoes("nome");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
    }

    private void AtualizarDefinicoes(final String key)
    {
        String setNomeUtilizador = nomeEtDefinicoesPerfil.getText().toString();
        String setEstado = estadoEtDefinicoesPerfil.getText().toString();
        String setNumTelemovel = numTelemovelEtDefinicoesPerfil.getText().toString();
        String setNomeCurso = nomeCursoEtDefinicoesPerfil.getText().toString();
        String setAnoCurso = anoCursoEtDefinicoesPerfil.getText().toString();
        String setNumAluno = numAlunoEtDefinicoesPerfil.getText().toString();
        String setIdade = idadeEtDefinicoesPerfil.getText().toString();
        String setGenero = generoEtDefinicoesPerfil.getText().toString();
        String setFacebook = facebookEtDefinicoesPerfil.getText().toString();
        String setTwitter = twitterEtDefinicoesPerfil.getText().toString();
        String setInstagram = instagramEtDefinicoesPerfil.getText().toString();
        String setLinkedIn = linkedinEtDefinicoesPerfil.getText().toString();
        String setGitHub = githubEtDefinicoesPerfil.getText().toString();


        if (TextUtils.isEmpty(setNomeUtilizador))
        {
            nomeEtDefinicoesPerfil.setError("Deve ser preenchido o primeiro e último nome");
            nomeEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setEstado))
        {
            estadoEtDefinicoesPerfil.setError("Deve ser preenchido um estado de perfil");
            estadoEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setNumTelemovel))
        {
            numTelemovelEtDefinicoesPerfil.setError("Deve ser preenchido o número de telemóvel pessoal");
            numTelemovelEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setNomeCurso))
        {
            nomeCursoEtDefinicoesPerfil.setError("Deve ser preenchido o nome do curso frequentado");
            nomeCursoEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setAnoCurso))
        {
            anoCursoEtDefinicoesPerfil.setError("Deve ser preenchido o número correspondente ao ano do curso");
            anoCursoEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setNumAluno))
        {
            numAlunoEtDefinicoesPerfil.setError("Deve ser preenchido o número de aluno");
            numAlunoEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setIdade))
        {
            idadeEtDefinicoesPerfil.setError("Deve ser preenchida a idade");
            idadeEtDefinicoesPerfil.setFocusable(true);
        }
        else if (TextUtils.isEmpty(setGenero))
        {
            generoEtDefinicoesPerfil.setError("Deve ser preenchida o género");
            generoEtDefinicoesPerfil.setFocusable(true);
        }
        else if(!setNomeUtilizador.equals("") && !setEstado.equals("") && !setNumTelemovel.equals("") && !setNomeCurso.equals("") && !setAnoCurso.equals("") && !setNumAluno.equals("") && !setIdade.equals("") && !setGenero.equals("")){
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("nome", setNomeUtilizador);
            profileMap.put("estadoPerfil", setEstado);
            profileMap.put("telemovel", setNumTelemovel);
            profileMap.put("nomeCurso", setNomeCurso);
            profileMap.put("anoCurso", setAnoCurso);
            profileMap.put("numAluno", setNumAluno);
            profileMap.put("idade", setIdade);
            profileMap.put("genero", setGenero);
            profileMap.put("facebook", setFacebook);
            profileMap.put("twitter", setTwitter);
            profileMap.put("instagram", setInstagram);
            profileMap.put("linkedin", setLinkedIn);
            profileMap.put("github", setGitHub);
            profileMap.put(key, setNomeUtilizador);

            databaseReference.child(user.getUid()).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(DefinicoesPerfilActivity.this, "Informação do Perfil atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(DefinicoesPerfilActivity.this, "Erro: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void lerInfoUtilizador() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Utilizadores");

        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //obter dadaos
                    nome = ""+ds.child("nome").getValue();
                    uid = ""+ds.child("uid").getValue();
                    email = ""+ds.child("email").getValue();
                    telemovel = ""+ds.child("telemovel").getValue();
                    estado = ""+ ds.child("estadoPerfil").getValue();
                    nomeCurso = ""+ ds.child("nomeCurso").getValue();
                    anoCurso = ""+ ds.child("anoCurso").getValue();
                    numAluno = ""+ ds.child("numAluno").getValue();
                    idade = ""+ ds.child("idade").getValue();
                    genero = ""+ ds.child("genero").getValue();
                    facebook = ""+ ds.child("facebook").getValue();
                    twitter = ""+ ds.child("twitter").getValue();
                    instagram = ""+ ds.child("instagram").getValue();
                    linkedin = ""+ ds.child("linkedin").getValue();
                    github = ""+ ds.child("github").getValue();
                    imagem = ""+ ds.child("imagem").getValue();
                    capa = ""+ ds.child("capa").getValue();

                    //inserir dados nas views
                    //inserir dados
                    nomeEtDefinicoesPerfil.setText(nome);
                    emailTvDefinicoesPerfil.setText(email);
                    numTelemovelEtDefinicoesPerfil.setText(telemovel);
                    meuUtilizadorIdDefinicoesPerfil.setText(uid);
                    estadoEtDefinicoesPerfil.setText(estado);
                    nomeCursoEtDefinicoesPerfil.setText(nomeCurso);
                    anoCursoEtDefinicoesPerfil.setText(anoCurso);
                    numAlunoEtDefinicoesPerfil.setText(numAluno);
                    idadeEtDefinicoesPerfil.setText(idade);
                    generoEtDefinicoesPerfil.setText(genero);
                    facebookEtDefinicoesPerfil.setText(facebook);
                    twitterEtDefinicoesPerfil.setText(twitter);
                    instagramEtDefinicoesPerfil.setText(instagram);
                    linkedinEtDefinicoesPerfil.setText(linkedin);
                    githubEtDefinicoesPerfil.setText(github);



                    //definir imagem
                    if (!imagem.equals("noImage")){
                        try {
                            Picasso.get().load(imagem).fit().centerCrop().into(imagemIvDefinicoesPerfil);
                            Picasso.get().load(capa).fit().centerCrop().into(capaIvDefinicoesPerfil);
                        }
                        catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showImagePickDialog() {
        String[] option = {"Camera", "Galeria"};

        //dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolher imagem da");
        //definir opções de dialogo
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    //clicou em camera
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if (which==1){
                    //clicou em galeria
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        //criar e mostrar dialogo
        builder.create().show();
    }

    private void pickFromGallery() {
        //intent para escolher imagem da galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //intent para escolher imagem da camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        //verificar se as permissoes estão ativadas
        //obter true se ativadas
        //obter false se desativadas
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //obter permissao
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        //verificar se as permissoes estão ativadas
        //obter true se ativadas
        //obter false se desativadas
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //obter permissao
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarEstadoUtilizador();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarEstadoUtilizador();
    }

    @Override
    protected void onStop() {
        super.onStop();
        verificarEstadoUtilizador();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    private void verificarEstadoUtilizador(){
        //obter utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            email = user.getEmail();
            uid = user.getUid();
        }
        else {
            //o utilizador nao tem sessao inciada portanto voltar para a mainactivity
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
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_ajuda).setVisible(false);
        menu.findItem(R.id.action_terminar_sessao).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //obter id do item
        int id = item.getItemId();
        if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //este metodo é chamado quando o utilizador clique em Permitir ou Não Permitir permissoes
        //aqui é onde se faz a gestao  (permitido e nao permitido)

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //as permissoes forem garantidas
                        pickFromCamera();
                    }
                    else {
                        //camera e galeria tem permissoes negadas
                        Toast.makeText(this, "Permissões de acesso à Camera e Armazenamento são necessárias...", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permissoes garantidas
                        pickFromGallery();
                    }
                    else {
                        //galeria e camera tem permissoes negadas
                        Toast.makeText(this, "Permissões de armazenamento são necessárias...", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //este metodo é chamado apos escolher uma imagem a partir da camera ou da galeria

        if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode ==  RESULT_OK){
            Uri imagePath = data.getData();

            CropImage.activity(imagePath)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(DefinicoesPerfilActivity.this);
        }

        if (requestCode == IMAGE_PICK_CAMERA_CODE && resultCode ==  RESULT_OK){

            CropImage.activity(image_rui)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(DefinicoesPerfilActivity.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK){

                image_rui = result.getUri();
                //Log.d(TAG, "onActivityResult: Image Uri " + image_rui.toString());


                File actualImage = new File(image_rui.getPath());

                try {
                    Bitmap compressedImage = new Compressor(this)
                            .setMaxWidth(250)
                            .setMaxHeight(250)
                            .setQuality(25)
                            .compressToBitmap(actualImage);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] final_image = baos.toByteArray();

                    //mostrar progresso

                    //caminho e nome da imagem para enviar para a firebase storage
                    String filePathAndName = storagePath+ ""+ imagemOuCapaFoto +"_"+ user.getUid();

                    StorageReference storageReference2nd = storageReference.child(filePathAndName);

                    UploadTask uploadTask = storageReference2nd.putBytes(final_image);

                    uploadTask
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //imagem carregada na storage, obter url e enviar para a base de dados
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    final Uri downloadUri = uriTask.getResult();

                                    //verificar se a imagem está a ser carregar ou não e se o url foi recebido
                                    if (uriTask.isSuccessful()){
                                        //imagem carregada
                                        //adicionar e atualizar os urls na base de dados
                                        HashMap<String, Object> results = new HashMap<>();
                                        /*o primeiro parametro da imagemOuCapafoto tem o valor de "imagem" ou "capa" que são keys na base de dados de cada utilizador onde o url vai ser guardado
                                         * o segundo parametro inclui o url da imagem guardada na firebase storage*/
                                        results.put(imagemOuCapaFoto, downloadUri.toString());

                                        databaseReference.child(user.getUid()).updateChildren(results)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //url adicionado com sucesso na base de dados do utilizador
                                                        //fechar progress bar
                                                        pd.dismiss();
                                                        Toast.makeText(DefinicoesPerfilActivity.this, "Imagem Atualizada.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //erro ao adicionar url na base de dados dos utilizadores
                                                        //fechar progress bar
                                                        pd.dismiss();
                                                        Toast.makeText(DefinicoesPerfilActivity.this, "Ocorreu um erro ao atualizar a imagem.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else{
                                        //erro
                                        pd.dismiss();
                                        Toast.makeText(DefinicoesPerfilActivity.this, "Ocorreu um erro.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //ocorreu um erro geral, obter erro e mostar erro
                                    pd.dismiss();
                                    Toast.makeText(DefinicoesPerfilActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}