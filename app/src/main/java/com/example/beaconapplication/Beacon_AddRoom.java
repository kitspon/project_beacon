package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Beacon_AddRoom extends AppCompatActivity {
    private final static String TAG = Beacon_AddRoom.class.getSimpleName();

    public static final String EXTRA_ADDRESS = "android.aviles.bletutorial.Activity_BTLE_Services.ADDRESS";
    public static final String Floors = "floors level";
    private String address,floors;
    private Button mButtonUpload,btn_back;
    private TextView beaconaddress;
    private Information_BeaCon BeaCon_Data;
    private Information_Route route;
    private DatabaseReference dbreff,dbreff2;
    private ArrayList<String> listrbuild = new ArrayList<>();
    private ArrayList<String> listfloor = new ArrayList<>();
    private ArrayList<String> listLbuild = new ArrayList<>();
    private ArrayList<String> listRbuild = new ArrayList<>();
    private ArrayList<String> listFbuild = new ArrayList<>();
    private ArrayList<String> listBbuild = new ArrayList<>();
    private Spinner rmname,rm_left,rm_right,rm_front,rm_back;
    private DatabaseReference getDbreff = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> list = new ArrayList<>();
    ImageView map_left,map_right,map_front,map_back;
    String build_id,type;
    String map_L,map_R,map_F,map_B;
    public String statusmap;
    String nleft,nright,nback,nfront,nmain;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_addroom);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        build_id = getIntent().getStringExtra("build_id");

        System.out.println("???????????????????????????????????? AddRoom "+ build_id );

        map_L = getIntent().getStringExtra("mapL");
        map_R = getIntent().getStringExtra("mapR");
        map_F = getIntent().getStringExtra("mapF");
        map_B = getIntent().getStringExtra("mapB");

        nmain = getIntent().getStringExtra("nmain");
        nleft = getIntent().getStringExtra("nleft");
        nright = getIntent().getStringExtra("nright");
        nback = getIntent().getStringExtra("nback");
        nfront = getIntent().getStringExtra("nfront");

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        System.out.println("N LEFT    "+nleft);


        getSupportActionBar().setTitle("SetBeaConRoom");
        dbreff = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon");
        dbreff2 = FirebaseDatabase.getInstance().getReference(build_id).child("Route");

        Intent intent = getIntent();
        floors = intent.getStringExtra("floor");
        type = intent.getStringExtra("type");
        address = intent.getStringExtra("address");

        rmname = findViewById(R.id.list_room);
        rm_left = findViewById(R.id.rm_on_left);
        rm_right = findViewById(R.id.rm_on_right);
        rm_front = findViewById(R.id.rm_on_front);
        rm_back = findViewById(R.id.rm_on_back);

        System.out.println("ชั้นที่ได้เลือกมาได้แก่ชั้นที่ ผ่ามผามผ้ามมม"+ floors);
        ((TextView) findViewById(R.id.tv_address)).setText(address);
