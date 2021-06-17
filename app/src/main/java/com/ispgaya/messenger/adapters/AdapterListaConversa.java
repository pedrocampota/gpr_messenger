package com.ispgaya.messenger.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ispgaya.messenger.ConversaActivity;
import com.ispgaya.messenger.R;
import com.ispgaya.messenger.models.ModelConversa;
import com.ispgaya.messenger.models.ModelUtilizador;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class AdapterListaConversa extends RecyclerView.Adapter<AdapterListaConversa.MyHolder>{

    Context context;
    List<ModelUtilizador> utilizadorList; //getting user info
    private HashMap<String, String> ultimaMensagemMap;

    //construtor
    public AdapterListaConversa(Context context, List<ModelUtilizador> utilizadorList) {
        this.context = context;
        this.utilizadorList = utilizadorList;
        ultimaMensagemMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout row_lista_conversa.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_lista_conversa, viewGroup, false);
        return new MyHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //obter dados
        final String destinatarioUid = utilizadorList.get(i).getUid();
        String utilizadorImagem = utilizadorList.get(i).getImagem();
        String nomeUtilizador = utilizadorList.get(i).getNome();
        String ultimaMensagem = ultimaMensagemMap.get(destinatarioUid);

        //definir dados
        myHolder.nomeTv.setText(nomeUtilizador);
        if (ultimaMensagem==null || ultimaMensagem.equals("default")){
            myHolder.ultimaMensagemTv.setVisibility(View.GONE);
        }
        else {
            myHolder.ultimaMensagemTv.setVisibility(View.VISIBLE);


            myHolder.ultimaMensagemTv.setText(ultimaMensagem);
        }


        try {
            Picasso.get().load(utilizadorImagem).placeholder(R.drawable.img_default_utilizador).fit().centerCrop().into(myHolder.perfilIv);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.img_default_utilizador).fit().centerCrop().into(myHolder.perfilIv);
        }
        //definir estado de online na lista de conversa
        if (utilizadorList.get(i).getEstadoOnline().equals("Online")){
            //online
            myHolder.estadoOnlineIv.setImageResource(R.drawable.circle_online);
         }
        else {
            myHolder.estadoOnlineIv.setImageResource(R.drawable.circle_offline);
        }

        //clique listener na lista de conversa para abrir a conversa correspondente
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //começar a conversa activity com aquele utilizador
                Intent intent = new Intent(context, ConversaActivity.class);
                intent.putExtra("destinatarioUid", destinatarioUid);
                context.startActivity(intent);
            }
        });
    }


    public void setLastMessageMap(String utilizadorId, String ultimaMensagem){
        ultimaMensagemMap.put(utilizadorId, ultimaMensagem);
    }

    @Override
    public int getItemCount() {
        return utilizadorList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView perfilIv, estadoOnlineIv;
        TextView nomeTv, ultimaMensagemTv;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            //init views
            perfilIv = itemView.findViewById(R.id.perfilIv);
            estadoOnlineIv = itemView.findViewById(R.id.estadoOnlineIv);
            nomeTv = itemView.findViewById(R.id.nomeTvList);
            ultimaMensagemTv = itemView.findViewById(R.id.ultimaMensagemTv);
        }
    }
}
