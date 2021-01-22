package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListBuilding extends AppCompatActivity {
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    private EditText mSearchField;
    String s_search = "";
    Button btn_back;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_building);

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Building");

        System.out.println(userid+" "+status);

        mResultList = (RecyclerView) findViewById(R.id.building_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchField = (EditText)findViewById(R.id.search_field_indoor);
        btn_back = findViewById(R.id.indoorlist_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListBuilding.this, Main_Navigation.class);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        firebaseSearch(s_search);

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString()!=null){
                    firebaseSearch(editable.toString());
                }else{
                    firebaseSearch("");
                }
            }
        });


    }

    private void firebaseSearch(String searchText) {

//        Toast.makeText(BuildingList.this, "Wait for searching..", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = mUserDatabase.orderByChild("building_name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Information_building, BuildingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Information_building, BuildingViewHolder>(

                Information_building.class,
                R.layout.item_listbuilding,
                BuildingViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(BuildingViewHolder viewHolder, Information_building model, int position) {

                viewHolder.BuildingsetDetails(getApplicationContext(), model.getBuilding_name(), model.getBuilding_img(), model.getBuilding_id()
                ,model.getLatitude(),model.getLongtitude(),model.getInformation());

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);


    }


    // View Holder Class

    public static class BuildingViewHolder extends RecyclerView.ViewHolder {
        public String geturl,det;
        private ListBuilding ee;
        View mView;
        String name,i;
        public BuildingViewHolder(View itemView) {
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

                    Intent intent = new Intent(mView.getContext(), ListRoom.class);
                    intent.putExtra("build_id", set_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mView.getContext().startActivity(intent);
                }
            });

        }

        public void BuildingsetDetails(Context ctx, String name, String img, String id,String latitude,String longtitude,String inf) {
            geturl = img;
            det = inf;
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

    public void onBackPressed(){ }
}