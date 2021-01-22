package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main_Sub_Building_Setting extends AppCompatActivity {
    private ImageView addbeacon, listrm_ele, addroom, building;
    public String build_id,build_name;
    Button back;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sub_buuilding_setting);


        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);


        System.out.println("????????????????????????????   Main ACtivity  "+build_id);

        getSupportActionBar().setTitle(build_id);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back = findViewById(R.id.all_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Sub_Building_Setting.this, Main_Building_Setting.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        addbeacon = findViewById(R.id.btn_addbeacon);
        addbeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Sub_Building_Setting.this, ScanBeacon.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.putExtra("build_id",build_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



        addroom = findViewById(R.id.btn_addroom);
        addroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Sub_Building_Setting.this, Add_Room.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.putExtra("build_id",build_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        building = findViewById(R.id.btn_building);
        building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Sub_Building_Setting.this, UpdateAndDelete_Building.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.putExtra("build_id",build_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        listrm_ele = findViewById(R.id.btn_list_rm_ele);
        listrm_ele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Sub_Building_Setting.this, List.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.putExtra("build_id",build_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }
    public void onBackPressed(){ }
}
