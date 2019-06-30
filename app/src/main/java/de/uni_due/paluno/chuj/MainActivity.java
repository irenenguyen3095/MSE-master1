package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.ConnectivityHelper;
import de.uni_due.paluno.chuj.Models.Datum;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import de.uni_due.paluno.chuj.Models.PushToken;
import de.uni_due.paluno.chuj.Models.Status;
import de.uni_due.paluno.chuj.Models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button registerButton;
    private Button loginButton;
    private String userMessage;
    private Activity activity;
    private String probe;
    private String token;
    private TextView signUpText;
    private static List<Datum> msgBackzpList;
    private static Map<String, List<Datum>> backupMap;
    private static List<String> backupList;

    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        activity=this;
        preferences= getSharedPreferences("login",MODE_PRIVATE);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        //registerButton = (Button) findViewById(R.id.registerButton);
        signUpText= findViewById(R.id.signUp_Text);
        loginButton= (Button) findViewById(R.id.loginButton);
        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
            backupMap = new HashMap<String, List<Datum>>();
            backupList = new ArrayList<String>();
            msgBackzpList = new ArrayList<Datum>();
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

     /*   registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    registerUser(new User(username.getText().toString(), password.getText().toString()));   // hier wird die Methode registerUser aufgerufen
                } else {
                    Toast.makeText(MainActivity.this, "Gebe Benutzerdaten ein", Toast.LENGTH_SHORT).show();

                }

            }
        });*/

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        loginUser(new User(username.getText().toString(), password.getText().toString()));   // hier wird die Methode registerUser aufgerufen

                    } else {
                        Toast.makeText(MainActivity.this, "Gebe Benutzerdaten ein", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();

                    token=newToken;
                    Log.e("newToken",newToken);


                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this,"No connection",Toast.LENGTH_SHORT).show();
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
                        getMesseages(new GetMessages(username.getText().toString(), password.getText().toString(), contact));
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

   /* @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void registerUser(User user) {

        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle("Registierung wird duchrgefuhrt");
        waitingDialog.setMessage("Bitte warten");
        waitingDialog.show();


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Registierung wird durchgefuhrt");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });
        Call<Anmeldungsantwort> registerUserCall = new RestClient().getApiService().registerUser(user);

        registerUserCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() !=null)
                {
                    waitingDialog.dismiss();

                    Toast.makeText(MainActivity.this,response.body().getInfo(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

                alertDialog.setMessage("Registierung gescheitert  "+t.getMessage());
                alertDialog.show();
            }
        });

    }*/

    private void loginUser(User user) {

        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle("Anmeldung wird duchrgefuhrt");
        waitingDialog.setMessage("Bitte warten");
        waitingDialog.show();


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Anmeldung wird durchgefuhrt");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });
        Call<Anmeldungsantwort> loginUserCall = new RestClient().getApiService().loginUser(user);

        loginUserCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() !=null)
                {
                    switch(response.body().getMsgType()) {
                        case (1):
                            waitingDialog.dismiss();

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username",username.getText().toString());
                            editor.putString("password",password.getText().toString());
                            editor.putBoolean("loginStatus",true);
                            editor.commit();
                            Toast.makeText(MainActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            userMessage = username.getText().toString();
                            changePushToken(new PushToken(username.getText().toString(),password.getText().toString(),token));



                                    Intent intent = new Intent(MainActivity.this,Splashscreen.class);
                                    intent.putExtra("name", userMessage);

                                    startActivity(intent);
                                    activity.finish();




                            Toast.makeText(MainActivity.this,"Please wait",Toast.LENGTH_SHORT).show();
                            username.setText("");
                            password.setText("");


                            break;

                        case (0):
                            waitingDialog.dismiss();

                            Toast.makeText(MainActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

                alertDialog.setMessage("Anmeldung gescheitert  "+t.getMessage());
                alertDialog.show();
            }
        });

    }
    private void changePushToken(PushToken pushToken) {
        Call<Anmeldungsantwort> changePushToeknCall = new RestClient().getApiService().changePushToken(pushToken);

        changePushToeknCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() != null) {

                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

            }
        });

    }
}