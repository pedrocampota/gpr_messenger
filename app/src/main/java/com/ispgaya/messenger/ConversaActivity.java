package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.ispgaya.messenger.adapters.AdapterConversa;
import com.ispgaya.messenger.adapters.AdapterUtilizador;
import com.ispgaya.messenger.models.ModelConversa;
import com.ispgaya.messenger.models.ModelUtilizador;
import com.ispgaya.messenger.notificacoes.Dados;
import com.ispgaya.messenger.notificacoes.Remetente;
import com.ispgaya.messenger.notificacoes.Token;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConversaActivity extends AppCompatActivity {
    ActionBar actionBar;

    //views do xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView perfilIv;
    TextView nomeTv, estadoTv;
    EditText mensagemEt;
    ImageButton enviarBtn, adicionarBtn;

    //firebase auth
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    List<ModelConversa> conversaList;
    AdapterConversa adapterConversa;


    String destinatarioUid;
    String meuUid;
    String imagemDele;

    //volley pedido para as notificações em lista!
    private RequestQueue requestQueue;

    private boolean notify = false;

    //permissões
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //constantes para image pick
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //array de permissoes
    String[] cameraPermissions;
    String[] storagePermissions;
    //imagem escolhida será guardada neste uri
    Uri image_rui = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);
        //iniciar views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        //ativar o botao de voltar atras na actionbar
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //iniciar arrays de permissoes
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        recyclerView = findViewById(R.id.conversa_recyclerView);
        perfilIv = findViewById(R.id.perfilIv);
        nomeTv = findViewById(R.id.nomeTv);
        estadoTv = findViewById(R.id.estadoTv);
        mensagemEt = findViewById(R.id.mensagemEt);
        enviarBtn = findViewById(R.id.enviarBtn);
        adicionarBtn = findViewById(R.id.adicionarBtn);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Layout(LinearLayout) para RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //propriedades da recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        destinatarioUid = intent.getStringExtra("destinatarioUid");

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Utilizadores");

        //pesquisra pelo utilizador para obter as informações dele mesmo
        Query userQuery = usersDbRef.orderByChild("uid").equalTo(destinatarioUid);
        //obter imagem e nome dele
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //obter dados
                    String nome =""+ ds.child("nome").getValue();
                    imagemDele =""+ ds.child("imagem").getValue();
                    String estadoEscrever =""+ ds.child("escreverPara").getValue();

                    //verficiar se esta a escrever
                    if (estadoEscrever.equals(meuUid)){
                        estadoTv.setText("a escrever...");
                    }
                    else{
                        //definir value de estadoOnline
                        String onlineStatus =""+ ds.child("estadoOnline").getValue();
                        if (onlineStatus.equals("Online")){
                            estadoTv.setText(onlineStatus);
                        }
                        else{
                            //converter timestamp em dado de tempo compreensivel
                            //converter timestamp para dd/mm/yy hh/mm am/pm
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy 'às' HH:mm", cal).toString();
                            estadoTv.setText("última vez online: " + dateTime);
                        }
                    }



                    //definir dados
                    nomeTv.setText(nome);
                    try {
                        //imagem recebida, definir imagem na imageview da barra superior
                        Picasso.get().load(imagemDele).placeholder(R.drawable.img_default_utilizador).into(perfilIv);
                    }
                    catch (Exception e){
                        //ocorreu um erro a obter a imagem, definir imagem default
                        Picasso.get().load(R.drawable.img_default_utilizador).placeholder(R.drawable.img_default_utilizador).into(perfilIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final MediaPlayer sound = MediaPlayer.create(this,R.raw.som1);

        //clique no botao de enviar mensagem
        enviarBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                notify = true;
                //obter texto do edittext
                String mensagem = mensagemEt.getText().toString().trim();
                //verificar se o edittext esta vazio ou nao
                if (TextUtils.isEmpty(mensagem)){
                    //texto esta vazio
                    Toast.makeText(ConversaActivity.this, "Escreve alguma coisa primeiro.", Toast.LENGTH_SHORT).show();
                    sound.start();
                }
                else{
                    //texto nao esta vazio
                    enviarMensagem(mensagem);
                    sound.start();
                }
                //resetar o edittext apos enviar mensagem
                mensagemEt.setText("");
            }
        });


        //clique no botao para enviar imagem
        adicionarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mostrar dialogo
                mostarDialogoEscolhaImagem();
                sound.start();
            }
        });

        //verificar alterações no edittext
        mensagemEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() ==0){
                    verificarEstadoEscrever("ninguem");
                }
                else{
                    verificarEstadoEscrever(destinatarioUid);//uid do destinatario
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lerMensagens();
    }

    private void mostarDialogoEscolhaImagem() {
        //opções (camera galeria) para mostrar no dialogo
        String[] option = {"Câmara", "Galeria"};

        //dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolher uma imagem da");
        //definir opções do dialogo
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
        //intent de ir buscar a imagem à galeria
        Intent  intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //intent de ir buscar a imagem à camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        //verificar se as permissoes de acesso ao armazenamento estao ativas ou nao
        //retorna true se sim
        //retorna false se nao
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //pedir permissoes para o armazenamento
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        //verificar se as permissoes de acesso à camera estao ativas ou nao
        //retorna true se sim
        //retorna false se nao
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //pedir permissoes para a camera
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }


    private void lerMensagens() {
        conversaList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Conversas");
        dbRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversaList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelConversa chat = ds.getValue(ModelConversa.class);
                    if (chat.getDestinatario().equals(meuUid) && chat.getRemetente().equals(destinatarioUid) ||
                            chat.getDestinatario().equals(destinatarioUid) && chat.getRemetente().equals(meuUid)){
                        conversaList.add(chat);
                    }

                    //adaptador
                    adapterConversa = new AdapterConversa(ConversaActivity.this, conversaList, imagemDele);
                    adapterConversa.notifyDataSetChanged();
                    //colocar o adaptador na recyclerview
                    recyclerView.setAdapter(adapterConversa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  String EncryptMessage(String a_myMsg){

        byte[] encryptedMsgByte = new byte[0];
        try {
            encryptedMsgByte = a_myMsg.getBytes("UTF-8");

        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String myEncodeMsg = Base64.getEncoder().encodeToString(encryptedMsgByte);
        return myEncodeMsg;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enviarMensagem(final String mensagem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("remetente", meuUid);
        hashMap.put("destinatario", destinatarioUid);
        hashMap.put("mensagem", EncryptMessage(mensagemEt.getText().toString()));
        hashMap.put("timestamp", timestamp);
        hashMap.put("tipo", "texto");
        databaseReference.child("Conversas").push().setValue(hashMap);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Utilizadores").child(meuUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUtilizador user = dataSnapshot.getValue(ModelUtilizador.class);

              if (notify){
                  enviarNotificacao(destinatarioUid, user.getNome(), mensagem);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //criar o ramo da lista da conversa no firebase database
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                .child(meuUid)
                .child(destinatarioUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(destinatarioUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                .child(destinatarioUid)
                .child(meuUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef2.child("id").setValue(meuUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarMensagemImagem(Uri image_rui) throws IOException {
        notify = true;

        //dialogo de progresso
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A enviar imagem...");
        progressDialog.show();

        final String timeStamp = ""+System.currentTimeMillis();

        String fileNameAndPath = "ImagensConversas/"+"post_"+timeStamp;

        //obter o bitmap do uri da imagem
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_rui);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray(); //converting image to bytes

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //imagem carregada
                        progressDialog.dismiss();
                        //obter o url da imagem e colocar no firebase
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                            //setup dos dados necessarios
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("remetente", meuUid);
                            hashMap.put("destinatario", destinatarioUid);
                            hashMap.put("mensagem", downloadUri);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("tipo", "imagem");
                            //colocar os dados no firebase
                            databaseReference.child("Conversas").push().setValue(hashMap);

                            //enviar notificação
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Utilizadores").child(meuUid);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ModelUtilizador user = dataSnapshot.getValue(ModelUtilizador.class);

                                    if (notify){
                                        enviarNotificacao(destinatarioUid, user.getNome(), "Enviou-te uma imagem...");
                                    }
                                    notify = false;

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //criar o ramo da lista da conversa no firebase database
                            final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                                    .child(meuUid)
                                    .child(destinatarioUid);
                            chatRef1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()){
                                        chatRef1.child("id").setValue(destinatarioUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                                    .child(destinatarioUid)
                                    .child(meuUid);
                            chatRef2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()){
                                        chatRef2.child("id").setValue(meuUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        //criar o ramo da lista da conversa no firebase database
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                .child(meuUid)
                .child(destinatarioUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef1.child("id").setValue(destinatarioUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ListaConversa")
                .child(destinatarioUid)
                .child(meuUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef2.child("id").setValue(meuUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarNotificacao(final String destinatarioUid, final String nome, final String mensagem) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(destinatarioUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Dados dados = new Dados(
                            ""+meuUid,
                            ""+nome+": " + mensagem,
                            "Mensagem Nova",
                            ""+destinatarioUid,
                            "NotificacoesConversa",
                            R.drawable.logotipo_w);

                    Remetente remetente = new Remetente(dados, token.getToken());

                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(remetente));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //resposta ao pedido
                                        Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                //colocar os parametros
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAA7-A7YwQ:APA91bHqhFcXvdjtCzWZmUXXDzEwLP4kku_k1BNT5F2ruLSqVzPivM5zg-5bOw3j4X8mNlIRCAyVGDuvw_YaMF-VJLsqEUvm8qAQMqfclDXTSAiQ2Z7oml_qSVgkBaz-A9iXXQxjD1yp");

                                return headers;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificarEstadoUtilizador(){
        //obter utilizador atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            meuUid = user.getUid(); //com sessao iniciada
        }
        else {
            //o utilizador nao tem sessao iniciada portanto ir para main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void verificarEstadoOnline(String estadoOnline){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Utilizadores").child(meuUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("estadoOnline", estadoOnline);
        //atualizar o valor do estadoOnline do utilizador atual
        dbRef.updateChildren(hashMap);
    }

    private void verificarEstadoEscrever(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Utilizadores").child(meuUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("escreverPara", typing);
        //atualizar o valor de escrita do utilizador atual
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        verificarEstadoUtilizador();
        //definir online
        verificarEstadoOnline("Online");
        super.onStart();
    }

    @Override
    protected void onStop() {
        //obter timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //definir offline com o timestamp da ultima vez online
        verificarEstadoOnline(timestamp);
        verificarEstadoEscrever("ninguem");

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //obter timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //definir offline com o timestamp da ultima vez online
        verificarEstadoOnline(timestamp);
        verificarEstadoEscrever("ninguem");
    }

    @Override
    protected void onResume() {
        //definir como online
        verificarEstadoOnline("Online");

        super.onResume();
    }

    //resultado das permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //este metodo é chamado quando o utilizador clica em permitir ou negar permissoes na caixa de dialogo

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //amabas as permissoes foram garantidas
                        pickFromCamera();
                    }
                    else {
                        //permissoes da camera ou galeria ou ambas foram negadas
                        Toast.makeText(this, "Permissões de Camera e Armazenamento são necessárias...", Toast.LENGTH_SHORT).show();
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
                        //permissoes de armazenamento foram garantidas
                        pickFromGallery();
                    }
                    else {
                        //permissoes da galeria ou ambas foram negadas
                        Toast.makeText(this, "Permissões de Armazenamento são necessárias...", Toast.LENGTH_SHORT).show();
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
        //este metodo é chamado apos retirar uma imagem da galeria ou da camera
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //imagem é captada da galeria, carregar uri
                image_rui = data.getData();

                //usar a uri da imagem para carregar para a firebasestorage
                try {
                    enviarMensagemImagem(image_rui);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //imagem é captada da camera, carregar uir
                try {
                    enviarMensagemImagem(image_rui);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //esconder botao de pesquisar
        menu.findItem(R.id.action_pesquisar).setVisible(false);

        //esconder botao ajuda
        menu.findItem(R.id.action_ajuda).setVisible(false);


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