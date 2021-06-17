package com.ispgaya.messenger.notificacoes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ispgaya.messenger.ConversaActivity;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //obter utilizador atual
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID", "None");

        String tipoNotificacao = remoteMessage.getData().get("tipoNotificacao");
        if (tipoNotificacao.equals("NotificacoesConversa")){
            //notificacoes da conversa
            String enviado = remoteMessage.getData().get("enviado");
            String utilizador = remoteMessage.getData().get("utilizador");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fUser != null && enviado.equals(fUser.getUid())){
                if (!savedCurrentUser.equals(utilizador)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        sendOAndAboveNotifications(remoteMessage);
                    }
                    else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        }

        String enviado = remoteMessage.getData().get("enviado");
        String utilizador = remoteMessage.getData().get("utilizador");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null && enviado.equals(fUser.getUid())){
            if (!savedCurrentUser.equals(utilizador)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOAndAboveNotifications(remoteMessage);
                }
                else {
                    sendNormalNotification(remoteMessage);
                }
            }
        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String utilizador = remoteMessage.getData().get("utilizador");
        String icone = remoteMessage.getData().get("icone");
        String titulo = remoteMessage.getData().get("titulo");
        String corpo = remoteMessage.getData().get("corpo");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(utilizador.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ConversaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("destinatarioUid", utilizador);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icone))
                .setContentText(corpo)
                .setContentTitle(titulo)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if (i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }

    private void sendOAndAboveNotifications(RemoteMessage remoteMessage) {
        String utilizador = remoteMessage.getData().get("utilizador");
        String icone = remoteMessage.getData().get("icone");
        String titulo = remoteMessage.getData().get("titulo");
        String corpo = remoteMessage.getData().get("corpo");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(utilizador.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ConversaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("destinatarioUid", utilizador);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAcimaNotificacoes notification1 = new OreoAcimaNotificacoes(this);
        Notification.Builder builder = notification1.getONotifications(titulo, corpo, pIntent, defSoundUri, icone);

        int j = 0;
        if (i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //atualizar token do utilizador
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
}
