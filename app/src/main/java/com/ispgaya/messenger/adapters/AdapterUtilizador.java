package com.ispgaya.messenger.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ispgaya.messenger.ConversaActivity;
import com.ispgaya.messenger.PerfilUtilizadorActivity;
import com.ispgaya.messenger.R;
import com.ispgaya.messenger.models.ModelUtilizador;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AdapterUtilizador extends RecyclerView.Adapter<AdapterUtilizador.MyHolder>{

    Context context;
    List<ModelUtilizador> utilizadorList;

    FirebaseAuth firebaseAuth;
    String meuUid;

    //constructor
    public AdapterUtilizador(Context context, List<ModelUtilizador> utilizadorList){
        this.context = context;
        this.utilizadorList = utilizadorList;

        firebaseAuth = FirebaseAuth.getInstance();
        meuUid = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_utilizadores.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_utilizadores, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //obter dados
        final String destinatarioUID = utilizadorList.get(i).getUid();
        String utilizadorImagem = utilizadorList.get(i).getImagem();
        String utilizadorNome = utilizadorList.get(i).getNome();
        final String utilizadorTelemovel = utilizadorList.get(i).getTelemovel();
        String utilizadorEstadoPerfil = utilizadorList.get(i).getEstadoPerfil();

        //definir dados
        myHolder.mNomeTv.setText(utilizadorNome);
        myHolder.mEmailTv.setText(utilizadorTelemovel);
        myHolder.mEstadoPerfilTv.setText(utilizadorEstadoPerfil);
        try {
            Picasso.get().load(utilizadorImagem).placeholder(R.drawable.img_default_utilizador).fit().centerCrop().into(myHolder.mImagemIv);
        }
        catch (Exception e){

        }

        //handle clique listener no layout
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //mostrar dialogo
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Perfil", "Conversar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){
                            //clicado na opção perfil
                            //ao clicar envia para o PerfilUtilizador Activity
                            Intent intent = new Intent(context, PerfilUtilizadorActivity.class);
                            intent.putExtra("uid",destinatarioUID);
                            context.startActivity(intent);
                        }
                        if (which==1){
                            //clicado na opção conversa
                            Intent intent = new Intent(context, ConversaActivity.class);
                            intent.putExtra("destinatarioUid", destinatarioUID);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return utilizadorList.size();
    }

    //view holder classes
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mImagemIv;
        TextView mNomeTv, mEmailTv, mEstadoPerfilTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //iniciar views
            mImagemIv = itemView.findViewById(R.id.imagemIv);
            mNomeTv = itemView.findViewById(R.id.nomeTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            mEstadoPerfilTv = itemView.findViewById(R.id.estadoPerfilTv);
        }
    }
}
