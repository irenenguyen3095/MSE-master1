package de.uni_due.paluno.chuj;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.ConnectivityHelper;
import de.uni_due.paluno.chuj.Models.Datum;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.Status;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import de.uni_due.paluno.chuj.Models.MessageModel;
import de.uni_due.paluno.chuj.Models.MessageResponse;
import de.uni_due.paluno.chuj.Models.RemoveFriend;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessaginActivity extends AppCompatActivity implements GetMessageAdapter.OnFriendListener  {


    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Button sendMessageButton;
    private EditText messageBox;
    private String username;
    private String password;
    private String recipent;
    private String mimeType;
    private String message;
    private RecyclerView recyclerView;
    private GetMessageAdapter adapter;
    private GetMessageAdapter adapter2;
    private RecyclerView.LayoutManager layoutManager;
    private GetMessages getMessages;
    private static ObservableArrayList<Datum> messagesList;
    private List<String> msgListString;
    SharedPreferences preferences;
    Activity activity;
    private Location lastLocation;
    private String coordinates;
    private ImageButton mapButton;
    private TextView longtitudeText;
    private TextView latitudeText;
    private String latitude;
    private String longtitude;
    private static List<Datum> backList;
    private FloatingActionButton sendtype;
    private FloatingActionButton floatSendLoc;
    private FloatingActionButton floatSendFile;
    private boolean openfloat = false;
    private Date date;
    private static ObservableMap<String,List<Datum>> backupMap;



    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messaging_activity_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagin);

        preferences = getSharedPreferences("login", MODE_PRIVATE);


        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessaginActivity.this));


        floatSendLoc = (FloatingActionButton) findViewById(R.id.floatLocation);
        floatSendFile = (FloatingActionButton) findViewById(R.id.floatFile);
        sendtype = (FloatingActionButton) findViewById(R.id.fab3);
        sendtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openfloat) {
                    showButtMenu();
                } else {
                    closeButMenu();
                }
            }
        });

        floatSendLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

                    getLastLocation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            getMesseages(new GetMessages(username, password, recipent));;

                        }
                    },1000);

                }
                else
                {
                    Toast.makeText(MessaginActivity.this, "no connection", Toast.LENGTH_SHORT).show();

                }
            }
        });

        floatSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a picture"), 1995);
                setResult(Activity.RESULT_OK);


            }
        });



        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
            //            //Show the connected screen
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            activity = this;


            Intent intent = getIntent();
            recipent = intent.getStringExtra("recipent");


            messagesList = new ObservableArrayList<Datum>();

            msgListString = new ArrayList<>();


            mimeType = "text/plain";


            sendMessageButton = (Button) findViewById(R.id.button_chatbox_send);
            messageBox = (EditText) findViewById(R.id.edittext_chatbox);
            password = preferences.getString("password", "");
            username = preferences.getString("username", "");




            longtitudeText = (TextView)findViewById(R.id.mapTextViewLongtitude);
            latitudeText = (TextView) findViewById(R.id.mapTextViewLatitude);
            mapButton = (ImageButton) findViewById(R.id.mappeButton);
            mapButton = (ImageButton) findViewById(R.id.mappeButton);



            messagesList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<Datum>>() {
                @Override
                public void onChanged(ObservableList<Datum> sender) {


                }

                @Override
                public void onItemRangeChanged(ObservableList<Datum> sender, int positionStart, int itemCount) {


                }

                @Override
                public void onItemRangeInserted(ObservableList<Datum> sender, int positionStart, int itemCount) {

                    if(Status.getMsgStatus()==true&&adapter!=null)
                    {
                        adapter.setList(messagesList);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messagesList.size() - 1);
                    }
                    else
                        if(adapter!=null)
                        {
                            adapter.setList(messagesList);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messagesList.size() - 1);
                        }
                        else
                    {
                        adapter = new GetMessageAdapter(messagesList, getApplicationContext(), username,MessaginActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(messagesList.size() - 1);
                    }

                    ObservableMap<String,List<Datum>> newBackup;
                    newBackup=new ObservableArrayMap<>();
                    newBackup=MenuActivity.getBackupMap();
                    newBackup.put(recipent,messagesList);
                    MenuActivity.setBackupMap(newBackup);





                }

                @Override
                public void onItemRangeMoved(ObservableList<Datum> sender, int fromPosition, int toPosition, int itemCount) {


                }

                @Override
                public void onItemRangeRemoved(ObservableList<Datum> sender, int positionStart, int itemCount) {


                }
            });

            if(Splashscreen.getBackupMap().get(recipent)!=null)
            {
                messagesList.addAll(Splashscreen.getBackupMap().get(recipent));
            }
            else
            {
                Toast.makeText(MessaginActivity.this,"Reloading the messages, please wait",Toast.LENGTH_SHORT).show();

                getMessages = new GetMessages(username, password, recipent);
                getMesseages(getMessages);
            }
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!messageBox.getText().toString().isEmpty()) {
                        message = messageBox.getText().toString();
                        sendMessage(new MessageModel(username, password, recipent, mimeType, message));   // hier wird die Methode registerUser aufgerufen
                        messageBox.setText("");

                    } else {
                        Toast.makeText(MessaginActivity.this, "Write a message", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else{


            Toast.makeText(MessaginActivity.this, "no connection", Toast.LENGTH_SHORT).show();

            try {
                activity = this;


                preferences = getSharedPreferences("login", MODE_PRIVATE);


                Intent intent = getIntent();
                recipent = intent.getStringExtra("recipent");


                mimeType = "text/plain";
                preferences = getSharedPreferences("login", MODE_PRIVATE);

                sendMessageButton = (Button) findViewById(R.id.button_chatbox_send);
                messageBox = (EditText) findViewById(R.id.edittext_chatbox);
                password = preferences.getString("password", "");
                username = preferences.getString("username", "");

                sendMessageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(MessaginActivity.this, "no connection", Toast.LENGTH_SHORT).show();

                    }
                });


                backList=new ArrayList<Datum>();
                backList=new MenuActivity().getBackupMap().get(recipent);

                recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(MessaginActivity.this));
                adapter = new GetMessageAdapter(backList, getApplicationContext(), username,MessaginActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(backList.size() - 1);



            } catch (Exception e) {

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1995 && resultCode == RESULT_OK) {

            Uri file = data.getData(); //The uri with the location of the file

            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(file);
                byte[] inputData = getBytes(inputStream);

                //encode bse64
                String encode = Base64.encodeToString(inputData,12);
                //Toast.makeText(MessaginActivity.this, encode, Toast.LENGTH_SHORT).show();

                sendFile(new MessageModel(username, password, recipent, "file",encode));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        //temp Bitmap with original size
        Bitmap bmp = BitmapFactory.decodeStream(inputStream);

        Bitmap scaledBmp= getNewBitmap(bmp, 800);

        Log.i("Width", String.valueOf(scaledBmp.getWidth()));
        Log.i("Height", String.valueOf(scaledBmp.getHeight()));

        scaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, byteBuffer);

        int bufferSize = 1024*3;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();



    }
    public Bitmap getNewBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void sendFile(final MessageModel fileModel){
        Call<MessageResponse> sendLocationCall = new RestClient().getApiService().sendMessage(fileModel);

        sendLocationCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(MessaginActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                    date = new Date();

                    messagesList.add(new Datum(username,recipent,"fileInsider",fileModel.getData(),date.toString()));



                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

                Toast.makeText(MessaginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showButtMenu(){

        openfloat=true;
        floatSendLoc.animate().translationY(-getResources().getDimension(R.dimen.standard_70));
        floatSendFile.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeButMenu(){

        openfloat=false;
        floatSendLoc.animate().translationY(0);
        floatSendFile.animate().translationY(0);
    }

    @Override
    public void onPause(){

        super.onPause();


        Status.setMsgStatus(false);

    }

    @Override
    public void onStart(){

        super.onStart();
        Status.setMsgStatus(true);

        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
            //Show the connected screen

            if(Status.getNotificationStatus()==true)
            {
                getMesseages( new GetMessages(username, password, recipent));
                Status.setNotificationStatus(false);
            }
        } else{
            Toast.makeText(MessaginActivity.this, "no connection", Toast.LENGTH_SHORT).show();


        }

    }


    @Override
    public void onRestart(){

        super.onRestart();
        Status.setMsgStatus(true);



    }
    @Override
    public void onStop()
    {
        super.onStop();
        Status.setMsgStatus(false);

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        recipent = intent.getStringExtra("recipent");
        getMesseages(new GetMessages(username, password, recipent));
        preferences = getSharedPreferences("login", MODE_PRIVATE);


    }





    public boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           //check Permission
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();

                    coordinates=lastLocation.getLatitude()+"|"+lastLocation.getLongitude();

                    sendLocation(new MessageModel(username, password, recipent, "gps",coordinates));

                } else {
                    Log.v("D", "get location exception", task.getException());
                }
            }
        });
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, 9002);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String itemName;
        itemName = String.valueOf(item.getTitle());
        switch (itemName) {
            case ("logout"):
                preferences.edit().remove("username").commit();
                preferences.edit().remove("password").commit();
                preferences.edit().remove("loginStatus").commit();
                Intent intent = new Intent(MessaginActivity.this, MainActivity.class);

                startActivity(intent);
                activity.finishAffinity();

                return true;
            case ("delete user"): {
                if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

                    Toast.makeText(MessaginActivity.this, "User wird entfernt", Toast.LENGTH_SHORT).show();

                    deleteFriend(new RemoveFriend(username, password, recipent));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            Intent intentDeleteUser = new Intent(MessaginActivity.this, MenuActivity.class);
                            startActivity(intentDeleteUser);

                            activity.finishAffinity();


                        }
                    }, 1500);


                    return true;
                }
                else
                {
                    Toast.makeText(MessaginActivity.this, "No connection", Toast.LENGTH_SHORT).show();


                    return true;
                }

            }
            case("maps"):
            {
                if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

                    getLastLocation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            getMesseages(new GetMessages(username, password, recipent));;

                        }
                    },1000);

                }
                else
                {
                    Toast.makeText(MessaginActivity.this, "no connection", Toast.LENGTH_SHORT).show();

                }
            }
        }

        return true;

    }


    private void sendMessage(final MessageModel messageModel) {
        Call<MessageResponse> sendMessageCall = new RestClient().getApiService().sendMessage(messageModel);

        sendMessageCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(MessaginActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                    date = new Date();
                    messagesList.add(new Datum(username,recipent,"textInsider",messageModel.getData(),date.toString()));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

                Toast.makeText(MessaginActivity.this, "Cannot load the contactlist", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public static void getMesseages(GetMessages getMessages) {

        Call<GetMessagesAntwort> getMessagesCall = new RestClient().getApiService().getMessages(getMessages);

        getMessagesCall.enqueue(new Callback<GetMessagesAntwort>() {
            @Override
            public void onResponse(Call<GetMessagesAntwort> call, Response<GetMessagesAntwort> response) {


                if (response.body() != null) {
                    messagesList.addAll(response.body().getData());




                }
            }


            @Override
            public void onFailure(Call<GetMessagesAntwort> call, Throwable t) {

            }
        });

    }
    private void sendLocation(MessageModel messageModel) {
        Call<MessageResponse> sendLocationCall = new RestClient().getApiService().sendMessage(messageModel);

        sendLocationCall.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(MessaginActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

                Toast.makeText(MessaginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void deleteFriend(RemoveFriend removeFriend) {
        Call<Anmeldungsantwort> deleteFriendCall = new RestClient().getApiService().deleteFriend(removeFriend);

        deleteFriendCall.enqueue(new Callback<Anmeldungsantwort>() {
            @Override
            public void onResponse(Call<Anmeldungsantwort> call, Response<Anmeldungsantwort> response) {
                if (response.body() != null) {
                    Toast.makeText(MessaginActivity.this, "Friend deleted", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<Anmeldungsantwort> call, Throwable t) {

                Toast.makeText(MessaginActivity.this, "Friend cannot be deleted", Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public void onNoteClick(int position) {

        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {


            if (messagesList.get(position).getMimetype().equals("gps")) {

                int index = messagesList.get(position).getData().indexOf("|");
                latitude = messagesList.get(position).getData().substring(0, index - 1);
                longtitude = messagesList.get(position).getData().substring(index + 1);

                Intent intent = new Intent(MessaginActivity.this, MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longtitude", longtitude);
                startActivity(intent);
            }


        }
        else
        {
            Toast.makeText(MessaginActivity.this, "No connection", Toast.LENGTH_SHORT).show();

        }
    }


}