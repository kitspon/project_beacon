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

public class UpdateAndDelete_Room_Elevator extends AppCompatActivity {
    private Handler delay = new Handler();
    ImageView imageView;
    EditText tname;
    TextView tfloor;
    Button btn_update,btn_delete,btn_back;
    private String name,floor,build_id,img;
    public String type,getfloor,getKeyroom,Tchecktype,newimg,parth;
    DatabaseReference database, old_allroom,old_bea,old_rou;
    Uri imguri;
    StorageReference firestorage;
    static String status,userid,dir,firs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_and_delete_room_ele);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        img = getIntent().getStringExtra("img");
        build_id = getIntent().getStringExtra("build_id");
        name = getIntent().getStringExtra("name");
        floor = getIntent().getStringExtra("floor");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        firestorage = FirebaseStorage.getInstance().getReference().child("ImgBuilding");
        System.out.println("????????????????????????????? UpdateANDDelete "+ build_id+"   img "+img+" name"+name+" floor"+floor);

        tname = findViewById(R.id.ttext_u_d_name);
        tname.setText(name);
        tfloor = findViewById(R.id.ttext_u_d_floor);
        tfloor.setText("ชั้น : "+floor);
        imageView = findViewById(R.id.ttext_u_d_img);
        Picasso.get().load(img).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefileimage();
            }
        });

        btn_update = findViewById(R.id.ud_update);
        btn_delete = findViewById(R.id.ud_delete);
        btn_back = findViewById(R.id.ud_update_back);

        Querytype();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateAndDelete_Room_Elevator.this, ListRoomAndElevator.class);
                intent.putExtra("type",type);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("asdasdsada" + newimg);
                if (tname.getText().toString().equals("")) {
                    Toast.makeText(UpdateAndDelete_Room_Elevator.this, "Please enter name", Toast.LENGTH_LONG).show();
                } else {
                    if (newimg == null) {
                        newimg = img;
                    } else {
                        System.out.println(newimg);
                    }

//                    TCheckname = "find";
//
//                    System.out.println("เจออออออออออออออออออออออออออออ");
//                    Query Checkname = FirebaseDatabase.getInstance().getReference().child(build_id).child(Tchecktype).orderByChild("name").equalTo(tname.getText().toString());
//                    Checkname.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.getChildrenCount()>0){
//                                TCheckname = "find";
//                            }else {TCheckname = "null";
//                                newimg=img;}
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) { }
//                    });
//                }else{
//                    TCheckname = "null";
//                    newimg=img;
//                }

                    delay.postDelayed(update, 500);
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAndDelete_Room_Elevator.this);
                builder.setCancelable(true);
                builder.setTitle("DELETE");
                builder.setMessage("Do you want to delete?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (type.equals("Room")) {
                                    database = FirebaseDatabase.getInstance().getReference(build_id).child("Room").child(getKeyroom);
                                    database.setValue(null);

                                    database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(getKeyroom);
                                    database.setValue(null);

                                    database = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+getKeyroom);
                                    database.setValue(null);

                                    Intent intent = new Intent(UpdateAndDelete_Room_Elevator.this, ListRoomAndElevator.class);
                                    intent.putExtra("type",type);
                                    intent.putExtra("build_id",build_id);
                                    intent.putExtra("status",status);
                                    intent.putExtra("userid",userid);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                if (type.equals("Elevator")) {
                                    database = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").child(getKeyroom);
                                    database.setValue(null);

                                    database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(getKeyroom);
                                    database.setValue(null);

                                    database = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+getKeyroom);
                                    database.setValue(null);

                                    Intent intent = new Intent(UpdateAndDelete_Room_Elevator.this, ListRoomAndElevator.class);
                                    intent.putExtra("type",type);
                                    intent.putExtra("build_id",build_id);
                                    intent.putExtra("status",status);
                                    intent.putExtra("userid",userid);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
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
    }

    private void choosefileimage() {
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
            Picasso.get().load(imguri).into(imageView);

            final StorageReference imagename = firestorage.child(imguri.getLastPathSegment());
            imagename.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                             newimg = String.valueOf(uri);
                        }
                    });
                }
            });
        }
    }

    private Runnable update = new Runnable() {
        @Override
        public void run() {

//            if (TCheckname.equals("find")) {
//                Toast.makeText(UpdateAndDelete.this,"ห้องนี้มีอยู่แล้ว",Toast.LENGTH_LONG).show();
//            } else {
                if (type.equals("Room")) {

                     old_allroom = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+getKeyroom);
                    DatabaseReference new_allroom = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+tname.getText().toString());
                    copyRecord(old_allroom,new_allroom);

                     old_bea = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(getKeyroom);
                    DatabaseReference new_bea = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(tname.getText().toString());
                    copyRecord(old_bea,new_bea);





                    DatabaseReference old = FirebaseDatabase.getInstance().getReference(build_id).child("Room").child(getKeyroom);
                    DatabaseReference newc = FirebaseDatabase.getInstance().getReference(build_id).child("Room").child(tname.getText().toString());
                    copyRecord(old,newc);

                    old.setValue(null);

                    DatabaseReference old_build = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(getKeyroom);
                    DatabaseReference new_build = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString());
                    copyRecord(old_build,new_build);

                    old_build.setValue(null);



                    delay.postDelayed(qwe,1500);

                    Intent intent = new Intent(UpdateAndDelete_Room_Elevator.this, ListRoomAndElevator.class);
                    intent.putExtra("type",type);
                    intent.putExtra("build_id",build_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                if (type.equals("Elevator")) {
                    old_allroom = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+getKeyroom);
                    DatabaseReference new_allroom = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+tname.getText().toString());
                    copyRecord(old_allroom,new_allroom);

                    old_bea = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(getKeyroom);
                    DatabaseReference new_bea = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(tname.getText().toString());
                    copyRecord(old_bea,new_bea);

                    DatabaseReference old = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").child(getKeyroom);
                    DatabaseReference newc = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").child(tname.getText().toString());
                    copyRecord(old,newc);

                    old.setValue(null);

                    DatabaseReference old_build = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(getKeyroom);
                    DatabaseReference new_build = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString());
                    copyRecord(old_build,new_build);

                    old_build.setValue(null);


                    delay.postDelayed(qwe2,1500);

                    Intent intent = new Intent(UpdateAndDelete_Room_Elevator.this, ListRoomAndElevator.class);
                    intent.putExtra("type",type);
                    intent.putExtra("build_id",build_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
            }

    };

    private Runnable qwe = new Runnable() {
        @Override
        public void run() {

            old_allroom.setValue(null);
            old_bea.setValue(null);

//            Query route = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("first").equalTo(getKeyroom);
//            route.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    int i=1;
//                    String p= String.valueOf(i);
//                    for (DataSnapshot item : snapshot.getChildren()){
//                        System.out.println("มาถึงแล้วววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววว");
//                        dir = item.child("direction").getValue().toString();
//                        firs = item.child("first").getValue().toString();
//
//                        DatabaseReference old_roup = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(getKeyroom+dir);
//                        DatabaseReference new_rou = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(tname.getText().toString()+dir);
//                        copyRecord(old_roup,new_rou);
//
//
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });



