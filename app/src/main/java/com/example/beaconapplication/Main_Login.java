package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main_Login extends AppCompatActivity {
    EditText user,pass;
    Button con_btn,regis_btn;
    DatabaseReference condb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        user = findViewById(R.id.user_id);
        pass = findViewById(R.id.cardnumber_id);
        con_btn = findViewById(R.id.con_btn);
        regis_btn = findViewById(R.id.regis_btn);

        condb = FirebaseDatabase.getInstance().getReference("User");
        con_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query queryadmin = FirebaseDatabase.getInstance().getReference("User").orderByChild("userid").equalTo(user.getText().toString());
                queryadmin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            System.out.println("have");
                            for (DataSnapshot item : snapshot.getChildren()){
                            if (pass.getText().toString().equals(item.child("pass").getValue().toString())){
                                String status = item.child("status").getValue().toString();
                                String iduser = item.child("userid").getValue().toString();
                                Intent intent = new Intent(Main_Login.this,Main_Navigation.class);
                                intent.putExtra("status",status);
                                intent.putExtra("userid",iduser);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(Main_Login.this, "Logging in.", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(Main_Login.this, "Password incorrect", Toast.LENGTH_LONG).show();
                            }
                            }
                        }else{
                            System.out.println("don't have");
                            Toast.makeText(Main_Login.this, "There is no user on the system.", Toast.LENGTH_LONG).show();
                            }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Login.this,Main_Register.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Login.this);
        builder.setMessage("Are you sure you want to Exit?")
        .setCancelable(false)
        .setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Main_Login.super.onBackPressed();
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
}