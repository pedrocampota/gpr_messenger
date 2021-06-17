package com.ispgaya.messenger;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ispgaya.messenger.adapters.AdapterListaConversa;
import com.ispgaya.messenger.models.ModelConversa;
import com.ispgaya.messenger.models.ModelListConversa;
import com.ispgaya.messenger.models.ModelUtilizador;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConversasListaFragment extends Fragment {

    //firebase auth
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelListConversa> conversaList;
    List<ModelUtilizador> utilizadorList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterListaConversa adapterListaConversa;

    public ConversasListaFragment() {
        // Construtor vazio e obrigatório
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_conversas_lista, container, false);

        //iniciar
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        conversaList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ListaConversa").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversaList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelListConversa chatList = ds.getValue(ModelListConversa.class);
                    conversaList.add(chatList);
                }
                carregarConversas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void carregarConversas() {
        utilizadorList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Utilizadores");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                utilizadorList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUtilizador user = ds.getValue(ModelUtilizador.class);
                    for (ModelListConversa chatList: conversaList){
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())){
                            utilizadorList.add(user);
                            break;
                        }
                    }
                    //adaptador
                    adapterListaConversa = new AdapterListaConversa(getContext(), utilizadorList);
                    //definir adaptador
                    recyclerView.setAdapter(adapterListaConversa);
                    //definir ultima mensagem
                    for (int i=0; i<utilizadorList.size(); i++){
                        ultimaMensagem(utilizadorList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ultimaMensagem(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversas");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelConversa chat = ds.getValue(ModelConversa.class);
                    if (chat==null){
                        continue;
                    }
                    String sender = chat.getRemetente();
                    String receiver = chat.getDestinatario();
                    if (sender == null || receiver ==null){
                        continue;
                    }
                    if (chat.getDestinatario().equals(currentUser.getUid())
                            && chat.getRemetente().equals(userId) ||
                            chat.getDestinatario().equals(userId) &&
                                    chat.getRemetente().equals(currentUser.getUid())){
                        //exibir "Imagem enviada" na mensagem
                        if (chat.getTipo().equals("imagem")){
                            theLastMessage = "Enviou uma imagem";
                        }
                        else if (chat.getTipo().equals("texto")){

                            byte[] decodedByte = Base64.getDecoder().decode(chat.getMensagem());
                            String decryptedMsg = "";
                            try {
                                decryptedMsg = new String(decodedByte, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            theLastMessage = decryptedMsg;

                        }
                    }
                }
                adapterListaConversa.setLastMessageMap(userId, theLastMessage);
                adapterListaConversa.notifyDataSetChanged();
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
            //o utilizador está com login efetuado
        }
        else {
            //o utilizador nao tem sessoa iniciada ir para a main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//mostrar menu de opções no fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //esconder icone de pesquisa
        menu.findItem(R.id.action_pesquisar).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ajuda){
            startActivity(new Intent(getActivity(), AjudaActivity.class));
        }
        else if (id == R.id.action_terminar_sessao){
            firebaseAuth.signOut();
            verificarEstadoUtilizador();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        verificarEstadoUtilizador();
        super.onStop();
    }

    @Override
    public void onResume() {
        verificarEstadoUtilizador();
        super.onResume();
    }

    @Override
    public void onPause() {
        verificarEstadoUtilizador();
        super.onPause();
    }
}