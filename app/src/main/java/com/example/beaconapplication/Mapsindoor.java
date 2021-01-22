package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

public class Mapsindoor extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    public String build_id,build_name,name,floor;
    public double lamark,lngmak;
    Handler delay = new Handler();
    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    Button btnmaps_mapback,btnmaps_indoor;
    LatLng sydney, my;
    public MarkerOptions place1, place2;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Polyline currentPolyline;
    public boolean loop = true;
    Double distance;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsindoor);

        build_id = getIntent().getStringExtra("build_id");
        name = getIntent().getStringExtra("name");
        floor = getIntent().getStringExtra("floor");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        System.out.println("TTTTTESSSTTTTTT"+build_id);


        place1 = new MarkerOptions().position(new LatLng(13.886545012756393, 100.49101501259199)).title("Location 2");
        btnmaps_mapback = findViewById(R.id.mapss_back);
        btnmaps_indoor = findViewById(R.id.mapss_indoor);
        btnmaps_mapback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loop=false;
                Intent intent = new Intent(Mapsindoor.this, ListAllRoom.class);
                intent.putExtra("build_id", build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btnmaps_indoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loop=false;
                Intent intent = new Intent(Mapsindoor.this, NavigationPage.class);
                intent.putExtra("floor", floor);
                intent.putExtra("build_id", build_id);
                intent.putExtra("name", name);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        Query databuild = FirebaseDatabase.getInstance().getReference("Building").orderByChild("building_id").equalTo(build_id);
        databuild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    build_name = item.child("building_name").getValue().toString();
                    lamark = Double.parseDouble(item.child("latitude").getValue().toString());
                    lngmak = Double.parseDouble(item.child("longtitude").getValue().toString());
                    System.out.println(build_name);
                    System.out.println(lamark);
                    System.out.println(lngmak);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        delay.postDelayed(start,1000);
    }
    private Runnable start = new Runnable() {
        @Override
        public void run() {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Mapsindoor.this);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(Mapsindoor.this);
            delay.postDelayed(getdirecton,1000);
            delay.postDelayed(qwe,1500);
        }
    };

    private Runnable qwe = new Runnable() {
        @Override
        public void run() {
            if (loop==true){
                distance = SphericalUtil.computeDistanceBetween(sydney,my);
                distance = distance/1000;
//            Toast.makeText(Google_Navigation.this,"This Distance "+distance,Toast.LENGTH_LONG).show();
                if (distance<0.05){
                    loop = false;
                    Toast.makeText(Mapsindoor.this,"You arrived at your destination",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Mapsindoor.this,NavigationPage.class);
                    intent.putExtra("floor", floor);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("name", name);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                delay.postDelayed(qwe,1000);
            }else { }
        }
    };

    private Runnable getdirecton = new Runnable() {
        @Override
        public void run() {
            if (loop==true){
                new FetchURL(Mapsindoor.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                delay.postDelayed(getdirecton,1000);
            }else { }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        sydney = new LatLng(lamark, lngmak);
        place2 = new MarkerOptions().position(new LatLng(lamark, lngmak)).title("Location target");

//        mMap.addMarker(new MarkerOptions().position(sydney).title(build_name));

        mMap.addMarker(place2);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            zoomToUserLocation();
            delay.postDelayed(getnewdirection,1000);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }
    }

    private Runnable getnewdirection = new Runnable() {
        @Override
        public void run() {
            zoomToUserLocation();
            delay.postDelayed(getnewdirection,1000);
        }
    };

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private void openGoogleMap(LatLng src, LatLng dest) {
        String url = "http://maps.google.com/maps?saddr=" + src.latitude + "," + src.longitude + "&daddr=" + dest.latitude + "," + dest.longitude + "&mode=driving";
        Uri gmmIntentUri = Uri.parse(url);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                my = new LatLng(location.getLatitude(), location.getLongitude());
                place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Location me");
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 15));
//                mMap.addMarker(new MarkerOptions().position(my));

            }
        });
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    public void onBackPressed(){ }
}