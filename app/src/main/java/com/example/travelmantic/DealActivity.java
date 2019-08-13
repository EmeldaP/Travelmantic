package com.example.travelmantic;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.grpc.internal.SharedResourceHolder;
import io.opencensus.resource.Resource;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference mDataref;

    EditText  txtTitle;
    EditText txtDescribtion;
    EditText txtPrice;
    private TravelDeal deal;
    private static final int PICTURE_RESULTS = 42;
     ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        mFirebasedatabase = FirebaseUtil.mFirebaseDatabase;
        mDataref = FirebaseUtil.mFireDatavase;
        FirebaseUtil.opednFBReference("traveldeals");

        txtPrice = (EditText)findViewById(R.id.editText2);
        txtDescribtion =(EditText)findViewById(R.id.editText3);
        txtTitle =(EditText)findViewById(R.id.editText) ;
        Button btnImg = (Button)findViewById(R.id.buttonImg);
        ImageView image =(ImageView)findViewById(R.id.imageView);



        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO SELECT DATA AND RETURN IT
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert picture"),42);
            }
        });

        Intent intent = getIntent();
        TravelDeal deal= (TravelDeal)intent.getSerializableExtra("Deal" );
        if (deal==null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtPrice.setText(deal.getPrice());
        txtDescribtion.setText(deal.getDescrption());
        txtTitle.setText(deal.getTitle());
        showImage(deal.getImageUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
            Toast.makeText(this,"Deal saved",Toast.LENGTH_LONG).show();
            clean();
            backToList();
            return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this,"Deal Deleted",Toast.LENGTH_LONG).show();
                backToList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==PICTURE_RESULTS && resultCode == RESULT_OK){
            Uri imageUrl = data.getData();
            StorageReference reff = FirebaseUtil.mStoreRef.child(imageUrl.getLastPathSegment());
            reff.putFile(imageUrl).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    String pictureNane = taskSnapshot.getStorage().getPath();
                    deal.setImageUrl(url);
                    deal.setImageName(pictureNane);
                    showImage(url);

                }
            });

        }
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescribtion.setText("");

        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle( txtTitle.getText().toString());
        deal.setDescrption(txtDescribtion.getText().toString());
       deal.setPrice( txtPrice.getText().toString());

       //id to id if deal exist or not
       if (deal.getId()==null) {

           // inserting new data
           mDataref.push().setValue(deal);
       }
       else {
           mDataref.child(deal.getId()).setValue(deal);
       }
    }
      private void deleteDeal(){
        if (deal  ==null){
            Toast.makeText(this,"Please save the deal before deleting",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mDataref.child(deal.getId()).removeValue();
        if (deal.getImageName() != null && deal.getImageName().isEmpty()== false){
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }

            });
        }
      }
      private void backToList(){
        Intent intent =new Intent(this,ListActivity.class);
        startActivity(intent);
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
       inf.inflate(R.menu.save_menu,menu);
       //hiding menu
       if (FirebaseUtil.isAdmin ){
           menu.findItem(R.id.delete_menu).setVisible(true);
           menu.findItem(R.id.save_menu).setVisible(true);
           enableEditText(true);
       }else {
           menu.findItem(R.id.delete_menu).setVisible(false);
           menu.findItem(R.id.save_menu).setVisible(false);
           enableEditText(false);
       }
          return true;
    }
    public void enableEditText(boolean isEnabled){

        txtTitle.setEnabled(isEnabled);
        txtDescribtion.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }
    public void showImage(String url){
        if (url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(url).resize(width,width*2/3).centerCrop().into(image);
        }
    }
}
