package com.example.beaconapplication;

import androidx.annotation.MainThread;
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

public class ListRoomAndElevator extends AppCompatActivity {
    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;
    public String build_id,la,lng,build_name,type;
    String s_search = "";
    Button back;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_room_and_elevator);


        type = getIntent().getStringExtra("type");
        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);
        System.out.println("????????????????????????????? ListRoomANDElevator "+ build_id+ "  Type  "+type);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(build_id).child(type);

        back = findViewById(R.id.back_listroomandelevator);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListRoomAndElevator.this, List.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mResultList = (RecyclerView) findViewById(R.id.RoomandElevator);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firebaseSearch(s_search);

    }

    private void firebaseSearch(String searchText) {

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Information_SetNF, RoomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Information_SetNF, RoomViewHolder>(

                Information_SetNF.class,
                R.layout.listroom,
                RoomViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(RoomViewHolder viewHolder, Information_SetNF model, int position) {

                viewHolder.BuildingsetDetails(getApplicationContext(), model.getName(), model.getImage(), model.getFloor(), model.getBuilding_id());

            }
        };


        mResultList.setAdapter(firebaseRecyclerAdapter);


    }


    // View Holder Class

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public String geturi,ffloors;

        public RoomViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView name = (TextView) mView.findViewById(R.id.nameroom);
                    TextView floor = (TextView) mView.findViewById(R.id.floorroom);
                    TextView build_id = (TextView) mView.findViewById(R.id.search_building_id);
                    String set_id = build_id.getText().toString().trim();
                    String Name = name.getText().toString().trim();
                    String Floor = floor.getText().toString().trim();
                    Intent intent = new Intent(mView.getContext(), UpdateAndDelete_Room_Elevator.class);
                    intent.putExtra("img",geturi);
                    intent.putExtra("build_id",set_id);
                    intent.putExtra("name", Name);
                    intent.putExtra("floor", ffloors);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mView.getContext().startActivity(intent);
                }
            });


        }

        public void BuildingsetDetails(Context ctx, String name, String img, String floor,String idbuild) {
            geturi = img;
            TextView Name = (TextView) mView.findViewById(R.id.nameroom);
            ImageView Image = (ImageView) mView.findViewById(R.id.imageroom);
            TextView Floor = (TextView) mView.findViewById(R.id.floorroom);
            TextView Build_id = (TextView) mView.findViewById(R.id.search_building_id);
            Name.setText(name);
            Floor.setText(floor);
            Build_id.setText(idbuild);
            ffloors = floor;


            Glide.with(ctx).load(img).into(Image);

        }
    }
    public void onBackPressed(){ }
}