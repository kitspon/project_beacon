package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanBeacon extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private final static String TAG = Main_Sub_Building_Setting.class.getSimpleName();
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int BTLE_SERVICES = 2;
    Handler delay = new Handler();

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;
    private ListView listView;

    private Button btn_Scan,btn_back;
    private Scanner_BTLE mBTLeScanner;
    String build_id,la,lng,build_name;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_beacon);

        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getSupportActionBar().setTitle("ScanDevice");

        mBTLeScanner = new Scanner_BTLE(this, 1200, -500);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);



        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanBeacon.this, Main_Sub_Building_Setting.class);
                intent.putExtra("build_id", build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btn_Scan = (Button) findViewById(R.id.btn_scan);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        findViewById(R.id.btn_scan).setOnClickListener(this);
        checkLocationPermission();

//        startScan();

    }

    private Runnable eiei = new Runnable() {
        @Override
        public void run() {
            for (int i=0;i<mBTDevicesArrayList.size();i++){
                System.out.println(mBTDevicesArrayList.get(i).getBluetoothDevice()+"    "+mBTDevicesArrayList.get(i).getStatus());
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();

        String address = mBTDevicesArrayList.get(position).getBluetoothDevice().getAddress();
        Utils.toast(context, "List Item clicked"+address);
        stopScan();


        Intent intent = new Intent(this, Select_Add_Type_Beacon.class);
        intent.putExtra("putAddress", address);
        intent.putExtra("build_id",build_id);
        intent.putExtra("status",status);
        intent.putExtra("userid",userid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, BTLE_SERVICES);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_scan:
//                Utils.toast(getApplicationContext(), "Scan Button Pressed");

                if (!mBTLeScanner.isScanning()) {
                    startScan();

                } else {
                    stopScan();
                }

                break;
            default:
                break;
        }
        delay.postDelayed(eiei,3000);

    }

    public void addDevice(BluetoothDevice device, int rssi) {
        String address = device.getAddress();
        Query query = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").orderByChild("address").equalTo(address);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    if (!mBTDevicesHashMap.containsKey(address)) {
                        BTLE_Device btleDevice = new BTLE_Device(device);
                        btleDevice.setRssi(rssi);
                        btleDevice.setName(device.getName());
                        btleDevice.setStatus("used");

                        mBTDevicesHashMap.put(address, btleDevice);
                        mBTDevicesArrayList.add(btleDevice);

                    } else {
                        mBTDevicesHashMap.get(address).setRssi(rssi);
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    if (!mBTDevicesHashMap.containsKey(address)) {
                        BTLE_Device btleDevice = new BTLE_Device(device);
                        btleDevice.setRssi(rssi);
                        btleDevice.setName(device.getName());
                        btleDevice.setStatus("Not in use yet");

                        mBTDevicesHashMap.put(address, btleDevice);
                        mBTDevicesArrayList.add(btleDevice);

                    } else {
                        mBTDevicesHashMap.get(address).setRssi(rssi);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void startScan() {
        btn_Scan.setText("Scanning..");
        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

    public void stopScan() {
        btn_Scan.setText("Scan Again");
        mBTLeScanner.stop();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ScanBeacon.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    public void onBackPressed(){ }
}