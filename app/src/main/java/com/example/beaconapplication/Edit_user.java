package com.example.beaconapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Edit_user extends AppCompatActivity {
    EditText pass,name;
    TextView user;
    Button regis_btn,back_btn;
    DatabaseReference condb;
    String userid,status,tname,tpass;
    Information_User infouser = new Information_User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        tpass = getIntent().getStringExtra("name");
        tname = getIntent().getStringExtra("pass");

        user = findViewById(R.id.edit_user_id);
        pass = findViewById(R.id.edit_cardnumber_id);
        name = findViewById(R.id.edit_name_use);
        regis_btn = findViewById(R.id.edit_con_btn);
        back_btn = findViewById(R.id.edit_back_btn);

        user.setText(userid);
        pass.setText(tpass);
        name.setText(tname);

        condb = FirebaseDatabase.getInstance().getReference("User");


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Edit_user.this, Main_USer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getText().toString().equals("")) {
                    Toast.makeText(Edit_user.this, "Please enter UserID", Toast.LENGTH_LONG).show();
                } else if (pass.getText().toString().equals("")) {
                    Toast.makeText(Edit_user.this, "Please enter Password", Toast.LENGTH_LONG).show();
                } else if (name.getText().toString().equals("")) {
                    Toast.makeText(Edit_user.this, "Please enter Name", Toast.LENGTH_LONG).show();
                }else {
                    condb = FirebaseDatabase.getInstance().getReference("User").child(userid).child("name");
                    condb.setValue(name.getText().toString());

                    condb = FirebaseDatabase.getInstance().getReference("User").child(userid).child("pass");
                    condb.setValue(pass.getText().toString());
                    Toast.makeText(Edit_user.this, "Edited done", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Edit_user.this, Main_USer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    startActivity(intent);
                }
            }

        });
    }

    public void onBackPressed(){}

}