package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Main_Building_Setting extends AppCompatActivity {
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    String s_search = "";
    Button create;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_building_setting);

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Building");



        mResultList = (RecyclerView) findViewById(R.id.building_list_setting);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        create = findViewById(R.id.set_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_Building_Setting.this,Main_Create_Building.class);
                status = getIntent().getStringExtra("status");
                userid = getIntent().getStringExtra("userid");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        firebaseSearch(s_search);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_setting);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_navigation:
                            Intent intent2 = new Intent(getApplicationContext(),Main_Navigation.class);
                            intent2.putExtra("status",status);
                            intent2.putExtra("userid",userid);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            overridePendingTransition(0,0);
                            return true;
                    case R.id.menu_setting:
                        return true;
                    case R.id.menu_user:
                        Intent intent3 = new Intent(getApplicationContext(),Main_USer.class);
                        intent3.putExtra("status",status);
                        intent3.putExtra("userid",userid);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


    }

    private void firebaseSearch(String searchText) {

//        Toast.makeText(BuildingList_Setting.this, "Wait for searching..", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("building_name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Information_building, Building_SettingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Information_building, Building_SettingViewHolder>(

                Information_building.class,
                R.layout.item_listbuilding,
                Building_SettingViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(Building_SettingViewHolder viewHolder, Information_building model, int position) {

                viewHolder.Building_settingsetDetails(getApplicationContext(), model.getBuilding_name(), model.getBuilding_img(), model.getBuilding_id()
                        ,model.getLatitude(),model.getLongtitude());

            }
        };












        mResultList.setAdapter(firebaseRecyclerAdapter);


    }


    // View Holder Class

    public static class Building_SettingViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public Building_SettingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView build_id = (TextView) mView.findViewById(R.id.list_building_id);
                    TextView build_name = (TextView) mView.findViewById(R.id.list_building_name);
                    TextView latitue = (TextView) mView.findViewById(R.id.vlatitude);
                    TextView longtitude = (TextView) mView.findViewById(R.id.vlogtitude);
                    String set_id = build_id.getText().toString().trim();
                    String name = build_name.getText().toString().trim();
                    String la = latitue.getText().toString().trim();
                    String lng = longtitude.getText().toString().trim();

                    Intent intent = new Intent(mView.getContext(), Main_Sub_Building_Setting.class);
                    intent.putExtra("build_id", set_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mView.getContext().startActivity(intent);
                }
            });


        }

        public void Building_settingsetDetails(Context ctx, String name, String img, String id, String latitude, String longtitude) {

            TextView Name = (TextView) mView.findViewById(R.id.list_building_name);

            ImageView Image = (ImageView) mView.findViewById(R.id.list_building_img);
            TextView Id = (TextView) mView.findViewById(R.id.list_building_id);
            TextView La = (TextView) mView.findViewById(R.id.vlatitude);
            TextView Lo = (TextView) mView.findViewById(R.id.vlogtitude);
            Name.setText(name);
            Id.setText(id);
            La.setText(latitude);
            Lo.setText(longtitude);
            Glide.with(ctx).load(img).into(Image);

        }
    }

    //กดกลับแล้วไม่เกิดอะไร
    public void onBackPressed(){ }
}