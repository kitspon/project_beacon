package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

public class Google_Navigation extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    Handler delay = new Handler();
    private static final int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private GoogleMap mMap;
    public String build_name, details, img, namerm, floorsrm, build_id;
    public double la, lng;
    TextView name, infor;
    ImageView imgv;
    Button btn_navi,btn_mapback,btn_indoor;
    LatLng sydney, my;
    public MarkerOptions place1, place2, test1, test2;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Polyline currentPolyline;
    public boolean loop = true;
    int a=1;
    Double distance;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google__navigation);

        build_id = getIntent().getStringExtra("build_id");
        build_name = getIntent().getStringExtra("build_name");
        la = Double.parseDouble(getIntent().getStringExtra("latitude"));
        lng = Double.parseDouble(getIntent().getStringExtra("longtitude"));
        details = getIntent().getStringExtra("details");
        img = getIntent().getStringExtra("imgs");

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        place1 = new MarkerOptions().position(new LatLng(13.886545012756393, 100.49101501259199)).title("Location 2");
        test1 = new MarkerOptions().position(new LatLng(19.70214623770932, 99.71888074550756)).title("Location 2");
        test2 = new MarkerOptions().position(new LatLng(19.69475496748316, 99.72180329726862)).title("Location 2");
//        System.out.println("name "+build_name);
//        System.out.println("latitude "+la);
//        System.out.println("longtitude "+lng);

        name = findViewById(R.id.map_name);
        infor = findViewById(R.id.map_details);
        imgv = findViewById(R.id.map_img);
        btn_navi = findViewById(R.id.map_btn);
        btn_mapback = findViewById(R.id.ggmap_back);
        btn_mapback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                loop=false;
                Intent intent = new Intent(Google_Navigation.this, ListBuilding_for_map.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMap(my,sydney);
            }
        });




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Google_Navigation.this);

        name.setText(build_name);
        infor.setText(details);
        Glide.with(Google_Navigation.this).load(img).into(imgv);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(Google_Navigation.this);
        delay.postDelayed(getdirecton,1000);
        delay.postDelayed(qwe,1500);

    }

    private Runnable getdirecton = new Runnable() {
        @Override
        public void run() {
            if (loop==true){
                new FetchURL(Google_Navigation.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                delay.postDelayed(getdirecton,1000);
            }else { }
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        sydney = new LatLng(la, lng);
        place2 = new MarkerOptions().position(new LatLng(la, lng)).title("Location target");




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

    private Runnable qwe = new Runnable() {
        @Override
        public void run() {
            if (loop==true){
            distance = SphericalUtil.computeDistanceBetween(sydney,my);
            distance = distance/1000;
//            Toast.makeText(Google_Navigation.this,"This Distance "+distance,Toast.LENGTH_LONG).show();
            if (distance<0.05){
                loop = false;
                Toast.makeText(Google_Navigation.this,"You arrived at your destination",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(Google_Navigation.this);
                builder.setMessage("Do you want to select room to continue?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Google_Navigation.this,ListRoomForOutdoor.class);
                                        intent.putExtra("build_id",build_id);
                                        intent.putExtra("status",status);
                                        intent.putExtra("userid",userid);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            delay.postDelayed(qwe,1000);
          }else { }
        }
    };



    private Runnable getnewdirection = new Runnable() {
        @Override
        public void run() {
            if (loop==true){
            zoomToUserLocation();
            delay.postDelayed(getnewdirection,1000);
            }else { }
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
                    System.out.println("TNI               TIN          ");
                    System.out.println("ตำแหน่งตัวเอง" + place1);


//              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 15));
//              mMap.addMarker(new MarkerOptions().position(my));
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