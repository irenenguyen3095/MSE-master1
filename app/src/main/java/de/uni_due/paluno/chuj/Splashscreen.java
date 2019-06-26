package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableMap;
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


        activity = this;
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        setContentView(R.layout.activity_splashscreen);



        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        loginStatus = preferences.getBoolean("loginStatus", false);
        if (loginStatus == true) {
            prefrences = getSharedPreferences("login", MODE_PRIVATE);
            password = prefrences.getString("password", "").toString();
            username = prefrences.getString("username", "").toString();


            if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
                backupMap = new HashMap<String, List<Datum>>();
                backupList = new ArrayList<String>();
                msgBackzpList = new ArrayList<Datum>();

                getFriends(new User(username, password));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        intent.putExtra("from", "splashscreen");

                        startActivity(intent);
                        activity.finish();


                    }
                }, 4 * 1000);

            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("from", "splashscreen");

                startActivity(intent);
                activity.finish();
            }


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
    public void getFriends(User user) {
        Call<Anmeldungsantwort> getFriendsCall = new RestClient().getApiService().getFriends(user);

        getFriendsCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() != null) {

                    backupList.addAll(response.body().getData());

                    for(String contact:backupList)
                    {
                        getMesseages(new GetMessages(username, password, contact));
                    }
                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {


            }
        });

    }
    public void getMesseages(final GetMessages getMessages) {

        Call<GetMessagesAntwort> getMessagesCall = new RestClient().getApiService().getMessages(getMessages);

        getMessagesCall.enqueue(new Callback<GetMessagesAntwort>() {
            @Override
            public void onResponse(Call<GetMessagesAntwort> call, Response<GetMessagesAntwort> response) {


                if (response.body() != null) {
                    msgBackzpList = response.body().getData();

                    if(!msgBackzpList.isEmpty()) {
                        String sender;
                        String recipent;
                        sender = msgBackzpList.get(0).getSender();
                        recipent = getMessages.getRecipient();
                        if (username.equals(sender)) {
                            backupMap.put(recipent, new ArrayList<Datum>());
                            backupMap.put(recipent, msgBackzpList);

                        } else {
                            backupMap.put(sender, msgBackzpList);
                        }
                    }


                }
            }


            @Override
            public void onFailure(Call<GetMessagesAntwort> call, Throwable t) {
                if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

                }
                else
                {

                }


            }
        });

    }

    public static List<String> getBackupList() {
        return backupList;
    }

    public static void setBackupList(List<String> backupList) {
        Splashscreen.backupList = backupList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static List<Datum> getMsgBackzpList() {
        return msgBackzpList;
    }

    public static void setMsgBackzpList(List<Datum> msgBackzpList) {
        Splashscreen.msgBackzpList = msgBackzpList;
    }

    public static Map<String, List<Datum>> getBackupMap() {
        return backupMap;
    }

    public static void setBackupMap(Map<String, List<Datum>> backupMap) {
        Splashscreen.backupMap = backupMap;
    }
}



