package com.gpr.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class IniciarSessaoActivity extends AppCompatActivity {

    EditText mEmailET, mPasswordET;
    TextView mNaoTemContaTV;
    Button mFinalizarIniciarSessao;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sessao);

        //Actionbar com titulo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Iniciar Sessão");
        //ativar botão de voltar atrás
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mEmailET = findViewById(R.id.emailET);
        mPasswordET = findViewById(R.id.passwordET);
        mNaoTemContaTV = findViewById(R.id.nao_tem_conta_TV);
        mFinalizarIniciarSessao = findViewById(R.id.botao_finalizar_inicio_sessao);


        //clique do botao de iniciar sessao
        mFinalizarIniciarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input de dados
                String email = mEmailET.getText().toString().trim();
                String passsword = mPasswordET.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //se for invalido mostrar erro
                    mEmailET.setError("Email Inválido");
                    mEmailET.setFocusable(true);
                }
                else {
                    //se for válido
                    iniciarSessaoUtilizador(email, passsword);
                }
            }
        });
        //clique do text view do nao tem conta
        mNaoTemContaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IniciarSessaoActivity.this, RegistarActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A Iniciar Sessão...");
    }

    private void iniciarSessaoUtilizador(String email, String passsword) {

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, passsword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(IniciarSessaoActivity.this, PerfilActivity.class));
                            finish();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(IniciarSessaoActivity.this, "O Inicio de Sessão falhou.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(IniciarSessaoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //voltar atras
        return super.onSupportNavigateUp();
    }
}