package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.AddFriend;
import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.ConnectivityHelper;
import de.uni_due.paluno.chuj.Models.Datum;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddContactActivity extends AppCompatActivity {
private Button addContactButton;
private TextView addContactText;
private SharedPreferences prefrences;
private Activity activity;
private String password;
private String username;
private static List<Datum> msgBackzpList;
private static List<String> backupList;
private static ObservableMap<String,List<Datum>> backupMap;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);


        prefrences =getSharedPreferences("login",MODE_PRIVATE);
         password = prefrences.getString("password","").toString();
         username = prefrences.getString("username","").toString();

        activity = this;


        addContactButton =(Button) findViewById(R.id.addContactButton);
        addContactText= (TextView) findViewById(R.id.addContactText);



        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addContactText.getText().toString().isEmpty() ) {
                    addFriend(new AddFriend(username,password,addContactText.getText().toString()));// hier wird die Methode registerUser aufgerufen



                            Intent intent = new Intent(AddContactActivity.this,MenuActivity.class);

                            startActivity(intent);
                            activity.finishAffinity();




                } else {
                    Toast.makeText(AddContactActivity.this, "Gebe Benutzerdaten ein", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addFriend(AddFriend addFriend) {

        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle("Adding");
        waitingDialog.setMessage("Please wait");
        waitingDialog.show();


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("adding");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });
        Call<Anmeldungsantwort> addUserCall = new RestClient().getApiService().addFriend(addFriend);

        addUserCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() !=null)
                {
                    switch(response.body().getMsgType()) {
                        case (1):
                            waitingDialog.dismiss();

                            Toast.makeText(AddContactActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                            ObservableArrayList<String> backupList = new ObservableArrayList<>();

                           backupList=MenuActivity.getBackupList();
                           backupList.add(addContactText.getText().toString());
                           Splashscreen.getBackupMap().put(addContactText.getText().toString(),null);



                            break;

                        case (0):
                            waitingDialog.dismiss();

                            Toast.makeText(AddContactActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        prefrences.edit().remove("username").commit();
        prefrences.edit().remove("password").commit();
        prefrences.edit().remove("loginStatus").commit();
        Intent intent = new Intent(AddContactActivity.this,MainActivity.class);

        activity.finishAffinity();
        startActivity(intent);
        return true;
    }

}
