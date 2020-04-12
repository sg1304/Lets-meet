package com.project.letsmeet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMExampleMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            Intent intent = new Intent(this, DisplayNotification.class);
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            if(remoteMessage.getData().size()> 0) {
                SharedPreferences mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                String eventId = remoteMessage.getData().get("eventId");
                String regId = remoteMessage.getData().get("regId");
                editor.putString("eventId",eventId);
                editor.putString("regId",regId);
                editor.apply();
                Log.d(TAG, eventId);
                Log.d(TAG, regId);
//                Bundle bundle = new Bundle();
//                bundle.putString("eventId", eventId);
//                bundle.putString("fcmRegId", regId);
//                intent.putExtras(bundle);
            }
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            NotificationHelper.displayNotification(getApplicationContext(), title, body);
        }
    }
}
