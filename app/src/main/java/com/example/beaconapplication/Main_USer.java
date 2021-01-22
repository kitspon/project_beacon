package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main_USer extends AppCompatActivity {
    TextView name,id,pass,stat;
    Button logout,edit;
    static String status,userid,tname,tpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__u_ser);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        name  = findViewById(R.id.name_user);
        id = findViewById(R.id.id_user);
        pass = findViewById(R.id.pass_user);
        stat = findViewById(R.id.status_user);
        logout = findViewById(R.id.logout_user);
        edit = findViewById(R.id.edit_user);
        FQueryA();
        FQueryU();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_USer.this);
                builder.setCancelable(true);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to Logout?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(),Main_Login.class);
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
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Edit_user.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.putExtra("name",tname);
                intent.putExtra("pass",tpass);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_user);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_navigation:
                        Intent intent = new Intent(getApplicationContext(),Main_Navigation.class);
                        intent.putExtra("status",status);
                        intent.putExtra("userid",userid);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    case R.id.menu_setting:
                        if (status.equals("admin")) {
                            Intent intent2 = new Intent(getApplicationContext(), Main_Building_Setting.class);
                            intent2.putExtra("status", status);
                            intent2.putExtra("userid", userid);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            overridePendingTransition(0, 0);
                            return true;
                        }else{
                            Toast.makeText(Main_USer.this,"This function is for admin",Toast.LENGTH_LONG).show();
                            return false;
                        }
                    case R.id.menu_user:
                        return true;
                }
                return false;
            }
        });
    }

    private void FQueryA(){
        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("userid").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    name.setText(item.child("name").getValue().toString());
                    id.setText(item.child("userid").getValue().toString());
                    pass.setText(item.child("pass").getValue().toString());
                    stat.setText(item.child("status").getValue().toString());

                    tname = item.child("name").getValue().toString();
                    tpass = item.child("pass").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FQueryU(){
        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("userid").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()){
                    name.setText(item.child("name").getValue().toString());
                    id.setText(item.child("userid").getValue().toString());
                    pass.setText(item.child("pass").getValue().toString());
                    stat.setText(item.child("status").getValue().toString());

                    tname = item.child("name").getValue().toString();
                    tpass = item.child("pass").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //กดกลับแล้วไม่เกิดอะไร
    public void onBackPressed(){ }
}