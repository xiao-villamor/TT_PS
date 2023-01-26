package es.udc.psi.tt_ps.ui.viewmodel;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.domain.activity.getActivityUseCase;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.view.MainActivity;

public class FCMservice extends FirebaseMessagingService {
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String update_id = remoteMessage.getData().get("update_id");
        String rating_id = remoteMessage.getData().get("rating_id");


        String CHANNEL_ID = "my_channel_01";
         NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "TT Channel",
                NotificationManager.IMPORTANCE_DEFAULT);

         getSystemService(NotificationManager.class).createNotificationChannel(channel);
        //create intent with her parent activity

        Intent intent = new Intent(this, MainActivity.class);

        if(update_id != null){
            intent.putExtra("update_id", update_id);
        }else if (rating_id != null){
            intent.putExtra("rating_id", rating_id);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


        Notification.Builder notificationBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notificationBuilder.build());
        super.onMessageReceived(remoteMessage);

        //launch function from main activity



    }

    public void displayActivity(String id) throws InterruptedException {
        Log.d("TAG", "Mostrar en detalle" );


    }


}