//            delay.postDelayed(wiat,1200);



            database = FirebaseDatabase.getInstance().getReference(build_id).child("Room").child(tname.getText().toString()).child("name");
            database.setValue(tname.getText().toString());

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Room").child(tname.getText().toString()).child("image");
            database.setValue(newimg);

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString()).child("name");
            database.setValue(tname.getText().toString());

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString()).child("image");
            database.setValue(newimg);

            database = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+tname.getText().toString()).child("name");
            database.setValue(tname.getText().toString());

            database = FirebaseDatabase.getInstance().getReference("AllRoom").child(build_id+tname.getText().toString()).child("image");
            database.setValue(newimg);

            Query All = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon");
            All.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()){
                        parth = item.child("name").getValue().toString();
                        System.out.print("*****"+parth);

                        if (item.child("name").getValue()==null){
                        }else{
                            String n = item.child("name").getValue().toString();
                            System.out.print(" - "+n);
                            if (n.equals(getKeyroom)){

                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(tname.getText().toString()).child("name");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_L").getValue()==null){
                        }else{
                            String l = item.child("rm_L").getValue().toString();
                            System.out.print(" - "+l);
                            if (l.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_L");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_R").getValue()==null){
                        }else{
                            String r = item.child("rm_R").getValue().toString();
                            System.out.println(" - "+r);
                            if (r.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_R");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_F").getValue()==null){
                        }else{
                            String f = item.child("rm_F").getValue().toString();
                            System.out.println(" - "+f);
                            if (f.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_F");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_B").getValue()==null){
                        }else{
                            String b = item.child("rm_B").getValue().toString();
                            System.out.println(" - "+b);
                            if (b.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_B");
                                database.setValue(tname.getText().toString());
                            }
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            delay.postDelayed(delete,1000);
        }
    };

    private Runnable qwe2 = new Runnable() {
        @Override
        public void run() {

            old_allroom.setValue(null);
            old_bea.setValue(null);

//            Query route = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("first").equalTo(getKeyroom);
//            route.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot item : snapshot.getChildren()){
//                        System.out.println("มาถึงแล้วววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววว");
//                        dir = item.child("direction").getValue().toString();
//                        firs = item.child("first").getValue().toString();
//                        System.out.println("asdwqdsadqwdsadsadqwdsadwqdsadwqdsad"+firs+dir);
//
//                        old_rou = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(getKeyroom+dir);
//                        DatabaseReference new_rou = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(tname.getText().toString()+dir);
//                        copyRecord(old_rou,new_rou);
//
//                        old_rou.setValue(null);
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

            //delay.postDelayed(wiat,2000);

//            Query routee = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(getKeyroom);
//            routee.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot item : snapshot.getChildren()){
//                        System.out.println("มาถึงแล้วววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววว");
//                        String dir = item.child("direction").getValue().toString();
//                        String firs = item.child("first").getValue().toString();
//
//                        database = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(firs+dir).child("end");
//                        database.setValue(tname.getText().toString());
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });




            database = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").child(tname.getText().toString()).child("name");
            database.setValue(tname.getText().toString());

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").child(tname.getText().toString()).child("image");
            database.setValue(newimg);

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString()).child("name");
            database.setValue(tname.getText().toString());

            database = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(getfloor).child(tname.getText().toString()).child("image");
            database.setValue(newimg);

            Query All = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon");
            All.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()){
                        parth = item.child("name").getValue().toString();
                        System.out.print("*****"+parth);

                        if (item.child("name").getValue()==null){
                        }else{
                            String n = item.child("name").getValue().toString();
                            System.out.print(" - "+n);
                            if (n.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(tname.getText().toString()).child("name");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_L").getValue()==null){
                        }else{
                            String l = item.child("rm_L").getValue().toString();
                            System.out.print(" - "+l);
                            if (l.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_L");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_R").getValue()==null){
                        }else{
                            String r = item.child("rm_R").getValue().toString();
                            System.out.println(" - "+r);
                            if (r.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_R");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_F").getValue()==null){
                        }else{
                            String f = item.child("rm_F").getValue().toString();
                            System.out.println(" - "+f);
                            if (f.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_F");
                                database.setValue(tname.getText().toString());
                            }
                        }
                        if (item.child("rm_B").getValue()==null){
                        }else{
                            String b = item.child("rm_B").getValue().toString();
                            System.out.println(" - "+b);
                            if (b.equals(getKeyroom)){
                                database = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon").child(parth).child("rm_B");
                                database.setValue(tname.getText().toString());
                            }
                        }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });



            delay.postDelayed(delete,1000);
        }
    };
    private Runnable wiat = new Runnable() {
        @Override
        public void run() {
            Query route = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("first").equalTo(tname.getText().toString());
            route.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i=1;
                    String p= String.valueOf(i);
                    for (DataSnapshot item : snapshot.getChildren()){
                        System.out.println("มาถึงแล้วววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววว");
                        dir = item.child("direction").getValue().toString();
                        firs = item.child("first").getValue().toString();

                        database = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(tname.getText().toString()+dir).child("first");
                        database.setValue(tname.getText().toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    };
    private Runnable delete = new Runnable() {
        @Override
        public void run() {
//            Query routee = FirebaseDatabase.getInstance().getReference(build_id).child("Route").orderByChild("end").equalTo(getKeyroom);
//            routee.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot item : snapshot.getChildren()){
//                        System.out.println("มาถึงแล้วววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววววว");
//                        String dir = item.child("direction").getValue().toString();
//                        String firs = item.child("first").getValue().toString();
//
//                        database = FirebaseDatabase.getInstance().getReference(build_id).child("Route").child(firs+dir).child("end");
//                        database.setValue(tname.getText().toString());
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });


        }
    };

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

    private void Querytype() {
        Query settyperoom = FirebaseDatabase.getInstance().getReference().child(build_id).child("Room").orderByChild("name").equalTo(name);
        settyperoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){ type = "Room";
                    for (DataSnapshot item : snapshot.getChildren()){
                        getfloor = (String)item.child("floor").getValue();
                        getKeyroom = item.getKey();
                        Tchecktype = "Room";
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        Query settypeelevator = FirebaseDatabase.getInstance().getReference().child(build_id).child("Elevator").orderByChild("name").equalTo(name);
        settypeelevator.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                    type = "Elevator";
                    for (DataSnapshot item : snapshot.getChildren()){
                        getfloor = (String)item.child("floor").getValue();
                        getKeyroom = item.getKey();
                        Tchecktype = "Elevator";
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