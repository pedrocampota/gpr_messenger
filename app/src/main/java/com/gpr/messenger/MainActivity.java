package com.gpr.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mBotaoRegistar, mBotaoIniciarSessao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //definir variáveis
        mBotaoIniciarSessao = findViewById(R.id.botao_iniciar_sessao);

        mBotaoRegistar = findViewById(R.id.botao_registar);

        //clique do botao registar
        mBotaoRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciar atividade de registo
                startActivity(new Intent(MainActivity.this, RegistarActivity.class));
            }
        });

        //clique do botao iniciar sessão
        mBotaoIniciarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciar atividade de inicio de sessao
                startActivity(new Intent(MainActivity.this, IniciarSessaoActivity.class));
            }
        });
    }
}