//        ((TextView) findViewById(R.id.textfloor)).setText(floors);

        Query queryimg = FirebaseDatabase.getInstance().getReference().child(build_id).child("Floors").orderByChild("level").equalTo(floors);
        queryimg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()){
                        if (item.child("map").getValue()!=null){
                            statusmap = "have";
                        }else {
                            statusmap = "without";
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        map_left = findViewById(R.id.map_left);
        map_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(Beacon_AddRoom.this, "No map yet", Toast.LENGTH_LONG).show();
                }else{
                    nleft = rm_left.getSelectedItem().toString();
                    nright = rm_right.getSelectedItem().toString();
                    nfront = rm_front.getSelectedItem().toString();
                    nback = rm_back.getSelectedItem().toString();
                    nmain = rmname.getSelectedItem().toString();
                Intent intent =  new Intent(Beacon_AddRoom.this, Touch_Insert_Arrow_Add_Beacon.class);
                String nkow = "left";
                intent.putExtra("nkow", nkow);
                intent.putExtra("namephoto", rmname.getSelectedItem().toString().trim()+"left");
                intent.putExtra("address", address);
                intent.putExtra("type", type);
                intent.putExtra("floor", floors);
                intent.putExtra("build_id", build_id);
                intent.putExtra("right", map_R);
                intent.putExtra("front", map_F);
                intent.putExtra("back", map_B);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                }
            }
        });

        map_right = findViewById(R.id.map_right);
        map_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(Beacon_AddRoom.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    nleft = rm_left.getSelectedItem().toString();
                    nright = rm_right.getSelectedItem().toString();
                    nfront = rm_front.getSelectedItem().toString();
                    nback = rm_back.getSelectedItem().toString();
                    nmain = rmname.getSelectedItem().toString();
                    Intent intent = new Intent(Beacon_AddRoom.this, Touch_Insert_Arrow_Add_Beacon.class);
                    String nkow = "right";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", rmname.getSelectedItem().toString().trim() + "right");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("floor", floors);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_L);
                    intent.putExtra("front", map_F);
                    intent.putExtra("back", map_B);
                    intent.putExtra("nmain", nmain);
                    intent.putExtra("nleft", nleft);
                    intent.putExtra("nright", nright);
                    intent.putExtra("nfront", nfront);
                    intent.putExtra("nback", nback);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        map_front = findViewById(R.id.map_front);
        map_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(Beacon_AddRoom.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    nleft = rm_left.getSelectedItem().toString();
                    nright = rm_right.getSelectedItem().toString();
                    nfront = rm_front.getSelectedItem().toString();
                    nback = rm_back.getSelectedItem().toString();
                    nmain = rmname.getSelectedItem().toString();
                    Intent intent = new Intent(Beacon_AddRoom.this, Touch_Insert_Arrow_Add_Beacon.class);
                    String nkow = "front";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", rmname.getSelectedItem().toString().trim() + "front");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("floor", floors);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_L);
                    intent.putExtra("right", map_R);
                    intent.putExtra("back", map_B);
                    intent.putExtra("nmain", nmain);
                    intent.putExtra("nleft", nleft);
                    intent.putExtra("nright", nright);
                    intent.putExtra("nfront", nfront);
                    intent.putExtra("nback", nback);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        map_back = findViewById(R.id.map_back);
        map_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(Beacon_AddRoom.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    nleft = rm_left.getSelectedItem().toString();
                    nright = rm_right.getSelectedItem().toString();
                    nfront = rm_front.getSelectedItem().toString();
                    nback = rm_back.getSelectedItem().toString();
                    nmain = rmname.getSelectedItem().toString();
                    Intent intent = new Intent(Beacon_AddRoom.this, Touch_Insert_Arrow_Add_Beacon.class);
                    String nkow = "back";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", rmname.getSelectedItem().toString().trim() + "back");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("floor", floors);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_L);
                    intent.putExtra("right", map_R);
                    intent.putExtra("front", map_F);
                    intent.putExtra("nmain", nmain);
                    intent.putExtra("nleft", nleft);
                    intent.putExtra("nright", nright);
                    intent.putExtra("nfront", nfront);
                    intent.putExtra("nback", nback);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        if (map_L==null){ }else{ Picasso.get().load(map_L).into(map_left); }

        if (map_R==null){ }else{ Picasso.get().load(map_R).into(map_right); }

        if (map_F==null){ }else{ Picasso.get().load(map_F).into(map_front); }

        if (map_B==null){ }else{ Picasso.get().load(map_B).into(map_back); }

        System.out.println("ว้าย "+map_L);
        System.out.println("ขวา "+map_R);
        System.out.println("หน้า "+map_F);
        System.out.println("หลัง "+map_B);

        beaconaddress = findViewById(R.id.tv_address);

        route = new Information_Route();
        BeaCon_Data = new Information_BeaCon();
        mButtonUpload = findViewById(R.id.btn_add);



        showlistrmname();
        showlistrm_left();
        showlistrm_right();
        showlistrm_front();
        showlistrm_back();

        btn_back = findViewById(R.id.room_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Beacon_AddRoom.this, Select_Add_Type_Beacon.class);
                intent.putExtra(Beacon_AddRoom.EXTRA_ADDRESS,address);
                intent.putExtra("build_id",build_id);
                intent.putExtra("putAddress",address);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rmname.getSelectedItem().toString().trim().equals("")){
                    Toast.makeText(Beacon_AddRoom.this,"กรุณาเลือกห้อง",Toast.LENGTH_LONG).show();
                }else {
                upload();
                }
            }
        });

    }

    private void showlistrmname() {
        getDbreff.child(build_id).child("Room").orderByChild("floor").equalTo(floors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listfloor.clear();
                if (nmain==null){
                    for(DataSnapshot item : snapshot.getChildren()){
                        listfloor.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this,R.layout.stylespinner,listfloor);
                    rmname.setAdapter(ara);
                }else {
                    listfloor.add(nmain);
                    for (DataSnapshot item : snapshot.getChildren()) {
                        System.out.println(item.child("name").getValue().toString());
                        if (nmain.equals(item.child("name").getValue().toString())) {
                        } else if (item.child("name").getValue().toString().equals("")) {
                        } else {
                            listfloor.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this, R.layout.stylespinner, listfloor);
                    rmname.setAdapter(ara);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showlistrm_left() {
        getDbreff.child(build_id).child("Build").child(floors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLbuild.clear();
                if (nleft==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listLbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this, R.layout.stylespinner, listLbuild);
                    rm_left.setAdapter(ara);
                }else {
                    listLbuild.add(nleft);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (nleft.equals(item.child("name").getValue().toString())){

                        }else {
                            listLbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(Beacon_AddRoom.this,R.layout.stylespinner,listLbuild);
                    rm_left.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_right() {
        getDbreff.child(build_id).child("Build").child(floors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRbuild.clear();
                if (nright==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listRbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this, R.layout.stylespinner, listRbuild);
                    rm_right.setAdapter(ara);
                }else {
                    listRbuild.add(nright);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (nright.equals(item.child("name").getValue().toString())){

                        }else {
                            listRbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(Beacon_AddRoom.this,R.layout.stylespinner,listRbuild);
                    rm_right.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_front() {
        getDbreff.child(build_id).child("Build").child(floors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFbuild.clear();
                if (nfront==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listFbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this, R.layout.stylespinner, listFbuild);
                    rm_front.setAdapter(ara);
                }else {
                    listFbuild.add(nfront);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (nfront.equals(item.child("name").getValue().toString())){

                        }else {
                            listFbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(Beacon_AddRoom.this,R.layout.stylespinner,listFbuild);
                    rm_front.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_back() {
        getDbreff.child(build_id).child("Build").child(floors).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBbuild.clear();
                if (nback==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listBbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(Beacon_AddRoom.this, R.layout.stylespinner, listBbuild);
                    rm_back.setAdapter(ara);
                }else {
                    listBbuild.add(nback);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (nback.equals(item.child("name").getValue().toString())){

                        }else {
                            listBbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(Beacon_AddRoom.this,R.layout.stylespinner,listBbuild);
                    rm_back.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void upload() {
        String checkadd = beaconaddress.getText().toString();
        System.out.println("ADDRESS  "+beaconaddress.getText().toString());
        Query address = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").orderByChild("address").equalTo(checkadd);
        address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                System.out.println(snapshot.getChildrenCount());
                for(DataSnapshot item : snapshot.getChildren()){
                    System.out.println(item.child("address").getValue().toString());
                }



                            if (snapshot.getChildrenCount()>0){
                                Toast.makeText(Beacon_AddRoom.this,"บีคอนนี้ได้ลงทะเบียนแล้ว กรุณาเปลี่ยนบีคอน",Toast.LENGTH_LONG).show();
                            }else{
                                String checkname = rmname.getSelectedItem().toString().trim();
                                Query checkn = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").orderByChild("name").equalTo(checkname);
                                checkn.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getChildrenCount()>0){
                                            Toast.makeText(Beacon_AddRoom.this,"ห้องนี้ได้ลงทะเบียนไปแล้ว กรุณาเปลี่ยนห้อง",Toast.LENGTH_LONG).show();
                                        }else{

                                            String L = rm_left.getSelectedItem().toString().trim();
                                            String R = rm_right.getSelectedItem().toString().trim();
                                            String F = rm_front.getSelectedItem().toString().trim();
                                            String B = rm_back.getSelectedItem().toString().trim();
                                            if (rm_left.getSelectedItem().toString().equals("")) { L = null; }
                                            if (rm_right.getSelectedItem().toString().equals("")) { R = null; }
                                            if (rm_front.getSelectedItem().toString().equals("")) { F = null; }
                                            if (rm_back.getSelectedItem().toString().equals("")) { B = null; }
                                            BeaCon_Data.setBuild_id(build_id);
                                            BeaCon_Data.setName(rmname.getSelectedItem().toString().trim());
                                            BeaCon_Data.setFloors(floors);
                                            BeaCon_Data.setAddress(beaconaddress.getText().toString().trim());
                                            BeaCon_Data.setRm_L(L);
                                            BeaCon_Data.setRm_R(R);
                                            BeaCon_Data.setRm_F(F);
                                            BeaCon_Data.setRm_B(B);
                                            BeaCon_Data.setType(type);
                                            list.add(L);
                                            list.add(R);
                                            list.add(F);
                                            list.add(B);
                                            for (int i=0;i<list.size();i++){
                                                if (list.get(i)==null){
                                                }else{
                                                    if (i==0){
                                                        route.setDirection("left");
                                                        route.setFirst(rmname.getSelectedItem().toString().trim());
                                                        route.setEnd(list.get(i));
                                                        route.setMap(map_L);
                                                        dbreff2.child(rmname.getSelectedItem().toString().trim()+"left").setValue(route);
                                                    }
                                                    if (i==1){
                                                        route.setDirection("right");
                                                        route.setFirst(rmname.getSelectedItem().toString().trim());
                                                        route.setEnd(list.get(i));
                                                        route.setMap(map_R);
                                                        dbreff2.child(rmname.getSelectedItem().toString().trim()+"right").setValue(route);
                                                    }
                                                    if (i==2){
                                                        route.setDirection("front");
                                                        route.setFirst(rmname.getSelectedItem().toString().trim());
                                                        route.setEnd(list.get(i));
                                                        route.setMap(map_F);
                                                        dbreff2.child(rmname.getSelectedItem().toString().trim()+"front").setValue(route);
                                                    }
                                                    if (i==3){
                                                        route.setDirection("back");
                                                        route.setFirst(rmname.getSelectedItem().toString().trim());
                                                        route.setEnd(list.get(i));
                                                        route.setMap(map_B);
                                                        dbreff2.child(rmname.getSelectedItem().toString().trim()+"back").setValue(route);
                                                    }

                                                }

                                            }

                                            dbreff.child(rmname.getSelectedItem().toString().trim()).setValue(BeaCon_Data);
                                            Toast.makeText(Beacon_AddRoom.this,"เพิ่มสถานที่ สำเร็จ",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Beacon_AddRoom.this,ScanBeacon.class);
                                            intent.putExtra("build_id",build_id);
                                            intent.putExtra("status",status);
                                            intent.putExtra("userid",userid);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

    }
    public void onBackPressed(){ }
}