package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

public class Main_Register extends AppCompatActivity {
    EditText user,pass,name;
    Button regis_btn,back_btn;
    DatabaseReference condb;
    Information_User infouser = new Information_User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        user = findViewById(R.id.regis_user_id);
        pass = findViewById(R.id.regis_cardnumber_id);
        name = findViewById(R.id.regis_name_use);
        regis_btn = findViewById(R.id.regis_con_btn);
        back_btn = findViewById(R.id.regis_back_btn);

        condb = FirebaseDatabase.getInstance().getReference("User");


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Register.this, Main_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getText().toString().equals("")) {
                    Toast.makeText(Main_Register.this, "Please enter UserID", Toast.LENGTH_LONG).show();
                } else if (pass.getText().toString().equals("")) {
                    Toast.makeText(Main_Register.this, "Please enter Password", Toast.LENGTH_LONG).show();
                } else if (name.getText().toString().equals("")) {
                    Toast.makeText(Main_Register.this, "Please enter Name", Toast.LENGTH_LONG).show();
                }else {
                    regis();
                }
            }

        });
    }

    private void regis(){
        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("userid").equalTo(user.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0){
                    infouser.setUserid(user.getText().toString());
                    infouser.setPass(pass.getText().toString());
                    infouser.setName(name.getText().toString());
                    infouser.setStatus("user");
                    condb.child(user.getText().toString()).setValue(infouser);
                    Toast.makeText(Main_Register.this, "Successfully added information", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Main_Register.this,Main_Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(Main_Register.this, "This UserID has already been used", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Register.this);
        builder.setMessage("Are you sure you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Main_Register.super.onBackPressed();
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