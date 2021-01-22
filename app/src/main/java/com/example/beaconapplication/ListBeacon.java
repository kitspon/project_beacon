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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListBeacon extends AppCompatActivity {
    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;
    public String build_id,la,lng,build_name;
    String s_search = "";
    Button back;
    static String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_beacon);

        build_id = getIntent().getStringExtra("build_id");
        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        System.out.println("????????????????????????????? ListRoomANDElevator "+ build_id);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUserDatabase = FirebaseDatabase.getInstance().getReference(build_id).child("Beacon");



        mResultList = (RecyclerView) findViewById(R.id.list_routingg);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));
        back = findViewById(R.id.back_listroute);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListBeacon.this, Main_Sub_Building_Setting.class);
                intent.putExtra("build_id",build_id);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        firebaseSearch(s_search);

    }

    private void firebaseSearch(String searchText) {

        Query firebaseSearchQuery = mUserDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Information_BeaCon, ListrouteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Information_BeaCon, ListrouteViewHolder>(

                Information_BeaCon.class,
                R.layout.listbeacon,
                ListrouteViewHolder.class,
                firebaseSearchQuery


        ) {
            @Override
            protected void populateViewHolder(ListrouteViewHolder viewHolder, Information_BeaCon model, int position) {

                viewHolder.ListroutesetDetails(getApplicationContext(), model.getName(), model.getAddress(), model.getFloors(), model.getRm_L()
                        , model.getRm_R(), model.getRm_F(), model.getRm_B(), model.getType(), model.getBuild_id());

            }
        };


        mResultList.setAdapter(firebaseRecyclerAdapter);


    }


    // View Holder Class

    public static class ListrouteViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public String tname, taddress, tfloor, trm_l, trm_r, trm_f, trm_b,build_id,types;

        public ListrouteViewHolder(View itemView) {
            super(itemView);


            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(mView.getContext(), UpdateAndDelete_Beacon.class);
                    intent.putExtra("build_id",build_id);
                    intent.putExtra("name",tname);
                    intent.putExtra("address", taddress);
                    intent.putExtra("floor", tfloor);
                    intent.putExtra("rm_l", trm_l);
                    intent.putExtra("rm_r", trm_r);
                    intent.putExtra("rm_f", trm_f);
                    intent.putExtra("rm_b", trm_b);
                    intent.putExtra("type", types);
                    intent.putExtra("status",status);
                    intent.putExtra("userid",userid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mView.getContext().startActivity(intent);
                }
            });


        }

        public void ListroutesetDetails(Context ctx, String name, String address, String floor, String rm_l, String rm_r, String rm_f
                                        , String rm_b, String type,String id) {

            TextView NAME = (TextView) mView.findViewById(R.id.name_beacon);
            TextView ADDRESS = (TextView) mView.findViewById(R.id.address_beacon);
            TextView FLOOR = (TextView) mView.findViewById(R.id.floor_beacon);
            TextView RM_L = (TextView) mView.findViewById(R.id.left_beacon);
            TextView RM_R = (TextView) mView.findViewById(R.id.right_beacon);
            TextView RM_F = (TextView) mView.findViewById(R.id.front_beacon);
            TextView RM_B = (TextView) mView.findViewById(R.id.back_beacon);
            TextView TYPE = (TextView) mView.findViewById(R.id.type_beacon);

            build_id = id;
            tname = name;
            taddress = address;
            tfloor = floor;
            trm_l = rm_l;
            trm_r = rm_r;
            trm_f = rm_f;
            trm_b = rm_b;
            types = type;
            NAME.setText(name);
            ADDRESS.setText(address);
            FLOOR.setText("floor:  "+floor);
            if (rm_l==null){
                RM_L.setText("Room on the left:  "+"none");
            }else {
                RM_L.setText("Room on the left:  "+rm_l);
            }
            if (rm_r==null){
                RM_R.setText("Room on the right:  "+"none");
            }else {
                RM_R.setText("Room on the right:  "+rm_r);
            }
            if (rm_f==null){
                RM_F.setText("Room on the front:  "+"none");
            }else {
                RM_F.setText("Room on the front:  "+rm_f);
            }
            if (rm_b==null){
                RM_B.setText("Room on the back:  "+"none");
            }else {
                RM_B.setText("Room on the back:  "+rm_b);
            }

            TYPE.setText(type);

        }
    }

    public void onBackPressed(){ }
}