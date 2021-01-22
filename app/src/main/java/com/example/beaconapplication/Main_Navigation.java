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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main_Navigation extends AppCompatActivity {
    ImageView room,build;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);
        room = findViewById(R.id.nv_romm);
        build = findViewById(R.id.nv_build);

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Navigation.this, ListAllRoom.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        build.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Navigation.this, ListBuilding_for_map.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_navigation:
                        return true;
                    case R.id.menu_setting:
                        if (status.equals("admin")){
                            Intent intent2 = new Intent(getApplicationContext(),Main_Building_Setting.class);
                            intent2.putExtra("status",status);
                            intent2.putExtra("userid",userid);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            overridePendingTransition(0,0);
                            return true;
                        }
                        if (status.equals("user")){
                            Toast.makeText(Main_Navigation.this,"This function is for admin",Toast.LENGTH_LONG).show();
                            return false;
                        }
                    case R.id.menu_user:
                            Intent intent3 = new Intent(getApplicationContext(),Main_USer.class);
                            intent3.putExtra("status",status);
                            intent3.putExtra("userid",userid);
                            intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent3);
                            overridePendingTransition(0,0);
                            return true;
                }
                return false;
            }
        });
    }

    //กดกลับแล้วไม่เกิดอะไร
    public void onBackPressed(){ }
}