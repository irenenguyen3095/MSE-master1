package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.ConnectivityHelper;
import de.uni_due.paluno.chuj.Models.Datum;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import de.uni_due.paluno.chuj.Models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splashscreen extends AppCompatActivity {


    private boolean loginStatus;
    private Activity activity;
    private static List<String> backupList;
    private String username;
    private String password;
    private static List<Datum> msgBackzpList;
    private static Map<String, List<Datum>> backupMap;
    SharedPreferences prefrences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backupList = new ArrayList<String>();

        activity = this;
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        setContentView(R.layout.activity_splashscreen);

        backupMap = new HashMap<String, List<Datum>>();
        msgBackzpList = new ArrayList<Datum>();
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        loginStatus = preferences.getBoolean("loginStatus", false);
        if (loginStatus == true) {
            prefrences = getSharedPreferences("login", MODE_PRIVATE);
            password = prefrences.getString("password", "").toString();
            username = prefrences.getString("username", "").toString();

            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.putExtra("from", "splashscreen");

            startActivity(intent);
            activity.finish();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    activity.finish();


                }
            }, 4 * 1000);
        }

    }
}



