package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListAllRoom extends AppCompatActivity {
    private EditText mSearchField;
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    String s_search = "";
    String build_id;
    Button btn_back;
    public ArrayList<String> array = new ArrayList<>();
    Handler delay = new Handler();
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_room);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUserDatabase = FirebaseDatabase.getInstance().getReference("AllRoom");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);
        mSearchField = (EditText) findViewById(R.id.search2_field);

        mResultList = (RecyclerView) findViewById(R.id.result2_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        btn_back = findViewById(R.id.indoorroomlist2_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListAllRoom.this, Main_Navigation.class);
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



        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Information_SetNF, ListAllRoom.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Information_SetNF, ListAllRoom.ViewHolder>(

                Information_SetNF.class,
                R.layout.listroom,
                ListAllRoom.ViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(ListAllRoom.ViewHolder viewHolder, Information_SetNF model, int position) {

                viewHolder.setDetails(getApplicationContext(), model.getName(), model.getImage(), model.getFloor(), model.getBuilding_id());

            }
        };


        mResultList.setAdapter(firebaseRecyclerAdapter);


    }


    // View Holder Class

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView name = (TextView) mView.findViewById(R.id.nameroom);
                    TextView floor = (TextView) mView.findViewById(R.id.floorroom);
                    String put_name = name.getText().toString().trim();
                    String put_floor = floor.getText().toString().trim();
                    TextView build_id = (TextView) mView.findViewById(R.id.search_building_id);
                    String set_id = build_id.getText().toString().trim();

                    Intent intent = new Intent(mView.getContext(), Checklocation.class);
                    intent.putExtra("name", put_name);
                    intent.putExtra("floor", put_floor);
                    intent.putExtra("build_id", set_id);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    mView.getContext().startActivity(intent);

                }
            });


        }

        public void setDetails(Context ctx, String name, String img, String floor, String b_id) {

            TextView Name = (TextView) mView.findViewById(R.id.nameroom);
            TextView Floor = (TextView) mView.findViewById(R.id.floorroom);
            ImageView Image = (ImageView) mView.findViewById(R.id.imageroom);
            TextView B_id = (TextView) mView.findViewById(R.id.search_building_id);

            Name.setText(name);
            Floor.setText(floor);
            B_id.setText(b_id);
            Glide.with(ctx).load(img).into(Image);

        }

    }
    public void onBackPressed(){ }

}