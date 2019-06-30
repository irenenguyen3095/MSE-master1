package de.uni_due.paluno.chuj;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private static EditText username;
    private static EditText password1;
    private static EditText password2;
    private static Button signUp_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username= findViewById(R.id.username_input_su);
        password1= findViewById(R.id.pw1_input);
        password2 = findViewById(R.id.pw2_input);
        signUp_button = findViewById(R.id.signup_butt);

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass1 = password1.getText().toString();
                String pass2= password2.getText().toString();

                if (user.isEmpty() || pass1.isEmpty()|| pass2.isEmpty()) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage("Bitte Benutzername und Passwort eingeben!").setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                } else if (!pass1.equals(pass2)){
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage("Passwörter nicht übereinstimmen!").setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    registerUser(new User(user, pass1));

                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void registerUser(User user){
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle("Registrierung wird duchrgefuhrt");
        waitingDialog.setMessage("Bitte warten");
        waitingDialog.show();
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Registrierung wird durchgefuhrt");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
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

                    Toast.makeText(SignUpActivity.this,response.body().getInfo(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

                alertDialog.setMessage("Registrierung gescheitert  "+t.getMessage());
                alertDialog.show();
            }
        });

    }
}
