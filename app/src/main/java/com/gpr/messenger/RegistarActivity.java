package com.gpr.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText mEmailET, mPasswordET;
    Button mFinalizarRegisto;

    //barra de progresso enquanto ocorre o registo
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        //Actionbar com titulo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Criar conta");
        //ativar botão de voltar atrás
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //definir variaveis

        mEmailET = findViewById(R.id.emailET);
        mPasswordET = findViewById(R.id.passwordET);
        mFinalizarRegisto = findViewById(R.id.botao_finalizar_registo);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A registar-te...");

        //clique do botao finalizar registo
        mFinalizarRegisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email e password
                String email = mEmailET.getText().toString().trim();
                String password = mPasswordET.getText().toString().trim();
                //validação
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //surgir erro e focar no email
                    mEmailET.setError("Email inválido!");
                    mEmailET.setFocusable(true);
                }
                else if (password.length()<6){
                    //surgir eurro e focar na password
                    mPasswordET.setError("A password tem que ter pelo menos 6 caractéres.");
                    mPasswordET.setFocusable(true);
                }
                else {
                    registarUtilizador(email, password); //registar o utilizador
                }
            }
        });
    }

    private void registarUtilizador(String email, String password) {
        //o email e password são válidos, mostrar progress dialog e iniciar sessao
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegistarActivity.this, "Registado com sucesso!\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistarActivity.this, PerfilActivity.class));
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(RegistarActivity.this, "O registo falhou.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistarActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //voltar atras
        return super.onSupportNavigateUp();
    }
}