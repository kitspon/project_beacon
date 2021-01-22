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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Navigationpage_outdoor extends AppCompatActivity {
    private String name, floor;
    private Handler delay = new Handler();
    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;
    private ListView listView;
    private Button Start,Btn_back;
    public String rmTar, first, end, direction, map, rmNow, floorrmNow, findelenow,findeletar;
    public String floorrmtarget,Textdisplayroute,Textdisplaynow,imgdisplayroute;
    public boolean point = false;
    private TextView Showdisplayroute,Showdisplaynow;
    private ArrayList<String> countway = new ArrayList<>();
    private ArrayList<Information_Route> eqarraytest = new ArrayList<Information_Route>();
    private Scanner_Navigationoutdoor mBTLeScanner;
    public boolean findcomplete;
    private ArrayList<String> databeaconfirebase = new ArrayList<>();
    ImageView dismap;
    public String build_id,la,lng,build_name,checkway;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationpage_outdoor2);


        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        System.out.println("Build ID ---------------------- "+build_id);


        name = getIntent().getStringExtra("name");
        floor = getIntent().getStringExtra("floor");

        System.out.println("ค่าที่ได้ดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดดด  " + name);



        getSupportActionBar().setTitle("Route Search");
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mBTLeScanner = new Scanner_Navigationoutdoor(this, 1000, -500);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);
        listView = new ListView(this);
        listView.setAdapter(adapter);

        Showdisplayroute = (TextView) findViewById(R.id.dis2_route);
        Showdisplaynow = (TextView) findViewById(R.id.dis2_roomnow);

        checkLocationPermission();

        Query queryway = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(name);
        queryway.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    checkway = "yes";
                    System.out.println("Found a route");
                }else{
                    checkway = "no";
                    System.out.println("No route found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        QueryBCfirst();
        QueryRMfirst();
        QueryROUTEfirst();

        dismap = findViewById(R.id.dis2_map);

        Btn_back = findViewById(R.id.dis2_btn_back);
        Btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findcomplete = true;
                Intent intent = new Intent(Navigationpage_outdoor.this, ListRoomForOutdoor.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        firstProcess();

        Start = (Button) findViewById(R.id.dis2_btn_start);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String setzero = null;
                Picasso.get().load(setzero).into(dismap);
                Showdisplaynow.setText("");
                Showdisplayroute.setText("Searching for a route...");
                findcomplete = false;
                delay.postDelayed(rescan, 250);


            }
        });



    }

    private void firstProcess() {
        String setzero = null;
        Picasso.get().load(setzero).into(dismap);
        Showdisplaynow.setText("");
        Showdisplayroute.setText("Searching for a route...");
        findcomplete = false;
        delay.postDelayed(rescan, 250);
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

    private void QueryROUTEfirst() {
        Query searchroute = FirebaseDatabase.getInstance().getReference().child(build_id).child("Route").orderByChild("first");
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



    private Runnable rescan = new Runnable() {
        @Override
        public void run() {
            if (findcomplete == false) {
                eqarraytest.clear();
                countway.clear();
                startScan();
                delay.postDelayed(step1, 1200);
                rmTar = name;
                System.out.println(rmTar);
                delay.postDelayed(rescan2, 5000);
            } else {
            }
        }
    };

    private Runnable rescan2 = new Runnable() {
        @Override
        public void run() {

            if (findcomplete == false) {
                eqarraytest.clear();
                countway.clear();
                startScan();
                delay.postDelayed(step1, 1200);
                rmTar = name;
                System.out.println(rmTar);
                delay.postDelayed(rescan, 5000);
            } else {
            }
        }
    };

    private Runnable step1 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Checkmylocation();
            delay.postDelayed(checkinstance, 1000);
        }
    };


    private Runnable rusultmyroom = new Runnable() {
        @Override
        public void run() {
            SelectrmTarget(rmTar);
        }
    };


    private Runnable checkinstance = new Runnable() {
        @Override
        public void run() {
            floorrmtarget = floor;
            if (checkway.equals("no")) {
                Showdisplayroute.setText("No route found");
            }else if (floorrmNow == null) {
                Showdisplayroute.setText("Can't find route");
                findcomplete=true;
            } else if (floorrmNow.equals(floorrmtarget)) {
                System.out.println("ชั้นเดียวกัน");
                delay.postDelayed(rusultmyroom, 1000);
            } else {
                System.out.println("คนละชั้นกัน");
                Query findelefloornow = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").orderByChild("floor").equalTo(floorrmNow);
                findelefloornow.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            findelenow = (String) item.child("name").getValue();
                            System.out.println("ลิฟต์ชั้นที่จะขึ้น" + findelenow);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                Query findelefloortar = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").orderByChild("floor").equalTo(floorrmtarget);
                findelefloortar.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            findeletar = (String) item.child("name").getValue();
                            System.out.println("ลิฟต์ที่จะขึ้นไป" + findeletar);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                delay.postDelayed(Searchnameelevator, 1000);
            }

        }
    };


    private Runnable Searchnameelevator = new Runnable() {
        @Override
        public void run() {
            System.out.println("หน้า Searchnameelevator  findeletar =  " + findeletar + "  findelenow  = " + findelenow);
            eqarraytest.clear();
            System.out.println("ลิฟต์ที่จะหา    " + findelenow);
            if (findelenow==null){
                Showdisplayroute.setText("Elevator information not found..");
            }
            if (rmNow.equals(findelenow)) {
                System.out.println(build_id);
                System.out.println("อยู่ตรงลิฟต์");
                eqarraytest.clear();

                Query findrouteelevator = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(findeletar);
                findrouteelevator.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            System.out.println("มี");
                            for (DataSnapshot item : snapshot.getChildren()) {
                                direction = (String) item.child("direction").getValue();
                                first = (String) item.child("first").getValue();
                                end = (String) item.child("end").getValue();
                                map = (String) item.child("map").getValue();
                                if (direction.equals("left")) {
                                } else if (direction.equals("right")) {
                                } else if (direction.equals("front")) {
                                } else if (direction.equals("back")) {
                                } else if (first.equals(rmNow)) {
                                    eqarraytest.add(new Information_Route(first, end, direction, map));
                                    System.out.println("*****กรณีอยู่ตรงลิฟต์*****");
                                    System.out.println("การเดินทาง" + direction);
                                    System.out.println("ต้น" + first);
                                    System.out.println("ปลาย" + end);
                                }
                            }
                        } else {
                            System.out.println("ไม่มี");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else {
                System.out.println("ไม่อยู่ตรงลิฟต์");
                eqarraytest.clear();
                Query findrouteelevator = FirebaseDatabase.getInstance().getReference().child(build_id).child("Route").orderByChild("end").equalTo(findelenow);
                findrouteelevator.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            System.out.println("มี");
                            for (DataSnapshot item : snapshot.getChildren()) {
                                direction = (String) item.child("direction").getValue();
                                first = (String) item.child("first").getValue();
                                end = (String) item.child("end").getValue();
                                map = (String) item.child("map").getValue();
                                if (direction.equals("tothetop")) {
                                } else if (direction.equals("tothedown")) {
                                } else {
                                    eqarraytest.add(new Information_Route(first, end, direction, map));
                                    System.out.println("*****ไม่อยู่ตรงลิฟต์*****");
                                    System.out.println("การเดินทาง" + direction);
                                    System.out.println("ต้น" + first);
                                    System.out.println("ปลาย" + end);
                                }
                            }
                        } else {
                            System.out.println("ไม่มี");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            delay.postDelayed(gotoele, 1000);
        }
    };

    private Runnable gotoele = new Runnable() {
        @Override
        public void run() {
            Textdisplaynow = "You are at " + rmNow;
            if (rmNow.equals(rmTar)) {
                Textdisplayroute = "Arriving at "+rmTar;
                imgdisplayroute = "";
                Showtextdisplay();
                findcomplete = true;
            } else {
                for (int i = 0; i < eqarraytest.size(); i++) {
                    if (eqarraytest.get(i).first.equals(rmNow)) {
                        if (eqarraytest.get(i).direction.equals("left")) {

                            imgdisplayroute = eqarraytest.get(i).map;
                            Textdisplayroute = "Turn left to" + eqarraytest.get(i).end;
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("right")) {
                            imgdisplayroute = eqarraytest.get(i).map;
                            Textdisplayroute = "Turn right to" + eqarraytest.get(i).end;
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("front")) {
                            imgdisplayroute = eqarraytest.get(i).map;
                            Textdisplayroute = "Go straight " + eqarraytest.get(i).end;
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("back")) {
                            imgdisplayroute = eqarraytest.get(i).map;
                            Textdisplayroute = "Go back " + eqarraytest.get(i).end;
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("tothetop")) {
                            if (floorrmtarget.equals("1")){
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator up to " + floorrmtarget+"st" + " floor";
                                Showtextdisplay();
                            }else if (floorrmtarget.equals("2")){
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator up to " + floorrmtarget+"nd" + " floor";
                                Showtextdisplay();
                            }else if (floorrmtarget.equals("3")){
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator up to " + floorrmtarget+"rd" + " floor";
                                Showtextdisplay();
                            }else {
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator up to " + floorrmtarget+"th" + " floor";
                                Showtextdisplay();
                            }
                        }
                        if (eqarraytest.get(i).direction.equals("tothedown")) {
                            if (floorrmtarget.equals("1")){
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator down to " + floorrmtarget+"st" + " floor";
                                Showtextdisplay();
                            }else if (floorrmtarget.equals("2")){
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator down to " + floorrmtarget+"nd" + " floor";
                                Showtextdisplay();
                            }else if (floorrmtarget.equals("3")) {
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator down to " + floorrmtarget+"rd" + " floor";
                                Showtextdisplay();
                            }else {
                                imgdisplayroute = eqarraytest.get(i).map;
                                Textdisplayroute = "Take the elevator down to " + floorrmtarget+"th" + " floor";
                                Showtextdisplay();
                            }
                        }
                        System.out.println("เจอแล้ว " + eqarraytest.get(i).end + " ไปทาง " + eqarraytest.get(i).direction);
                        break;
                    } else {
                        LoopSearchRouteElevator(eqarraytest.get(i).first, eqarraytest.get(i).end, eqarraytest.get(i).direction);
                    }
                }
            }
        }
    };

    private void LoopSearchRouteElevator(String f, String e, String d) {
        Query rmtarget = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(f);
        rmtarget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot datareq : snapshot.getChildren()) {
                        direction = (String) datareq.child("direction").getValue();
                        first = (String) datareq.child("first").getValue();
                        end = (String) datareq.child("end").getValue();
                        map = (String) datareq.child("map").getValue();
                        System.out.println("''''''''''''''''''");
                        System.out.println("การเดินทาง" + direction);
                        System.out.println("ต้น" + first);
                        System.out.println("ปลาย" + end);
                        if (direction.equals("tothetop")) {
                            point = false;
                        } else if (direction.equals("tothedown")) {
                            point = false;
                        } else if (first.equals(e)) {
                            System.out.println("เส้นทางเดิม" + first + " " + end + " " + direction);
                            point = false;
                        } else {
                            point = true;
                            if (first.equals(rmNow)) {
                                if (direction.equals("left")) {
                                    imgdisplayroute = map;
                                    Textdisplayroute = "Turn left to";
                                    Showtextdisplay();
                                }
                                if (direction.equals("right")) {
                                    imgdisplayroute = map;
                                    Textdisplayroute = "Turn right to";
                                    Showtextdisplay();
                                }
                                if (direction.equals("front")) {
                                    imgdisplayroute = map;
                                    Textdisplayroute = "Go straight";
                                    Showtextdisplay();
                                }
                                if (direction.equals("back")) {
                                    imgdisplayroute = map;
                                    Textdisplayroute = "Go back";
                                    Showtextdisplay();
                                }
                                break;
                            } else {
                                if (point = true) {
                                    System.out.println("ทางที่ต้องค้นหาอีกทีนึง" + first + " " + end + " " + direction);
                                    LoopSearchRouteElevator(first, end, direction);
                                }
                            }
                        }
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void SelectrmTarget(String tar) {
        eqarraytest.clear();
        Query rmtarget = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(tar);
        rmtarget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot datareq : snapshot.getChildren()) {
                        direction = (String) datareq.child("direction").getValue();
                        first = (String) datareq.child("first").getValue();
                        end = (String) datareq.child("end").getValue();
                        map = (String) datareq.child("map").getValue();
                        if (direction.equals("tothetop")) {
                        } else if (direction.equals("tothedown")) {
                        } else {
                            eqarraytest.add(new Information_Route(first, end, direction, map));
                            System.out.println("===========");
                            System.out.println("ทิศ" + direction);
                            System.out.println("ต้น" + first);
                            System.out.println("ปลาย" + end);
                        }
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        delay.postDelayed(check, 1000);
    }

    private Runnable check = new Runnable() {
        @Override
        public void run() {

            if (rmNow.equals(rmTar)) {
                Textdisplaynow = "";
                Textdisplayroute = "Arriving at "+rmTar;
                Query rm = FirebaseDatabase.getInstance().getReference().child(build_id).child("Room").orderByChild("name").equalTo(rmTar);
                rm.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            for (DataSnapshot item : snapshot.getChildren()){
                                imgdisplayroute = item.child("image").getValue().toString();
                            }
                        } }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                Showtextdisplay();
                findcomplete = true;
            } else if (findcomplete == false) {
                for (int i = 0; i < eqarraytest.size(); i++) {
                    if (eqarraytest.get(i).first.equals(rmNow)) {
                        if (eqarraytest.get(i).direction.equals("left")) {
                            if (eqarraytest.get(i).map==null){}
                            else {
                                imgdisplayroute = eqarraytest.get(i).map;
                            }
                            Textdisplaynow = "You are at " + rmNow;
                            Textdisplayroute = "Turn left";
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("right")) {
                            if (eqarraytest.get(i).map==null){}
                            else {
                                imgdisplayroute = eqarraytest.get(i).map;
                            }
                            Textdisplaynow = "You are at" + rmNow;
                            Textdisplayroute = "Turn right";
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("front")) {
                            if (eqarraytest.get(i).map==null){}
                            else {
                                imgdisplayroute = eqarraytest.get(i).map;
                            }
                            Textdisplaynow = "You are at " + rmNow;
                            Textdisplayroute = "Go straight";
                            Showtextdisplay();
                        }
                        if (eqarraytest.get(i).direction.equals("back")) {
                            if (eqarraytest.get(i).map==null){}
                            else {
                                imgdisplayroute = eqarraytest.get(i).map;
                            }
                            Textdisplaynow = "You are at " + rmNow;
                            Textdisplayroute = "Go back";
                            Showtextdisplay();
                        }
                        break;
                    } else {
//                        System.out.println("เส้นทางที่ไปหาได้มี "+eqarray.get(i).first+" "+eqarray.get(i).end+" "+eqarray.get(i).direc);
                        LoopSearchRouteSamFloor(eqarraytest.get(i).first, eqarraytest.get(i).end);
                    }
                }
            }

        }
    };

    private void LoopSearchRouteSamFloor(String f, String e) {
        Query rmtarget = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(f);
        rmtarget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot datareq : snapshot.getChildren()) {
                        direction = (String) datareq.child("direction").getValue();
                        first = (String) datareq.child("first").getValue();
                        end = (String) datareq.child("end").getValue();
                        map = (String) datareq.child("map").getValue();
                        System.out.println("''''''''''''''''''");
                        if (direction.equals("tothedown")) {
                            point = false;
                        } else if (direction.equals("tothetop")) {
                            point = false;
                        } else if (first.equals(e)) {
//                            System.out.println("เส้นทางเดิม"+first+" "+end+" "+direction);
                            point = false;
                        } else {
                            point = true;
                            if (first.equals(rmNow)) {
                                if (direction.equals("left")) {
                                    if (map==null){}
                                    else {
                                        imgdisplayroute = map;
                                    }
                                    Textdisplaynow = "You are at " + rmNow;
                                    Textdisplayroute = "Turn left";
                                    Showtextdisplay();
                                }
                                if (direction.equals("right")) {
                                    if (map==null){}
                                    else {
                                        imgdisplayroute = map;
                                        Textdisplaynow = "You are at " + rmNow;
                                        Textdisplayroute = "Turn right";
                                        Showtextdisplay();
                                    }
                                }
                                if (direction.equals("front")) {
                                    if (map==null){}
                                    else {
                                        imgdisplayroute = map;
                                    }
                                    Textdisplaynow = "You are at " + rmNow;
                                    Textdisplayroute = "Go straight";
                                    Showtextdisplay();
                                }
                                if (direction.equals("back")) {
                                    if (map==null){}
                                    else {
                                        imgdisplayroute = map;
                                    }
                                    Textdisplaynow = "You are at " + rmNow;
                                    Textdisplayroute = "Go back";
                                    Showtextdisplay();
                                }
//                               Toast.makeText(Place.this,"เดินไปทาง "+direction ,Toast.LENGTH_LONG).show();
                                System.out.println("เจอแล้วไปทาง " + direction);
                                break;
                            } else {
                                if (point = true) {
                                    System.out.println("ทางที่ต้องค้นหาอีกทีนึง" + first + " " + end + " " + direction);
                                    LoopSearchRouteSamFloor(first, end);
                                }
                            }
                        }
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void Showtextdisplay() {
        String setzero = null;
        delay.postDelayed(Showtextdisplay2,120);
        Showdisplaynow.setText("");
        Showdisplayroute.setText("");
        Picasso.get().load(setzero).into(dismap);
    }

    private Runnable Showtextdisplay2 = new Runnable() {
        @Override
        public void run() {
            Showdisplaynow.setText(Textdisplaynow);
            Showdisplayroute.setText(Textdisplayroute);
            if (imgdisplayroute.equals("")){
                imgdisplayroute = null;
                Picasso.get().load(imgdisplayroute).into(dismap);
            }else{
                Picasso.get().load(imgdisplayroute).into(dismap);
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
        for (int i = 0; i < mBTDevicesArrayList.size(); i++) {
            String checkadress = mBTDevicesArrayList.get(i).getBluetoothDevice().toString();
            Query checkroom = FirebaseDatabase.getInstance().getReference().child(build_id).child("Beacon").orderByChild("address").equalTo(checkadress);
            checkroom.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot datareq : snapshot.getChildren()) {
                            rmNow = (String) datareq.child("name").getValue();
                            floorrmNow = (String) datareq.child("floors").getValue();
                            System.out.println("คุณอยู่ใกล้ห้อง  " + rmNow + "   ในฐานข้อมูลชั้นที่  " + floorrmNow);
                        }
                    } else {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
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
                                ActivityCompat.requestPermissions(Navigationpage_outdoor.this,
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