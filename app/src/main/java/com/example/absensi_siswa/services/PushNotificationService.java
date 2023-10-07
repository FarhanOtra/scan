package com.example.absensi_siswa.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.absensi_siswa.MainActivity;
import com.example.absensi_siswa.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class PushNotificationService extends FirebaseMessagingService {
    private static  final String CHANNEL_ID = "com.example.absensoi_siswa.CH01";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        showNotification(title,message);
    }

    private void showNotification(String title, String message) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //BuatChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Channel Ohayou",
                            NotificationManager.IMPORTANCE_DEFAULT
                    );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Buat Pending Intent
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pendingMainIntenet = PendingIntent.getActivity(
                getApplicationContext(),
                12345,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Buat Notifikasi
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_email)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(pendingMainIntenet)
                        .addAction(R.drawable.ic_show, "Lihat", pendingMainIntenet);
        ;

        Notification notification = builder.build();

        //Tamoilkan notifikasi
        Random random = new Random(1000);

        notificationManager.notify(random.nextInt(), notification);
    }

}