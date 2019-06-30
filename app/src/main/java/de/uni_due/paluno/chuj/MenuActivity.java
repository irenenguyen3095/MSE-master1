package de.uni_due.paluno.chuj;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_due.paluno.chuj.Models.Anmeldungsantwort;
import de.uni_due.paluno.chuj.Models.ConnectivityHelper;
import de.uni_due.paluno.chuj.Models.Datum;
import de.uni_due.paluno.chuj.Models.GetMessages;
import de.uni_due.paluno.chuj.Models.GetMessagesAntwort;
import de.uni_due.paluno.chuj.Models.Status;
import de.uni_due.paluno.chuj.Models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
;

public class MenuActivity extends AppCompatActivity implements RecyclerAdapter.OnFriendListener {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button logoutButton;
    private Activity activity;
    private FloatingActionButton addingButton;

    private List<String> list;
    private String username;
    private String password;
    private String probe;
    private String recipent;
    private boolean mLocationPermissionGranted = false;
    private static ObservableArrayList<String> backupList;
    private static List<Datum> msgBackzpList;
    private static ObservableMap<String,List<Datum>> backupMap;
    SharedPreferences prefrences;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        Status.setStatusForMap(true);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
        list = new ArrayList<String>();
        prefrences = getSharedPreferences("login", MODE_PRIVATE);
        password = prefrences.getString("password", "").toString();
        username = prefrences.getString("username", "").toString();

        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

            backupMap = new ObservableArrayMap<>();
            msgBackzpList=new ArrayList<Datum>();
            backupList=new ObservableArrayList<String>();


            backupList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<String>>() {
                @Override
                public void onChanged(ObservableList<String> sender) {
                    if(adapter!=null)
                    {
                        adapter.setList(backupList);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this);
                        recyclerView.setAdapter(adapter);
                    };

                }

                @Override
                public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {

                    if(adapter!=null)
                    {
                        adapter.setList(backupList);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {

                    if(adapter!=null)
                    {
                        adapter.setList(backupList);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this);
                        recyclerView.setAdapter(adapter);
                    }

                }

                @Override
                public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {
                    if(adapter!=null)
                    {
                        adapter.setList(backupList);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {

                    if(adapter!=null)
                    {
                        adapter.setList(backupList);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });

            backupMap.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, List<Datum>>, String, List<Datum>>() {
                @Override
                public void onMapChanged(ObservableMap<String, List<Datum>> sender, String key) {

                    if(Status.getStatusForMap()==true)
                    {


                        adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this,backupMap);
                        recyclerView.setAdapter(adapter);
                    };

                    }





                });

            try{
                backupList.addAll(Splashscreen.getBackupList());
                for(String contact:backupList)
                {

                        backupMap.put(contact,Splashscreen.getBackupMap().get(contact));

                }
            }
         catch(Exception e)
         {
             Toast.makeText(MenuActivity.this,"Reloadig the contacts, please wait", Toast.LENGTH_LONG).show();
             getFriends(new User(username, password));
         }

            addingButton = (FloatingActionButton) findViewById(R.id.addingButton);
            addingButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {

                        Intent intent = new Intent(MenuActivity.this, AddContactActivity.class);

                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(MenuActivity.this,"No connection",Toast.LENGTH_SHORT).show();
                    }

                }

            });


            Intent intent = getIntent();





            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MenuActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();


                    Log.e("newToken",newToken);

                }
            });
        } else {


            Toast.makeText(MenuActivity.this,"No connection",Toast.LENGTH_SHORT).show();

            adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this,backupMap);
            recyclerView.setAdapter(adapter);

        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Status.setMenuStatus(true);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        Status.setMenuStatus(false);
        Status.setStatusForMap(false);
    }
    @Override
    public void onStop()
    {

        super.onStop();
        Status.setStatusForMap(false);
        Status.setMenuStatus(false);
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
    public void getMesseages(GetMessages getMessages) {

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
                        recipent = msgBackzpList.get(0).getRecipient();
                        if (username.equals(sender)) {
                            backupMap.put(recipent, new ArrayList<Datum>());
                            backupMap.put(recipent, msgBackzpList);

                        } else {
                            backupMap.put(sender, msgBackzpList);
                        }
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this,backupMap);
                            recyclerView.setAdapter(adapter);


                        }
                    },2*1000);

                }
            }


            @Override
            public void onFailure(Call<GetMessagesAntwort> call, Throwable t) {
                if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
                    Toast.makeText(MenuActivity.this, "Cannot load the contactlist", Toast.LENGTH_SHORT).show();
                }
                else
                {

                }


            }
        });

    }
    public static  ObservableMap<String,List<Datum>>getBackupMap()
    {
        return backupMap;
    }
    @Override
    public void onRestart(){

        super.onRestart();
        Status.setMenuStatus(true);
        Status.setStatusForMap(true);


        if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
            adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this,backupMap);
            recyclerView.setAdapter(adapter);
        } else {


            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(MenuActivity.this));
            adapter = new RecyclerAdapter(backupList, getApplicationContext(), MenuActivity.this,backupMap);
            recyclerView.setAdapter(adapter);

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String itemName;
        itemName = String.valueOf(item.getTitle());

        switch (itemName) {
            case ("logout"):
                prefrences.edit().remove("username").commit();
                prefrences.edit().remove("password").commit();
                prefrences.edit().remove("loginStatus").commit();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                activity.finish();
                return true;

            case ("change password"):
                Intent intentPassword = new Intent(MenuActivity.this,change_password_activity.class);
                startActivity(intentPassword);


                return true;
        }
        return true;
    }


    @Override
    public void onNoteClick(int position) {


        recipent = backupList.get(position);


        Intent intent = new Intent(this, MessaginActivity.class);
        intent.putExtra("recipent", recipent);
        startActivity(intent);

    }


    public static void setBackupMap(ObservableMap<String, List<Datum>> backupMap) {
        MenuActivity.backupMap = backupMap;
    }

    public static ObservableArrayList<String> getBackupList() {
        return backupList;
    }

    public static void setBackupList(ObservableArrayList<String> backupList) {
        MenuActivity.backupList = backupList;
    }
}
