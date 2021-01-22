package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;

public class Add_Room extends AppCompatActivity {
    private Handler delay = new Handler();
    private Button add,btn_create,btn_edit,btn_back;
    private EditText textname,count;
    private DatabaseReference condbroom,condballroom,condbbuild,condbelevator;
    private DatabaseReference condbfloors = FirebaseDatabase.getInstance().getReference();
    private Information_SetNF setNF;
    private Information_Build setBuild;
    private ArrayList<String> listfloors = new ArrayList<>();
    private ArrayList<String> listtype = new ArrayList<>();
    private Spinner floor,type;
    public String checkfloor;
    public String Showfloors;
    public  Uri setimg = null;
    private ImageView imageView;
    public StorageReference firestorage;
    private Uri mImageUri;
    public String build_id,la,lng,build_name;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room__name);

        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        getSupportActionBar().setTitle("Ceate");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //set
        setBuild = new Information_Build();
        setNF = new Information_SetNF();
        floor = findViewById(R.id.spin_floors);
        textname = findViewById(R.id.text_name);

        //dropdown floors
        showlistfloors();
        //dropdown type
        type = findViewById(R.id.spin_typeset);
        listtype.add("Room");
//        listtype.add("Stair");
        listtype.add("Elevator");
        ArrayAdapter<String> Adapttype = new ArrayAdapter<>(Add_Room.this,R.layout.stylespinner,listtype);
        type.setAdapter(Adapttype);

        //btn create
        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textname.getText().toString().equals("")){
                    Toast.makeText(Add_Room.this, "กรุณาใส่ชื่อ", Toast.LENGTH_LONG).show();
                }else if (type.getSelectedItem().toString().equals("Room")){
                    SearchRoom();
                }else if (type.getSelectedItem().toString().equals("Elevator")){
                    Searchelevator();
                }
            }
        });

        //btn_image
        imageView = findViewById(R.id.ar_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefileimage();
            }
        });

        //connect firebase
        condbroom = FirebaseDatabase.getInstance().getReference(build_id).child("Room");
        condballroom = FirebaseDatabase.getInstance().getReference("AllRoom");
//        condbstair = FirebaseDatabase.getInstance().getReference(build_id).child("Staircase");
        condbelevator = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator");
        firestorage = FirebaseStorage.getInstance().getReference().child(build_id).child("ImageFolder");

        btn_back = findViewById(R.id.create_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Add_Room.this, Main_Sub_Building_Setting.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



        add = findViewById(R.id.btn_addfloors);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Add_Room.this, Insert_map_floor.class);
                intent.putExtra("type",type.getSelectedItem().toString());
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(imageView);
            System.out.println("mImageUriiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii   "+mImageUri);

            final StorageReference imagename = firestorage.child(mImageUri.getLastPathSegment());
            imagename.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            setNF.setImage(String.valueOf(uri));
                            setBuild.setImage(String.valueOf(uri));
                        }
                    });
                }
            });
        }
    }


    private void Searchelevator() {
        String checkelevator = textname.getText().toString();
        InserNull();
        Query Name = FirebaseDatabase.getInstance().getReference(build_id).child("Elevator").orderByChild("name").equalTo(checkelevator);
        Name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    System.out.println("เจอ");
                    Toast.makeText(Add_Room.this, "ห้องนี้มีอยู่แล้ว", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("ไม่เจอ");
                    condbbuild = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(checkfloor);
                    setBuild.setName(textname.getText().toString());
                    condbbuild.child(textname.getText().toString()).setValue(setBuild);
                    setNF.setName(textname.getText().toString());
                    setNF.setBuilding_id(build_id);
                    setNF.setFloor(floor.getSelectedItem().toString());
                    condbelevator.child(textname.getText().toString()).setValue(setNF);
                    condballroom.child(build_id+textname.getText().toString()).setValue(setNF);
                    Toast.makeText(Add_Room.this, "เพิ่มห้องแล้ว", Toast.LENGTH_LONG).show();
                    textname.getText().clear();
                    Picasso.get().load(setimg).into(imageView);
                    floor.setSelection(0);
                    type.setSelection(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void SearchRoom() {
        String checknamerm = textname.getText().toString();
        InserNull();
        Query Name = FirebaseDatabase.getInstance().getReference(build_id).child("Room").orderByChild("name").equalTo(checknamerm);
        Name.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    System.out.println("เจอ");
                    Toast.makeText(Add_Room.this, "ห้องนี้มีอยู่แล้ว", Toast.LENGTH_LONG).show();
                } else {
                    condbbuild = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(checkfloor);
                    setBuild.setName(textname.getText().toString());
                    condbbuild.child(textname.getText().toString()).setValue(setBuild);
                    setNF.setName(textname.getText().toString());
                    setNF.setFloor(floor.getSelectedItem().toString());
                    setNF.setBuilding_id(build_id);
                    condbroom.child(textname.getText().toString()).setValue(setNF);
                    condballroom.child(build_id+textname.getText().toString()).setValue(setNF);
                    Toast.makeText(Add_Room.this, "เพิ่มห้องแล้ว", Toast.LENGTH_LONG).show();
                    textname.getText().clear();
                    Picasso.get().load(setimg).into(imageView);
                    floor.setSelection(0);
                    type.setSelection(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showlistfloors() {
        condbfloors.child(build_id).child("Floors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listfloors.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    listfloors.add(item.child("level").getValue(String.class));
                    Showfloors = (String) item.child("level").getValue();
                }
                ArrayAdapter<String> ara = new ArrayAdapter<>(Add_Room.this,R.layout.stylespinner,listfloors);
                floor.setAdapter(ara);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void InserNull() {
        checkfloor = floor.getSelectedItem().toString();
        Query checkbuild = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(checkfloor).orderByChild("name").equalTo("");
        checkbuild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                }else{
                    condbbuild = FirebaseDatabase.getInstance().getReference(build_id).child("Build").child(checkfloor);
                    Information_Build ss = new Information_Build();
                            ss.setName("");

                    condbbuild.child("0").setValue(ss);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void onBackPressed(){ }
}