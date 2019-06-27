package de.uni_due.paluno.chuj;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.Status;
import de.uni_due.paluno.chuj.Models.User;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    SharedPreferences prefrences;
    private String username;
    private boolean loginStatus;
    private ActivityManager activityManager;
    private boolean msgStatus;
    private String password;
    private boolean menuStatus;



        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {


            Status.setNotificationStatus(true);
                prefrences = getSharedPreferences("login", MODE_PRIVATE);
                username = prefrences.getString("username", "").toString();
                msgStatus = Status.getMsgStatus();
                password=prefrences.getString("password","");
                menuStatus=Status.getMenuStatus();





                loginStatus = prefrences.getBoolean("loginStatus",false);
            String sender = remoteMessage.getData().get("sender");
            String preview = remoteMessage.getData().get("preview");
            String showPreview;

            if(isBase64(preview)){
                showPreview=sender+" sent you a picture!";
            } else

                if(preview.contains("|"))
                {
                    showPreview=sender+"´s location";

                }
                else
                {
                    showPreview=preview;
                }

           // Toast.makeText(this,loginStatus+"",Toast.LENGTH_SHORT).show();
               if (loginStatus == true) {
                   if(msgStatus==true)
                   {
                       if (!sender.equals(username)) {
                       showNotificationInApp(showPreview, sender);
                   }
                   }

                   else
                       if(Status.getMenuStatus()==true)
                       {
                           MessaginActivity.getMesseages( new GetMessages(username, password, sender));
                           showNotificationInMenu(showPreview,sender);

                       }
                       else
                   {
                       showNotification(remoteMessage.getData().get("preview"), sender);
                   }

                }


        }

        private void showNotification(String message, String recipent) {



            Intent i = new Intent(this,MessaginActivity.class);
            i.putExtra("recipent",recipent);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            MessaginActivity.getMesseages( new GetMessages(username, password, recipent));

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(message)
                    .setContentText(recipent+" "+message)
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentIntent(pendingIntent);


            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.notify(0,builder.build());

        }

    public static boolean isBase64(String str) {
        if (TextUtils.isEmpty(str))
            return false;
        try {
            Base64.decode(str, 12);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void showNotificationInApp(String message, String recipent) {



        MessaginActivity.getMesseages( new GetMessages(username, password, recipent));



    }
    private void showNotificationInMenu(String message, String recipent) {


        Intent i = new Intent(this,MessaginActivity.class);
        i.putExtra("recipent",recipent);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);



    }


    @Override
    public void onNewToken(String token) {

        Log.d("D", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

    }



}