package com.ispgaya.messenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ispgaya.messenger.adapters.AdapterUtilizador;
import com.ispgaya.messenger.models.ModelUtilizador;

import java.util.ArrayList;
import java.util.List;

public class UtilizadoresFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUtilizador adapterUtilizador;
    List<ModelUtilizador> userList;

    //firebase auth
    FirebaseAuth firebaseAuth;

    CardView convidarAmigos;

    public UtilizadoresFragment() {
        // é necessario um construtor vazio
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate o layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_utilizadores, container, false);

        //iniciar recyclerview
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //definir as suas propriedades
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setNestedScrollingEnabled(false);

        //iniciar
        firebaseAuth = FirebaseAuth.getInstance();

        convidarAmigos = view.findViewById(R.id.convidar_Amigos);

        //iniciar lista de utilizadores
        userList = new ArrayList<>();

        //obter os utilizadores
        obterTodosUtilizadores();

        convidarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partilharTextoUrl();
            }
        });

        return view;
    }

    private void partilharTextoUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Adicionar dados ao intent
        share.putExtra(Intent.EXTRA_TEXT, "Olá, como estás? Torna-te num membro da MessageMe, estão todos à tua espera." + System.getProperty("line.separator") +
                "https://www.ispgaya.pt/");
        startActivity(Intent.createChooser(share, "Convidar colegas!"));
    }

    private void obterTodosUtilizadores() {
        //obter utilizador atual
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //obter o caminho da base dados denominado "" Utilizadores "contendo informações dos Utilizadores
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Utilizadores");
        //obter todas as informações do caminho
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUtilizador modelUser = ds.getValue(ModelUtilizador.class);

                    //obter todos os utilziadores execpto o que esta atualmente conectado
                    if (!modelUser.getUid().equals(fUser.getUid())){
                        userList.add(modelUser);
                    }

                    //adaptador
                    adapterUtilizador = new AdapterUtilizador(getActivity(), userList);
                    //definir adaptador para a recycler view
                    recyclerView.setAdapter(adapterUtilizador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pesquisarUtilizadores(final String query) {

        //obter utilizador atual
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // obtém o caminho da base de dados denominado "Utilizadores"contendo informações dos mesmos
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Utilizadores");
        // obtém todos os dados do caminho
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUtilizador modelUser = ds.getValue(ModelUtilizador.class);

                    /* Condições para cumprir a pesquisa
                     * 1 - o utilizador nao é o atual
                     * 2 - o nome do utilizador ou e-mail contém texto inserido na visualização de pesquisa com distinção entre maiúsculas e minúsculas */

                    //obter todos os utilizadores pesquisados, exceto o utilizador atualmente conectado
                    if (!modelUser.getUid().equals(fUser.getUid())){

                        if (modelUser.getNome().toLowerCase().contains(query.toLowerCase())  ||
                                modelUser.getTelemovel().toLowerCase().contains(query.toLowerCase()) ||
                                    modelUser.getNumAluno().toLowerCase().contains(query.toLowerCase()) ||
                                        modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                        }
                    }

                    //adaptador
                    adapterUtilizador = new AdapterUtilizador(getActivity(), userList);
                    //atualizar adaptador
                    adapterUtilizador.notifyDataSetChanged();
                    //definir adaptador na recycler view
                    recyclerView.setAdapter(adapterUtilizador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void verificarEstadoUtilizador(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user !=null){
            //utilizador está conectado
            //mPerfilTv.setText(user.getEmail());
        }
        else {
            //utilizador nao esta conectado, ir para a main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//mostrar o menu de opções
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //carregar menu
        inflater.inflate(R.menu.menu_main, menu);

        //mostrar botao de pesquisasr
        MenuItem item = menu.findItem(R.id.action_pesquisar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //listener da pesquisa
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //chamado quando o utilizador pressiona o botão de pesquisa no teclado
                //se a consulta de pesquisa não estiver vazia, então realizar pesquisa
                if(!TextUtils.isEmpty(s.trim())){
                    //pesquisar contém o texto, pesquise
                    pesquisarUtilizadores(s);
                }
                else {
                    //texto de pesquisa vazio, obter todos os usuários
                    obterTodosUtilizadores();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //chamado quando o utilizador pressiona o botão de pesquisa no teclado
                //se a consulta de pesquisa não estiver vazia, então realizar pesquisa
                if(!TextUtils.isEmpty(s.trim())){
                    //pesquisar contém o texto, pesquise
                    pesquisarUtilizadores(s);
                }
                else {
                    //texto de pesquisa vazio, obter todos os usuários
                    obterTodosUtilizadores();
                }
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //obter id dos itens
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
}