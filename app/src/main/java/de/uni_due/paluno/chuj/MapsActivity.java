package de.uni_due.paluno.chuj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude;
    private String longtitude;
    private static final int MY_REQUEST_INT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        latitude= intent.getStringExtra("latitude");
        longtitude= intent.getStringExtra("longtitude");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapp);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT  );
            }
            return;
        }else{
            mMap.setMyLocationEnabled(true);

        }

        // Add a marker
        Intent intent = getIntent();
        latitude= intent.getStringExtra("latitude");
        longtitude= intent.getStringExtra("longtitude");

        LatLng here = new LatLng(Double.valueOf(latitude),Double.valueOf(longtitude));
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(here));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13.5f));

    }
}
