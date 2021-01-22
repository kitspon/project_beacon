package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateAndDelete_Building extends AppCompatActivity {
    Handler delay = new Handler();
    public String build_id,confirm_id;
    EditText building_id,building_name,la,lng,about;
    TextView floor;
    ImageView img;
    Button btn_update,btn_delete,btn_back;
    DatabaseReference delete,database,old,newc;
    Information_building building_data = new Information_building();
    Uri imguri;
    StorageReference firestorage;
    public String Url;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_and_delete__building);

        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);
        System.out.println("????????????????????? UPDATEBUILDING"+ build_id +" RL "+Url);
        Querydata();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        img = findViewById(R.id.update_bui_img);
        building_id = findViewById(R.id.update_bui_id);
        building_name = findViewById(R.id.update_bui_name);
        floor = findViewById(R.id.update_Floors);
        la = findViewById(R.id.update_Latitude);
        lng = findViewById(R.id.update_Longitude);
        about = findViewById(R.id.update_Information);

        btn_back = findViewById(R.id.update_back_btn);
        btn_delete = findViewById(R.id.update_dl_btn);
        btn_update = findViewById(R.id.update_up_btn);

        database = FirebaseDatabase.getInstance().getReference("Building");
        firestorage = FirebaseStorage.getInstance().getReference().child("ImgBuilding");

        Query imgbuilding = FirebaseDatabase.getInstance().getReference("Building").orderByChild("building_id").equalTo(build_id);
        imgbuilding.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    System.out.println("YES");
                    for (DataSnapshot item: snapshot.getChildren()){
                        Url = item.child("building_img").getValue().toString();
                        System.out.println(Url);
                    }
                }else{
                    System.out.println("NO");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateAndDelete_Building.this, Main_Sub_Building_Setting.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAndDelete_Building.this);
                builder.setCancelable(true);
                builder.setTitle("DELETE BUILDING");
                builder.setMessage("Do you want to delete building?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Query query = FirebaseDatabase.getInstance().getReference().child("Building").orderByChild("building_id").equalTo(build_id);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot item : snapshot.getChildren()){
                                            delete = FirebaseDatabase.getInstance().getReference().child("Building").child(item.child("building_id").getValue().toString());
                                            delete.setValue(null);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                delete = FirebaseDatabase.getInstance().getReference().child(build_id);
                                delete.setValue(null);
                                Intent intent = new Intent(UpdateAndDelete_Building.this, Main_Building_Setting.class);
                                intent.putExtra("status",status);
                                intent.putExtra("userid",userid);
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

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 old = FirebaseDatabase.getInstance().getReference(build_id);
                 newc = FirebaseDatabase.getInstance().getReference(building_id.getText().toString());
                copyRecord(old,newc);

                Query e = FirebaseDatabase.getInstance().getReference("Building").child(build_id);
                e.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        building_data.setBuilding_id(building_id.getText().toString());
                        building_data.setBuilding_name(building_name.getText().toString());
                        building_data.setLatitude(la.getText().toString());
                        building_data.setBuilding_img(Url);
                        building_data.setLongtitude(lng.getText().toString());
                        building_data.setInformation(about.getText().toString());
                        building_data.setFloors(floor.getText().toString());
                        database.child(building_id.getText().toString()).setValue(building_data);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                delay.postDelayed(change_id,2000);



            }
        });
    }

    private Runnable change_id = new Runnable() {
        @Override
        public void run() {
            Query bc = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Beacon");
            bc.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String parth = item.child("name").getValue().toString();
                        System.out.print("*****" + parth);

                        if (item.child("build_id").getValue().equals(build_id)) {
                            database = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Beacon").child(parth).child("build_id");
                            database.setValue(building_id.getText().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Query ele = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Elevator");
            ele.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String parth = item.child("name").getValue().toString();
                        System.out.print("*****" + parth);

                        if (item.child("building_id").getValue().equals(build_id)) {
                            database = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Elevator").child(parth).child("building_id");
                            database.setValue(building_id.getText().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Query rm = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Room");
            rm.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        String parth = item.child("name").getValue().toString();
                        System.out.print("*****" + parth);

                        if (item.child("building_id").getValue().equals(build_id)) {
                            database = FirebaseDatabase.getInstance().getReference(building_id.getText().toString()).child("Room").child(parth).child("building_id");
                            database.setValue(building_id.getText().toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (!building_id.getText().toString().equals(build_id)){
                DatabaseReference deletein = FirebaseDatabase.getInstance().getReference("Building").child(build_id);
                deletein.setValue(null);
                DatabaseReference deleteout = FirebaseDatabase.getInstance().getReference(build_id);
                deleteout.setValue(null);
            }
            Toast.makeText(UpdateAndDelete_Building.this, "Update Success!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateAndDelete_Building.this,Main_Building_Setting.class);
            intent.putExtra("status",status);
            intent.putExtra("userid",userid);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            delay.postDelayed(asdasd,1000);
        }
    };

    private Runnable asdasd = new Runnable() {
        @Override
        public void run() {


        }
    };


    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imguri = data.getData();
            Picasso.get().load(imguri).into(img);

            final StorageReference imagename = firestorage.child(imguri.getLastPathSegment());
            imagename.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Url = String.valueOf(uri);
                        }
                    });
                }
            });
        }
    }

    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            System.out.println("Success");
                        } else {
                            System.out.println("Fail");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }


    private void Querydata() {
        Query query = FirebaseDatabase.getInstance().getReference("Building").orderByChild("building_id").equalTo(build_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item: snapshot.getChildren()){
                    if (item.child("building_img").getValue().toString().equals(null)){
                    }else{
                        Picasso.get().load(item.child("building_img").getValue().toString()).into(img);
                    }
                    building_id.setText(item.child("building_id").getValue().toString());
                    building_name.setText(item.child("building_name").getValue().toString());
                    floor.setText(item.child("floors").getValue().toString());
                    if (item.child("latitude").getValue().toString().equals(null)){

                    }else{
                        la.setText(item.child("latitude").getValue().toString());
                    }
                    if (item.child("longtitude").getValue().toString().equals(null)){

                    }else{
                        lng.setText(item.child("longtitude").getValue().toString());
                    }
                    if (item.child("information").getValue().toString().equals(null)){

                    }else{
                        about.setText(item.child("information").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onBackPressed(){ }
}