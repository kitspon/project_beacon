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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Main_Create_Building extends AppCompatActivity {
    EditText bui_id,bui_name,Lat,Lng,ifm,floors;
    ImageView bui_imge;
    Button btn_add_bui;
    DatabaseReference database,databasebuild;
    Information_building building_data = new Information_building();
    Uri imguri;
    Uri reimg = null;
    StorageReference firestorage;
    Handler delay = new Handler();
    ArrayList<String> cfloors = new ArrayList<>();
    Information_floors information_floors = new Information_floors();
    Button btn_back;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_create_building);

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        bui_imge = findViewById(R.id.add_bui_img);
        bui_id = findViewById(R.id.add_bui_id);
        bui_name = findViewById(R.id.add_bui_name);
        btn_add_bui = findViewById(R.id.add_create_btn);
        Lat = findViewById(R.id.Latitude);
        Lng = findViewById(R.id.Longitude);
        ifm = findViewById(R.id.Information);
        floors = findViewById(R.id.Floors);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btn_back = findViewById(R.id.creatbuild_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Create_Building.this, Main_Building_Setting.class);
                status = getIntent().getStringExtra("status");
                userid = getIntent().getStringExtra("userid");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance().getReference("Building");
        firestorage = FirebaseStorage.getInstance().getReference().child("ImgBuilding");

        bui_imge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefileimage();
            }
        });

        btn_add_bui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (imguri==null){
                        Toast.makeText(Main_Create_Building.this, "Please Select img", Toast.LENGTH_LONG).show();
                    }else if (bui_id.getText().toString().equals("")){
                        Toast.makeText(Main_Create_Building.this, "Please enter Building ID", Toast.LENGTH_LONG).show();
                    }else if(bui_name.getText().toString().equals("")){
                            Toast.makeText(Main_Create_Building.this, "Please enter Building Name", Toast.LENGTH_LONG).show();
                    }else if(floors.getText().toString().equals("")){
                        Toast.makeText(Main_Create_Building.this, "Please enter Number of floors", Toast.LENGTH_LONG).show();
                    }else if(Integer.parseInt(floors.getText().toString())>100) {
                        Toast.makeText(Main_Create_Building.this, "The Floors that can be set is exceeded", Toast.LENGTH_LONG).show();
                    }else{
                            Upload();
                            CreateFloors();
                    }
            }
        });
    }

    private void CreateFloors() {
        databasebuild = FirebaseDatabase.getInstance().getReference(bui_id.getText().toString()).child("Floors");
//        databasebuild.child("Floors").setValue(null);

        int C = Integer.parseInt(floors.getText().toString());

        for (int i=0 ; i<(C);i++){
            information_floors.setLevel(String.valueOf(i+1));
            databasebuild.child(String.valueOf(i+1)).setValue(information_floors);
        }
    }

    private void Upload() {
        Query Checkbuilding = FirebaseDatabase.getInstance().getReference("Building").orderByChild("building_id").equalTo(bui_id.getText().toString());
        Checkbuilding.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0){
                    building_data.setBuilding_id(bui_id.getText().toString());
                    building_data.setBuilding_name(bui_name.getText().toString());
                    building_data.setLatitude(Lat.getText().toString());
                    building_data.setLongtitude(Lng.getText().toString());
                    building_data.setInformation(ifm.getText().toString());
                    building_data.setFloors(floors.getText().toString());
                    database.child(bui_id.getText().toString()).setValue(building_data);
                    bui_id.getText().clear();
                    bui_name.getText().clear();
                    floors.getText().clear();
                    Lat.getText().clear();
                    Lng.getText().clear();
                    ifm.getText().clear();
                    Picasso.get().load(reimg).into(bui_imge);
                    Toast.makeText(Main_Create_Building.this, "Successfully added information", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Main_Create_Building.this, "This Building ID has already been used", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                Picasso.get().load(imguri).into(bui_imge);

                final StorageReference imagename = firestorage.child(imguri.getLastPathSegment());
                imagename.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                building_data.setBuilding_img(String.valueOf(uri));
                            }
                        });
                    }
                });
            }
    }

    public void onBackPressed(){ }
}