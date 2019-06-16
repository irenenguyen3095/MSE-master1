package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.Password;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class change_password_activity extends AppCompatActivity {

    private String username;
    SharedPreferences prefrences;
    private Button changeButton;
    private String sharedPassword;
    private TextView oldPassword;
    private TextView newPassword;
    private Activity activity;
    private String oldpassword;
    private String newpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_layout);

        activity=this;

        prefrences = getSharedPreferences("login", MODE_PRIVATE);
        username = prefrences.getString("username", "").toString();
        sharedPassword = prefrences.getString("password", "").toString();
        oldPassword= (TextView) findViewById(R.id.old_password);
        newPassword = (TextView) findViewById(R.id.new_password);
        changeButton = (Button) findViewById(R.id.change_button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!oldPassword.getText().toString().isEmpty() && !newPassword.getText().toString().isEmpty()) {

                    oldpassword =oldPassword.getText().toString();
                    newpassword = newPassword.getText().toString();
                    Toast.makeText(change_password_activity.this, "Passwort wird aktualisiert", Toast.LENGTH_SHORT).show();

                    if(oldpassword.equals(sharedPassword))
                    {
                        changePassword(new Password(username,sharedPassword,newpassword));
                        SharedPreferences.Editor editor = prefrences.edit();
                        editor.remove("password");
                        editor.putString("password",newpassword).commit();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent= new Intent(change_password_activity.this,MenuActivity.class);

                                startActivity(intent);

                                activity.finishAffinity();




                            }
                        },3*1000);






                    }
                    else
                    {
                        Toast.makeText(change_password_activity.this, "Current password invlid", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(change_password_activity.this, "Gebe Benutzerdaten ein", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void changePassword(Password password){


       ;
        Call<Anmeldungsantwort> changePasswordCall = new RestClient().getApiService().changePassword(password);

        changePasswordCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() != null) {

                    Toast.makeText(change_password_activity.this,response.body().getInfo(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

                Toast.makeText(change_password_activity.this, "Cannot load the contactlist", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
