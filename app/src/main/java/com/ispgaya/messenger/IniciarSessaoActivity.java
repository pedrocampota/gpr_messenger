package com.ispgaya.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IniciarSessaoActivity extends AppCompatActivity {

    EditText mEmailET, mPasswordET;
    TextView mNaoTemContaTV, mRecuperarPassowrdTV;
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
        mRecuperarPassowrdTV = findViewById(R.id.recuperarPasswordTV);
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
                finish();
            }
        });

        mRecuperarPassowrdTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarProcessoRecuperarPassword();
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void mostrarProcessoRecuperarPassword() {
        //caixa de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //definir linearlayout
        LinearLayout linearLayout = new LinearLayout(this);
        //views
        EditText mEmailET = new EditText(this);
        mEmailET.setHint("Email");
        mEmailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mEmailET.setMinEms(16);

        linearLayout.addView(mEmailET);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //buttons
        builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = mEmailET.getText().toString().trim();
                iniciarProcessoRecupararPassword(email);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder
                .create().show();
    }

    private void iniciarProcessoRecupararPassword(String email) {
        progressDialog.setMessage("A enviar email de recuperação...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(IniciarSessaoActivity.this, "Email de recuperação enviado!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(IniciarSessaoActivity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //obter e mostrar o erro
                Toast.makeText(IniciarSessaoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciarSessaoUtilizador(String email, String passsword) {
        progressDialog.setMessage("A Iniciar Sessão...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, passsword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(IniciarSessaoActivity.this, DashboardActivity.class));
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