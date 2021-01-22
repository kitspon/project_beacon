package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Checklocation extends AppCompatActivity {
    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;
    private ScanCheckBeacon mBTLeScanner;
    private ListView listView;
    private Handler delay = new Handler();
    public String build_id,choices,name,floor,point;
    TextView text;
    Button btnscan;
    ImageView imgscan;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklocation);

        build_id = getIntent().getStringExtra("build_id");
        name = getIntent().getStringExtra("name");
        floor = getIntent().getStringExtra("floor");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        System.out.println("##USERID## = "+userid+"  **STATUS = "+status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mBTLeScanner = new ScanCheckBeacon(this, 1500, -500);
        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();
        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);
        listView = new ListView(this);
        listView.setAdapter(adapter);

        text = findViewById(R.id.text_wait);
        checkLocationPermission();
        QueryRMfirst();
        QueryBCfirst();
        if (!mBTLeScanner.isScanning()) {
            System.out.println("!isScaning");
            startScan();
            if (mBTLeScanner.isScanning()) {
                delay.postDelayed(step1, 2000);
                delay.postDelayed(textpoint, 1000);
            }
        }
        else {
            stopScan();
        }
        btnscan = findViewById(R.id.check_scan);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBTLeScanner.isScanning()) {
                    System.out.println("!isScaning");
                    startScan();
                    if (mBTLeScanner.isScanning()) {
                        delay.postDelayed(step1, 2000);
                        delay.postDelayed(textpoint, 1000);
                    }
                }
                else {
                    System.out.println("else");
                    stopScan();
                }
            }
        });
    }

    private Runnable textpoint = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
                text.setText("Checklocation.");
                delay.postDelayed(textpoint2, 1000);
        }
    };
    private Runnable textpoint2 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            text.setText("Checklocation..");
            delay.postDelayed(textpoint3, 1000);
        }
    };
    private Runnable textpoint3 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            text.setText("Checklocation...");
        }
    };
    private Runnable step1 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Checkmylocation();
            delay.postDelayed(twochoices, 1000);
        }
    };

    private Runnable twochoices = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            if (choices.equals("Near")){

                System.out.println(choices);
                Intent intent = new Intent(Checklocation.this, NavigationPage.class);
                intent.putExtra("floor", floor);
                intent.putExtra("build_id", build_id);
                intent.putExtra("name", name);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{

                System.out.println(choices);
                Intent intent = new Intent(Checklocation.this, Mapsindoor.class);
                intent.putExtra("floor", floor);
                intent.putExtra("build_id", build_id);
                intent.putExtra("name", name);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Checkmylocation() {
        Collections.sort(mBTDevicesArrayList, Comparator.comparing(BTLE_Device::getRssi));
        for (int i = 0; i < mBTDevicesArrayList.size(); i++) {
            System.out.println(mBTDevicesArrayList.get(i).getBluetoothDevice() + "    " + mBTDevicesArrayList.get(i).getRssi() + "    " + mBTDevicesArrayList.get(i).getName());
        }

        System.out.println("--------------------ค้นหา----------------------");
        if (mBTDevicesArrayList.size()==0){
            choices = "NotNear";
        }else {
            for (int i = 0; i < mBTDevicesArrayList.size(); i++) {
                String checkadress = mBTDevicesArrayList.get(i).getBluetoothDevice().toString();
                Query checkroom = FirebaseDatabase.getInstance().getReference().child(build_id).child("Beacon").orderByChild("address").equalTo(checkadress);
                checkroom.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            choices = "Near";
                            System.out.println();
                            for (DataSnapshot datareq : snapshot.getChildren()) {
                                String rmNow = (String) datareq.child("name").getValue();
                                String floorrmNow = (String) datareq.child("floors").getValue();
                                System.out.println("คุณอยู่ใกล้ห้อง  " + rmNow + "   ในฐานข้อมูลชั้นที่  " + floorrmNow);
                            }
                        } else {
                            choices = "NotNear";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        Log.d("bluetoothTest", "name:  " + device.getName() + "   address:  " + device.getAddress());
        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            btleDevice.setRssi(rssi);
            btleDevice.setName(device.getName());
            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesArrayList.add(btleDevice);
        } else {
            mBTDevicesHashMap.get(address).setRssi(rssi);
        }
        adapter.notifyDataSetChanged();
    }

    public void startScan() {
        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();
        mBTLeScanner.start();
    }

    public void stopScan() {
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
                        .setPositiveButton(R.string.app_name, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Checklocation.this,
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
            System.out.println("เปิดบลู");
            return false;
        } else {
            System.out.println("ยังไม่เปิดบลู");
            return true;
        }
    }

    private void QueryRMfirst() {
        Query searchroute = FirebaseDatabase.getInstance().getReference().child(build_id).child("Room").orderByChild("name");
        searchroute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { if (snapshot.getChildrenCount() > 0) { } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void QueryBCfirst() {
        Query searchbeacon = FirebaseDatabase.getInstance().getReference().child(build_id).child("Beacon").orderByChild("address");
        searchbeacon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { if (snapshot.getChildrenCount() > 0) { } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

//    public void onBackPressed(){ }
}