package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class UpdateAndDelete_Beacon extends AppCompatActivity {
    private Handler delay = new Handler();
    private Spinner main,left,right,front,back;
    private DatabaseReference condb = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> listfloor = new ArrayList<>();
    private String name,address,floor,rm_l,rm_r,rm_f,rm_b,build_id,type;
    private ArrayList<String> listlbuild = new ArrayList<>();
    private ArrayList<String> listrbuild = new ArrayList<>();
    private ArrayList<String> listfbuild = new ArrayList<>();
    private ArrayList<String> listbbuild = new ArrayList<>();
    private Button btn_update,btn_delete,btn_back;
    private DatabaseReference delete,dbroute,dbbeacon;
    private Information_BeaCon BeaCon_Data = new Information_BeaCon();
    private ArrayList<String> list = new ArrayList<>();
    private Information_Route route = new Information_Route();
    public ArrayList<String> key = new ArrayList<>();
    ImageView imgleft,imgright,imgfront,imgback;
    String map_l,map_r,map_f,map_b,checktype;
    TextView textaddtress;
    ImageView map_left,map_right,map_front,map_back;
    public String stepquery, Countfloors, statusmap;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_and_delete__beacon);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        build_id = getIntent().getStringExtra("build_id");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        floor = getIntent().getStringExtra("floor");
        type = getIntent().getStringExtra("type");
        rm_l = getIntent().getStringExtra("rm_l");
        rm_r = getIntent().getStringExtra("rm_r");
        rm_f = getIntent().getStringExtra("rm_f");
        rm_b = getIntent().getStringExtra("rm_b");
        stepquery = getIntent().getStringExtra("stepquery");

        map_l = getIntent().getStringExtra("map_l");
        map_r = getIntent().getStringExtra("map_r");
        map_f = getIntent().getStringExtra("map_f");
        map_b = getIntent().getStringExtra("map_b");

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        System.out.println(stepquery+"   stepquery");
        System.out.println(type+"   type");
        System.out.println(build_id+"   build_id");
        System.out.println(name+"   name");
        System.out.println(address+"    address");
        System.out.println(floor+"  floor");
        System.out.println(rm_l+"   rm_l");
        System.out.println(rm_r+"   rm_r");
        System.out.println(rm_f+"   rm_f");
        System.out.println(rm_b+"   rm_b");

        main = findViewById(R.id.edit_main);
        left = findViewById(R.id.edit_l);
        right = findViewById(R.id.edit_r);
        front = findViewById(R.id.edit_f);
        back = findViewById(R.id.edit_b);

        textaddtress = findViewById(R.id.edit_address);

        imgleft = findViewById(R.id.edit_left);
        imgright = findViewById(R.id.edit_right);
        imgfront = findViewById(R.id.edit_front);
        imgback = findViewById(R.id.edit_back);

        getcountele();

        textaddtress.setText(address);

        btn_back = findViewById(R.id.edit_back_btn);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(UpdateAndDelete_Beacon.this, ListBeacon.class);
                intent.putExtra("build_id", build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_delete = findViewById(R.id.edit_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAndDelete_Beacon.this);
                builder.setCancelable(true);
                builder.setTitle("DELETE BEACON");
                builder.setMessage("Do you want to delete beacon?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
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
        });
        btn_update = findViewById(R.id.edit_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        if (map_l==null){ }else{ Picasso.get().load(map_l).into(imgleft); }

        if (map_r==null){ }else{ Picasso.get().load(map_r).into(imgright); }

        if (map_f==null){ }else{ Picasso.get().load(map_f).into(imgfront); }

        if (map_b==null){ }else{ Picasso.get().load(map_b).into(imgback); }

        showlistrmname();
        showlistrm_left();
        showlistrm_right();
        showlistrm_front();
        showlistrm_back();
        Querykey();
        Querymap();

        Query queryimg = FirebaseDatabase.getInstance().getReference().child(build_id).child("Floors").orderByChild("level").equalTo(floor);
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

        map_left = findViewById(R.id.edit_left);
        map_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(UpdateAndDelete_Beacon.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    stepquery = "1";
                    rm_l = left.getSelectedItem().toString();
                    rm_r = right.getSelectedItem().toString();
                    rm_f = front.getSelectedItem().toString();
                    rm_b = back.getSelectedItem().toString();
                    name = main.getSelectedItem().toString();
                    Intent intent = new Intent(UpdateAndDelete_Beacon.this, Touch_Insert_Edit_Beacon.class);
                    String nkow = "left";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", main.getSelectedItem().toString().trim() + "left");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("name", name);
                    intent.putExtra("stepquery", stepquery);
                    intent.putExtra("floor", floor);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("right", map_r);
                    intent.putExtra("front", map_f);
                    intent.putExtra("back", map_b);
                    intent.putExtra("rm_l", rm_l);
                    intent.putExtra("rm_r", rm_r);
                    intent.putExtra("rm_f", rm_f);
                    intent.putExtra("rm_b", rm_b);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        map_right = findViewById(R.id.edit_right);
        map_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(UpdateAndDelete_Beacon.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    stepquery = "1";
                    rm_l = left.getSelectedItem().toString();
                    rm_r = right.getSelectedItem().toString();
                    rm_f = front.getSelectedItem().toString();
                    rm_b = back.getSelectedItem().toString();
                    name = main.getSelectedItem().toString();
                    Intent intent = new Intent(UpdateAndDelete_Beacon.this, Touch_Insert_Edit_Beacon.class);
                    String nkow = "right";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", main.getSelectedItem().toString().trim() + "right");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("stepquery", stepquery);
                    intent.putExtra("name", name);
                    intent.putExtra("floor", floor);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_l);
                    intent.putExtra("front", map_f);
                    intent.putExtra("back", map_b);
                    intent.putExtra("rm_l", rm_l);
                    intent.putExtra("rm_r", rm_r);
                    intent.putExtra("rm_f", rm_f);
                    intent.putExtra("rm_b", rm_b);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        map_front = findViewById(R.id.edit_front);
        map_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(UpdateAndDelete_Beacon.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    stepquery = "1";
                    rm_l = left.getSelectedItem().toString();
                    rm_r = right.getSelectedItem().toString();
                    rm_f = front.getSelectedItem().toString();
                    rm_b = back.getSelectedItem().toString();
                    name = main.getSelectedItem().toString();
                    Intent intent = new Intent(UpdateAndDelete_Beacon.this, Touch_Insert_Edit_Beacon.class);
                    String nkow = "front";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", main.getSelectedItem().toString().trim() + "front");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("floor", floor);
                    intent.putExtra("stepquery", stepquery);
                    intent.putExtra("name", name);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_l);
                    intent.putExtra("right", map_r);
                    intent.putExtra("back", map_b);
                    intent.putExtra("rm_l", rm_l);
                    intent.putExtra("rm_r", rm_r);
                    intent.putExtra("rm_f", rm_f);
                    intent.putExtra("rm_b", rm_b);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        map_back = findViewById(R.id.edit_back);
        map_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusmap.equals("without")){
                    Toast.makeText(UpdateAndDelete_Beacon.this, "No map yet", Toast.LENGTH_LONG).show();
                }else {
                    stepquery = "1";
                    rm_l = left.getSelectedItem().toString();
                    rm_r = right.getSelectedItem().toString();
                    rm_f = front.getSelectedItem().toString();
                    rm_b = back.getSelectedItem().toString();
                    name = main.getSelectedItem().toString();
                    Intent intent = new Intent(UpdateAndDelete_Beacon.this, Touch_Insert_Edit_Beacon.class);
                    String nkow = "back";
                    intent.putExtra("nkow", nkow);
                    intent.putExtra("namephoto", main.getSelectedItem().toString().trim() + "back");
                    intent.putExtra("address", address);
                    intent.putExtra("type", type);
                    intent.putExtra("name", name);
                    intent.putExtra("stepquery", stepquery);
                    intent.putExtra("floor", floor);
                    intent.putExtra("build_id", build_id);
                    intent.putExtra("left", map_l);
                    intent.putExtra("right", map_r);
                    intent.putExtra("front", map_f);
                    intent.putExtra("rm_l", rm_l);
                    intent.putExtra("rm_r", rm_r);
                    intent.putExtra("rm_f", rm_f);
                    intent.putExtra("rm_b", rm_b);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        dbbeacon = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon");
        dbroute = FirebaseDatabase.getInstance().getReference(build_id).child("Route");

        }

    private void getcountele() {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child(build_id).child("Floors");
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    Countfloors = item.child("level").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void Querymap() {
        if (stepquery==null){
            Query query =  FirebaseDatabase.getInstance().getReference().child(build_id).child("Route").orderByChild("first").equalTo(name);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot item : snapshot.getChildren()) {
                        System.out.println("ค่าในเส้นทาง    " + item.getKey());
                        if (item.child("direction").getValue().toString().equals("left")) {
                            if (item.child("map").getValue()!=null) {
                                map_l = item.child("map").getValue().toString();
                                Picasso.get().load(item.child("map").getValue().toString()).into(imgleft);
                            }
                        }
                        if (item.child("direction").getValue().toString().equals("right")) {
                            if (item.child("map").getValue()!=null) {
                                map_r = item.child("map").getValue().toString();
                                Picasso.get().load(item.child("map").getValue().toString()).into(imgright);
                            }
                        }
                        if (item.child("direction").getValue().toString().equals("front")) {
                            if (item.child("map").getValue()!=null) {
                                map_f = item.child("map").getValue().toString();
                                Picasso.get().load(item.child("map").getValue().toString()).into(imgfront);
                            }
                        }
                        if (item.child("direction").getValue().toString().equals("back")) {
                            if (item.child("map").getValue()!=null) {
                                map_b = item.child("map").getValue().toString();
                                Picasso.get().load(item.child("map").getValue().toString()).into(imgback);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{

        }
    }

    private void Querykey() {
        Query query =  FirebaseDatabase.getInstance().getReference().child(build_id).child("Route").orderByChild("first").equalTo(name);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    key.add(item.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void update() {
                    String checkname = main.getSelectedItem().toString().trim();
                    Query checkn = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").orderByChild("name").equalTo(checkname);
                    checkn.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                if (main.getSelectedItem().toString().equals(name)){
                                    System.out.println("ห้องตัวเองอัพเดตได้");
                                    checktype();
                                    delay.postDelayed(confirmupdate,500);
//                                    confirmupdate();
                                }else {
                                    Toast.makeText(UpdateAndDelete_Beacon.this, "ห้องนี้ได้ลงทะเบียนไปแล้ว กรุณาเปลี่ยนห้อง", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                checktype();
                                delay.postDelayed(confirmupdate,500);
//                                confirmupdate();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
    }

    private void checktype() {
        Query query = FirebaseDatabase.getInstance().getReference(build_id).child("Room").orderByChild("name");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    if (item.child("name").getValue().toString().equals(main.getSelectedItem().toString())){
                        checktype = "room";
                        System.out.println("เป็นประเภทROOM");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query querys = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").orderByChild("name");
        querys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    if (item.child("name").getValue().toString().equals(main.getSelectedItem().toString())){
                        checktype = "elevator";
                        System.out.println("เป็นประเภทELEVATOR");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Runnable confirmupdate = new Runnable() {
        @Override
        public void run() {
                delete = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(name);
                delete.setValue(null);
                for (int i = 0; i < key.size(); i++) {
                    delete = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(key.get(i));
                    delete.setValue(null);
                }
            if (checktype.equals("room")) {
                String L = left.getSelectedItem().toString().trim();
                String R = right.getSelectedItem().toString().trim();
                String F = front.getSelectedItem().toString().trim();
                String B = back.getSelectedItem().toString().trim();
                if (left.getSelectedItem().toString().equals("")) {
                    L = null;
                }
                if (right.getSelectedItem().toString().equals("")) {
                    R = null;
                }
                if (front.getSelectedItem().toString().equals("")) {
                    F = null;
                }
                if (back.getSelectedItem().toString().equals("")) {
                    B = null;
                }
                BeaCon_Data.setBuild_id(build_id);
                BeaCon_Data.setName(main.getSelectedItem().toString().trim());
                BeaCon_Data.setFloors(floor);
                BeaCon_Data.setAddress(address);
                BeaCon_Data.setRm_L(L);
                BeaCon_Data.setRm_R(R);
                BeaCon_Data.setRm_F(F);
                BeaCon_Data.setRm_B(B);
                BeaCon_Data.setType(checktype);
                list.add(L);
                list.add(R);
                list.add(F);
                list.add(B);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) == null) {
                    } else {
                        if (i == 0) {
                            route.setDirection("left");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_l);
                            dbroute.child(main.getSelectedItem().toString().trim() + "left").setValue(route);
                        }
                        if (i == 1) {
                            route.setDirection("right");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_r);
                            dbroute.child(main.getSelectedItem().toString().trim() + "right").setValue(route);
                        }
                        if (i == 2) {
                            route.setDirection("front");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_f);
                            dbroute.child(main.getSelectedItem().toString().trim() + "front").setValue(route);
                        }
                        if (i == 3) {
                            route.setDirection("back");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_b);
                            dbroute.child(main.getSelectedItem().toString().trim() + "back").setValue(route);
                        }
                    }
                }

                dbbeacon.child(main.getSelectedItem().toString().trim()).setValue(BeaCon_Data);
                Toast.makeText(UpdateAndDelete_Beacon.this, "Update Success!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UpdateAndDelete_Beacon.this, ListBeacon.class);
                intent.putExtra("build_id", build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }else if (checktype.equals("elevator")){
                String L = left.getSelectedItem().toString().trim();
                String R = right.getSelectedItem().toString().trim();
                String F = front.getSelectedItem().toString().trim();
                String B = back.getSelectedItem().toString().trim();

                if (left.getSelectedItem().toString().equals("")) { L = null; }
                if (right.getSelectedItem().toString().equals("")) { R = null; }
                if (front.getSelectedItem().toString().equals("")) { F = null; }
                if (back.getSelectedItem().toString().equals("")) { B = null; }

                BeaCon_Data.setName(main.getSelectedItem().toString().trim());
                BeaCon_Data.setFloors(floor);
                BeaCon_Data.setAddress(address);
                BeaCon_Data.setRm_L(L);
                BeaCon_Data.setRm_R(R);
                BeaCon_Data.setRm_F(F);
                BeaCon_Data.setRm_B(B);
                BeaCon_Data.setType(checktype);
                BeaCon_Data.setBuild_id(build_id);

                list.add(L);
                list.add(R);
                list.add(F);
                list.add(B);
                for (int i=0;i<list.size();i++){
                    if (list.get(i)==null){
                    }else{
                        if (i==0){
                            route.setDirection("left");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_l);
                            dbroute.child(main.getSelectedItem().toString()+"left").setValue(route);
                        }
                        if (i==1){
                            route.setDirection("right");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_r);
                            dbroute.child(main.getSelectedItem().toString()+"right").setValue(route);
                        }
                        if (i==2){
                            route.setDirection("front");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_f);
                            dbroute.child(main.getSelectedItem().toString()+"front").setValue(route);
                        }
                        if (i==3){
                            route.setDirection("back");
                            route.setFirst(main.getSelectedItem().toString().trim());
                            route.setEnd(list.get(i));
                            route.setMap(map_b);
                            dbroute.child(main.getSelectedItem().toString()+"back").setValue(route);
                        }
                    }

                }
                System.out.println("ทำการเพิ่มลิฟต์");

                for ( int i=1;i<(Integer.parseInt(Countfloors)+1); i++){
                    Query qelevator = FirebaseDatabase.getInstance().getReference().child(build_id).child("Elevator")
                            .orderByChild("floor").equalTo(String.valueOf(i));
                    qelevator.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0) {
                                System.out.println(":                มี");
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    System.out.println("ทำการแรกชื่อ  " + item.child("name").getValue().toString());
                                    if (item.child("name").getValue().toString().equals("")){

                                    }else{
                                        if (item.child("floor").getValue().toString().equals(floor)) {
                                        } else {
                                            if (Integer.parseInt(floor) < Integer.parseInt(item.child("floor").getValue().toString())) {
                                                route.setDirection("tothetop");
                                                route.setFirst(main.getSelectedItem().toString().trim());
                                                route.setEnd(item.child("name").getValue().toString());
                                                route.setMap("");
                                                dbroute.child(main.getSelectedItem().toString()+"tothetop"+item.child("name").getValue().toString()).setValue(route);
                                            } else {
                                                route.setDirection("tothedown");
                                                route.setFirst(main.getSelectedItem().toString().trim());
                                                route.setEnd(item.child("name").getValue().toString());
                                                route.setMap("");
                                                dbroute.child(main.getSelectedItem().toString()+"tothedown"+item.child("name").getValue().toString()).setValue(route);
                                            }
                                        }
                                    }
                                }
                            }else {System.out.println(":                ไม่มี");}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                dbbeacon.child(main.getSelectedItem().toString().trim()).setValue(BeaCon_Data);
                Toast.makeText(UpdateAndDelete_Beacon.this,"เพิ่มสถานที่ สำเร็จ",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UpdateAndDelete_Beacon.this,ListBeacon.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    };


    private void delete() {
        delete = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(name);
        delete.setValue(null);
        Query query =  FirebaseDatabase.getInstance().getReference().child(build_id).child("Route").orderByChild("first").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    System.out.println(item.getKey());
                    delete = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(item.getKey());
                    delete.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent intent = new Intent(UpdateAndDelete_Beacon.this, ListBeacon.class);
        intent.putExtra("build_id",build_id);
        intent.putExtra("status",status);
        intent.putExtra("userid",userid);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showlistrmname() {
        condb.child(build_id).child("Build").child(floor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listfloor.clear();
                listfloor.add(name);
                for(DataSnapshot item : snapshot.getChildren()){
                    System.out.println(item.child("name").getValue().toString());
                    if (name.equals(item.child("name").getValue().toString())){
                    }else if(item.child("name").getValue().toString().equals("")){
                    }else {
                        listfloor.add(item.child("name").getValue(String.class));
                        ArrayAdapter<String> ara = new ArrayAdapter<>(UpdateAndDelete_Beacon.this,R.layout.stylespinner,listfloor);
                        main.setAdapter(ara);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showlistrm_left() {
        condb.child(build_id).child("Build").child(floor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listlbuild.clear();
                if (rm_l==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listlbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(UpdateAndDelete_Beacon.this, R.layout.stylespinner, listlbuild);
                    left.setAdapter(ara);
                }else {
                    listlbuild.add(rm_l);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (rm_l.equals(item.child("name").getValue().toString())){

                        }else {
                            listlbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(UpdateAndDelete_Beacon.this,R.layout.stylespinner,listlbuild);
                    left.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_right() {
        condb.child(build_id).child("Build").child(floor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listrbuild.clear();
                if (rm_r==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listrbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(UpdateAndDelete_Beacon.this, R.layout.stylespinner, listrbuild);
                    right.setAdapter(ara);
                }else {
                    listrbuild.add(rm_r);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (rm_r.equals(item.child("name").getValue().toString())){

                        }else {
                            listrbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(UpdateAndDelete_Beacon.this,R.layout.stylespinner,listrbuild);
                    right.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_front() {
        condb.child(build_id).child("Build").child(floor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listfbuild.clear();
                if (rm_f==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listfbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(UpdateAndDelete_Beacon.this, R.layout.stylespinner, listfbuild);
                    front.setAdapter(ara);
                }else {
                    listfbuild.add(rm_f);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (rm_f.equals(item.child("name").getValue().toString())){

                        }else {
                            listfbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(UpdateAndDelete_Beacon.this,R.layout.stylespinner,listfbuild);
                    front.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void showlistrm_back() {
        condb.child(build_id).child("Build").child(floor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listbbuild.clear();
                if (rm_b==null) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listbbuild.add(item.child("name").getValue(String.class));
                    }
                    ArrayAdapter<String> ara = new ArrayAdapter<>(UpdateAndDelete_Beacon.this, R.layout.stylespinner, listbbuild);
                    back.setAdapter(ara);
                }else {
                    listbbuild.add(rm_b);
                    for(DataSnapshot item : snapshot.getChildren()){
                        if (rm_b.equals(item.child("name").getValue().toString())){

                        }else {
                            listbbuild.add(item.child("name").getValue(String.class));
                        }
                    }
                    ArrayAdapter<String> aras = new ArrayAdapter<>(UpdateAndDelete_Beacon.this,R.layout.stylespinner,listbbuild);
                    back.setAdapter(aras);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void onBackPressed(){ }
}