package mobile_dev.tennispro;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by eli on 12/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("from", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("data", "Message data payload: " + remoteMessage.getData());
           Map data=remoteMessage.getData();
            String url = (String)data.get("url");
            String headline = (String)data.get("headline");
            String sub = (String)data.get("sub");
            Log.d("url","url key is "+url);
            sendMessage(url,headline,sub);

            //Intent dialogIntent = new Intent(this, MainActivity.class);
          //  dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // startActivity(dialogIntent);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("data", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        super.onMessageReceived(remoteMessage);
    }

    private void sendMessage(String temp,String headline,String sub) {
        Intent intent = new Intent("foreground-notification");
        // add data
        intent.putExtra("message", temp);
        intent.putExtra("headline", headline);
        intent.putExtra("sub", sub);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
