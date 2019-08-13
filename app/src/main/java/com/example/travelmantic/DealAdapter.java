package com.example.travelmantic;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
    ArrayList<TravelDeal> deal;
    private FirebaseDatabase mFiredata;
    private DatabaseReference mdataRef;
    //listner for when item is added or removed
    private ChildEventListener mChildEvent;
    private TravelDeal travelDeal;
    private ImageView imageDeal;



    public DealAdapter() {

        mFiredata = FirebaseUtil.mFirebaseDatabase;
        mdataRef = FirebaseUtil.mFireDatavase;
        FirebaseUtil.opednFBReference("traveldeals");
        deal = FirebaseUtil.mTravelDeals;

        mChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //retrieve travel deals
                // TextView tvDeals = (TextView) findViewById(R.id.tvDeals);
                //declare travel deal
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                //   Log.d(tag:"deal: ",td.getTitle());
                td.setId(dataSnapshot.getKey());
                deal.add(td);

            }

            @Override
            public void onChildChanged(@androidx.annotation.NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@androidx.annotation.NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        };

        mdataRef.addChildEventListener(mChildEvent);

    }


    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

        TravelDeal tdeal = deal.get(position);
        holder.bind(tdeal);
    }
    public TravelDeal getItemCount() {
        //deal/tdea
        return travelDeal;
    }

    @Override
    public void onClick(View view) {

    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            tvDescription = (TextView)itemView.findViewById(R.id.txtDesc);
            tvPrice =(TextView)itemView.findViewById(R.id.txtPrice);
            imageDeal = (ImageView)itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);


        }
        public void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
            tvDescription.setText(deal.getDescrption());
            tvPrice.setText(deal.getPrice());
            showIage(deal.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("click",String.valueOf(position));
            TravelDeal selectedDeal = deal.get(position);
            Intent intent = new Intent(view.getContext(),DealActivity.class);
            intent.putExtra("Deal",selectedDeal);
            view.getContext().startActivity(intent);
        }
    }
    private void showIage(String url){
        if (url != null && url.isEmpty() == false){
            Picasso.with(imageDeal.getContext()).load(url).resize(160,160).centerCrop.into(imageDeal);
        }

    }
}
