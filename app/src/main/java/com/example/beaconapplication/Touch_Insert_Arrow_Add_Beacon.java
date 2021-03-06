package com.example.beaconapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Touch_Insert_Arrow_Add_Beacon extends AppCompatActivity implements View.OnTouchListener{
    private ImageView mImageView,mImageView2;
    DatabaseReference condb;
    RelativeLayout relativeLayout;
    Information_Build setBuild;
    Button btn,btn_rotate;
    private int _xDelta;
    private int _yDelta;
    public StorageReference firestorage;
    public  Uri photoURI;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    String build_id,la,lng,build_name,floor,address;
    String mapL,mapR,mapF,mapB,nkow,rmname,type,Countfloors;
    public String imghttp;
    String nleft,nright,nback,nfront,nmain;
    String status,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_insert_arrow_add_beacon);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        status = getIntent().getStringExtra("status");
        userid = getIntent().getStringExtra("userid");
        System.out.println("Status  "+status);
        System.out.println("Userid  "+userid);

        build_id = getIntent().getStringExtra("build_id");
        rmname = getIntent().getStringExtra("namephoto");
        address = getIntent().getStringExtra("address");
        floor = getIntent().getStringExtra("floor");
        mapL = getIntent().getStringExtra("left");
        mapR = getIntent().getStringExtra("right");
        mapF = getIntent().getStringExtra("front");
        mapB = getIntent().getStringExtra("back");
        nkow = getIntent().getStringExtra("nkow");
        type = getIntent().getStringExtra("type");
        Countfloors = getIntent().getStringExtra("Countfloors");

        nmain = getIntent().getStringExtra("nmain");
        nleft = getIntent().getStringExtra("nleft");
        nright = getIntent().getStringExtra("nright");
        nback = getIntent().getStringExtra("nback");
        nfront = getIntent().getStringExtra("nfront");

        System.out.println(" LLL EEE FFF TTT   "+nmain);

        mImageView2 = findViewById(R.id.back);
        mImageView = findViewById(R.id.imageView);
        btn = findViewById(R.id.btn);
        relativeLayout = findViewById(R.id.relay);
        setBuild = new Information_Build();
        btn_rotate = findViewById(R.id.btn_rorate);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setRotation(mImageView.getRotation() + 45);
            }
        });

        Query query = FirebaseDatabase.getInstance().getReference(build_id).child("Floors").orderByChild("level").equalTo(floor);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    System.out.println("มี");
                    for(DataSnapshot item : snapshot.getChildren()){
                        System.out.println("http "+item.child("level").getValue().toString());
                        System.out.println("http "+item.child("map").getValue().toString());
                        Picasso.get().load(item.child("map").getValue().toString()).into(mImageView2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mImageView = (ImageView) relativeLayout.findViewById(R.id.imageView);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
        mImageView.setLayoutParams(layoutParams);
        mImageView.setOnTouchListener(this);


        condb = FirebaseDatabase.getInstance().getReference();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private void share() {
        Bitmap bitmap = getbitmapFormview(relativeLayout);
        try {
            File file = new File(this.getExternalCacheDir(),rmname);
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);

            photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);

            firestorage = FirebaseStorage.getInstance().getReference().child("8796").child("ImageFolder");

            final StorageReference imagename = firestorage.child(photoURI.getLastPathSegment());
            imagename.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("QWESADQWEASDQWEASDQWQW     "+String.valueOf(uri));

                            imghttp = String.valueOf(uri);
                            sendimg();

                        }
                    });
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
    }

    private void sendimg() {

        if (type.equals("room")) {
            if (nkow.equals("right")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddRoom.class);
                intent.putExtra("mapR", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapF", mapF);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapB", mapB);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("front")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddRoom.class);
                intent.putExtra("mapF", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapB", mapB);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("left")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddRoom.class);
                intent.putExtra("mapL", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapB", mapB);
                intent.putExtra("mapF", mapF);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("back")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddRoom.class);
                intent.putExtra("mapB", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapF", mapF);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        if (type.equals("elevator")) {
            if (nkow.equals("right")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddElevator.class);
                intent.putExtra("mapR", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("countfloors", Countfloors);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapF", mapF);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapB", mapB);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("front")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddElevator.class);
                intent.putExtra("mapF", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("countfloors", Countfloors);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapB", mapB);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("left")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddElevator.class);
                intent.putExtra("mapL", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("countfloors", Countfloors);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapB", mapB);
                intent.putExtra("mapF", mapF);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (nkow.equals("back")) {
                String map = imghttp;
                Intent intent = new Intent(Touch_Insert_Arrow_Add_Beacon.this, Beacon_AddElevator.class);
                intent.putExtra("mapB", map);
                intent.putExtra("address", address);
                intent.putExtra("floor", floor);
                intent.putExtra("type", type);
                intent.putExtra("countfloors", Countfloors);
                intent.putExtra("build_id", build_id);
                intent.putExtra("mapR", mapR);
                intent.putExtra("mapL", mapL);
                intent.putExtra("mapF", mapF);
                intent.putExtra("nmain", nmain);
                intent.putExtra("nleft", nleft);
                intent.putExtra("nright", nright);
                intent.putExtra("nfront", nfront);
                intent.putExtra("nback", nback);
                intent.putExtra("status",status);
                intent.putExtra("userid",userid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private Bitmap getbitmapFormview (View view) {
        Bitmap returnbitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnbitmap);
        Drawable bgDrawable = view.getBackground();
        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        }else {
            canvas.drawColor(android.R.color.white);
        }
        view.draw(canvas);
        return returnbitmap;
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        relativeLayout.invalidate();
        return true;
    }
    public void onBackPressed(){ }
}