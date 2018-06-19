package entity.changexchange.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.LoginFilter;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import entity.changexchange.MainActivity;
import entity.changexchange.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationService extends FirebaseMessagingService {
    public NotificationService() {

    }

    //TODO:
    public void sendNotification(final String regToken) {
        Log.d("test", "sending notif");
        new AsyncTask<Void,Void,Void>(){
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body","Hi this is sent from device to device");
                    dataJson.put("title","dummy title");
                    json.put("notification",dataJson);
                    json.put("to",regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key=AAAAQCmdBBo:APA91bGyBJkfsOA4lWZX8_IeA2X5sCNsPMen5qOUtNAcsmisn3weve9yTKMGQ7L947gsYhT5rDnpH5Yuca3IwqYrA5WApvHrW-CKbhfEYH9MI6dPVqYxpnbqHp7aZSE9n45IpueKP7j4") //legacy server key
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.d("test", "notif sent" + finalResponse);
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();
    }

    private void displayNotification(RemoteMessage remoteMessage) {
        Log.d("test", "displaying notification");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "main_channel")
                //TODO: what icons should we put?
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_black_24dp))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = notificationBuilder.build();
        if((notif == null)) {
            Log.d("test", "notif is null" );
        } else {
            Log.d("test", "notif is NOT null" );
        }
        notificationManager.notify(0, notif);
        Log.d("test", "end of display of notification");
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("test", "From: " + remoteMessage.getFrom());
        displayNotification(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("test", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("test", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void scheduleJob(){
        //TODO: implement
    }

    private void handleNow(){
        //TODO: implement
    }

}
