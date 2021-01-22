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
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Select_Add_Type_Beacon extends AppCompatActivity {
    public static final String EXTRA_ADDRESS = "android.aviles.bletutorial.Activity_BTLE_Services.ADDRESS";
    private Button set,btn_back;
    private String address;
    private DatabaseReference confloor = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> listfloors = new ArrayList<>();
    private Spinner spinf,spintype;
    private ArrayAdapter<String> arrayAdapter;
    private String type[] = {"room","elevator"};
    public String Countfloors;
    //"staircase"
    String status,userid;
    String build_id,la,lng,build_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_add_type_beacon);

        getSupportActionBar().setTitle("Select to SetBeaCon");
        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        address = intent.getStringExtra("putAddress");

        System.out.println("????????????????????????????????????? Setbeacon "+ build_id + " Address "+address);
        spinf = findViewById(R.id.spin_select);
        spintype = findViewById(R.id.spinner);
        showlistfloors();

        ArrayAdapter < String > dataAdapter = new ArrayAdapter < String > ( Select_Add_Type_Beacon.this,R.layout.stylespinner, type );
        Spinner spinner = ( Spinner ) this.findViewById ( R.id.spinner );

        spinner.setAdapter ( dataAdapter );


        set = findViewById(R.id.Set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ค-----------ุณได้เลือก"+spinner.getSelectedItem().toString());
                if (spinner.getSelectedItem().toString().equals("room")){
                    Intent intent = new Intent(Select_Add_Type_Beacon.this, Beacon_AddRoom.class);
                    intent.putExtra("address",address);
                    intent.putExtra("type",spintype.getSelectedItem().toString());
                    intent.putExtra("floor",spinf.getSelectedItem().toString());
                    intent.putExtra("build_id",build_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else if (spinner.getSelectedItem().toString().equals("elevator")){
                    Intent intent = new Intent(Select_Add_Type_Beacon.this, Beacon_AddElevator.class);
                    intent.putExtra("address",address);
                    intent.putExtra("type",spintype.getSelectedItem().toString());
                    intent.putExtra("floor",spinf.getSelectedItem().toString());
                    intent.putExtra("build_id",build_id);
                    intent.putExtra("countfloors",Countfloors);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

        btn_back = findViewById(R.id.select_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Select_Add_Type_Beacon.this, ScanBeacon.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    private void showlistfloors() {
        confloor.child(build_id).child("Floors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listfloors.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    listfloors.add(item.child("level").getValue(String.class));
                    Countfloors = item.child("level").getValue(String.class);
                }
                ArrayAdapter<String> ara = new ArrayAdapter<>(Select_Add_Type_Beacon.this,R.layout.stylespinner,listfloors);
                spinf.setAdapter(ara);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void onBackPressed(){ }
}