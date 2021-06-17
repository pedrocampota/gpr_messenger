package com.ispgaya.messenger.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ispgaya.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ispgaya.messenger.VerImagemEnviadaActivity;
import com.ispgaya.messenger.models.ModelConversa;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterConversa extends RecyclerView.Adapter<AdapterConversa.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelConversa> conversaList;
    String imageUrl;

    FirebaseUser fUser;

    public AdapterConversa(Context context, List<ModelConversa> conversaList, String imageUrl) {
        this.context = context;
        this.conversaList = conversaList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layouts: row_conversa_esquerda.xml para o destinatario e row_conversa_direita para o remetente
        if (i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_conversa_direita, viewGroup, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_conversa_esquerda, viewGroup, false);
            return new MyHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        //obter dados
        String mensagem = conversaList.get(i).getMensagem();
        String timeStamp = conversaList.get(i).getTimestamp();
        String tipo = conversaList.get(i).getTipo();


        //converter timestamp para  dd/mm/yy hh/mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy 'às' HH:mm", cal).toString();

        if (tipo.equals("texto")){
            //mensagem de texto
            myHolder.mensagemTv.setVisibility(View.VISIBLE);
            myHolder.mensagemIv.setVisibility(View.GONE);

            //myHolder.mensagemTv.setText(mensagem);

            //definir dados
            //encriptação das mensagens #mensagem
            byte[] decodedByte = Base64.getDecoder().decode(mensagem);
            String decryptedMsg="";
            try {
                decryptedMsg = new String(decodedByte, "UTF-8");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            myHolder.mensagemTv.setText(decryptedMsg);
            //termina aqui
        }
        else {
            //mensagem de texto
            myHolder.mensagemTv.setVisibility(View.GONE);
            myHolder.mensagemIv.setVisibility(View.VISIBLE);

            Picasso.get().load(mensagem).fit().centerCrop().placeholder(R.drawable.ic_imagem_black).into(myHolder.mensagemIv);
        }

        myHolder.tempoTv.setText(dateTime);
        try{
            Picasso.get().load(imageUrl).into(myHolder.perfilIv);
        }
        catch (Exception e){

        }


        myHolder.mensagemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myHolder.itemView.getContext(), VerImagemEnviadaActivity.class);
                intent.putExtra("url", conversaList.get(i).getMensagem());
                myHolder.itemView.getContext().startActivity(intent);
            }
        });

        //clicar continuamente em cima do timestamp para mostrar dialogo de apagar mensagem
        myHolder.mensagemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //mostar o dialogo de confirmação
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Apagar");
                builder.setMessage("Tens a certeza que queres apagar esta mensagem?");
                //botao de apagar
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        apagarMensagem(i);
                    }
                });
                //botao de cancelar
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });
                //criar e mostrar dialogo
                builder.create().show();

                return false;
            }
        });
    }

    private void apagarMensagem(int position){
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //obter timestamp da mensagem apagada
        String msgTimeStamp = conversaList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Conversas");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("remetente").getValue().equals(myUID)){
                        ds.getRef().removeValue();

                        Toast.makeText(context, "Mensagem apagada.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Só podes apagar as tuas mensagens.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return conversaList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (conversaList.get(position).getRemetente().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    //view holder classes
    class MyHolder extends RecyclerView.ViewHolder{

        //views
        ImageView perfilIv, mensagemIv;
        TextView mensagemTv, tempoTv;
        LinearLayout mensagemLayout; //para o click listner para mostar o dialogo

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //inicializar views
            perfilIv = itemView.findViewById(R.id.perfilIv);
            mensagemIv = itemView.findViewById(R.id.mensagemIv);
            mensagemTv = itemView.findViewById(R.id.mensagemTv);
            tempoTv = itemView.findViewById(R.id.tempoTv);
            mensagemLayout = itemView.findViewById(R.id.mensagemLayout);
        }
    }
}
