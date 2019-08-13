package com.example.travelmantic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deal;
    private FirebaseDatabase mFiredata;
    private DatabaseReference mdataRef;
    //listner for when item is added or removed
    private ChildEventListener mChildEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        //hiding menus
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);

        if (FirebaseUtil.isAdmin==true){
            insertMenu.setVisible(true);
        }else {
          insertMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Log.d("log out","use logged out");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        RecyclerView rvDeal = (RecyclerView)findViewById(R.id.rvDeals);
        final DealAdapter adapter = new DealAdapter();
        rvDeal.setAdapter(adapter);

        LinearLayoutManager dealsLayoutManager = new LinearLayoutManager
                (this,LinearLayoutManager.VERTICAL,false);
        rvDeal.setLayoutManager(dealsLayoutManager);

        //populate
        mFiredata = FirebaseUtil.mFirebaseDatabase;
        mdataRef = FirebaseUtil.mFireDatavase;
        FirebaseUtil.opednFBReference("traveldeals");
//        mChildEvent = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                //retrieve travel deals
//               // TextView tvDeals = (TextView) findViewById(R.id.tvDeals);
//                //declare travel deal
//                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
//              //  tvDeals.setText(tvDeals.getText() + "\n" + td.getTitle());
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        mdataRef.addChildEventListener(mChildEvent);
        FirebaseUtil.attachListener();
    }
    public void showMenu(){
        invalidateOptionsMenu();
    }
}
