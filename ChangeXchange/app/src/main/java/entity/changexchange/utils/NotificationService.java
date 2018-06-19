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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import entity.changexchange.FirebaseLogin;
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

    public void letUserKnowOfferIsInteresting(final String nickname, final String title, final String body){
        Log.d("test", "letting user know");
        new AsyncTask<String, Void, Void>() {
            protected Void doInBackground(String... strings) {
                Connection c;
                Statement stmt;
                String token = "";
                try {
                    Class.forName("org.postgresql.Driver");
                    c = DriverManager
                            .getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1727132_u?&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
                                    "g1727132_u", "4ihe2mwvgy");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    //TODO: many users could have the same nickname
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE nickname='" + nickname + "';");
                    while (rs.next() && token == "") {
                        token = rs.getString("token");
                    }
                    rs.close();
                    stmt.close();
                    c.commit();
                    c.close();
                    Log.d("test", "before sending notif");
                    sendNotification(token, title, body);
                } catch (Exception e) {
                    Log.d("test", e.getMessage());
                }
                return null;
            }

        }.execute();
    }

    //TODO:
    private void sendNotification(final String regToken, final String title, final String body) {
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
                    dataJson.put("body",body);
                    dataJson.put("title",title);
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
                    Log.d("test",e+"");
                }
                return null;
            }
        }.execute();
    }

    private void displayNotification(RemoteMessage remoteMessage) {
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
        notificationManager.notify(0, notif);
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
