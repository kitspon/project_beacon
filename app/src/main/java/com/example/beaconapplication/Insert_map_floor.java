package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class Insert_map_floor extends AppCompatActivity {
    public String build_id,la,lng,build_name,texturi;
    private DatabaseReference condbfloors = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> listfloors = new ArrayList<>();
    private Spinner floor;
    private ImageView imageView;
    private Uri mImageUri;
    public Uri setimg = null;
    private StorageReference firestorage;
    private DatabaseReference databaseReference;
    private Button btn_setimg,btn_back;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_map_floor);

        build_id = getIntent().getStringExtra("build_id");
        build_name = getIntent().getStringExtra("build_name");
        la = getIntent().getStringExtra("latitude");
        lng = getIntent().getStringExtra("logtitude");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imageView = findViewById(R.id.img_map);
        floor = findViewById(R.id.spinner_map);
        showlistfloors();
        showlistdependentfloors();
        btn_setimg = findViewById(R.id.btn_set);
        btn_setimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference(build_id).child("Floors").child(floor.getSelectedItem().toString()).child("map");
                databaseReference.setValue(texturi);
                Toast.makeText(Insert_map_floor.this, "เพิ่มข้อมูลแล้ว", Toast.LENGTH_LONG).show();
            }
        });
        btn_back = findViewById(R.id.insertmap_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Insert_map_floor.this, Add_Room.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });



        System.out.println("");

        firestorage = FirebaseStorage.getInstance().getReference().child(build_id).child("ImageFolder");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefileimage();
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
                            texturi = String.valueOf(uri);
                        }
                    });
                }
            });
        }
    }

    private void showlistdependentfloors() {
        floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("You get :"+floor.getSelectedItem().toString());
                Query query = FirebaseDatabase.getInstance().getReference(build_id).child("Floors").orderByChild("level").equalTo(floor.getSelectedItem().toString());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0) {
                            System.out.println("มี");
                            for (DataSnapshot item : snapshot.getChildren()) {
                                if (item.child("map").getValue() == null) {
                                    Picasso.get().load(setimg).into(imageView);
                                } else {
                                    Picasso.get().load(item.child("map").getValue().toString()).into(imageView);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                }
                ArrayAdapter<String> ara = new ArrayAdapter<>(Insert_map_floor.this,R.layout.stylespinner,listfloors);
                floor.setAdapter(ara);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void onBackPressed(){ }
}