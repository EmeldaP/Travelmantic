package com.example.travelmantic;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mFireDatavase;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mFireAuthLis;
    public static ArrayList<TravelDeal> mTravelDeals;
    private static ListActivity caller;
    public static boolean isAdmin;
    private static final int RC_SIGN_IN=123;
    public static FirebaseStorage mStorage;
    public static StorageReference mStoreRef;

    public FirebaseUtil() {
    }

    public static void opednFBReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFireAuthLis = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    } else {
                        //for differentiating users aka admin and user
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!"
                            , Toast.LENGTH_SHORT).show();

                    connectStorage();
                }




                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER().build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER().build())));
                    caller = callerActivity;


// Create and launch sign-in intent
                   caller.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);

            };


        }
        //update list after every change
        mTravelDeals = new ArrayList<TravelDeal>();

        mFireDatavase = mFirebaseDatabase.getReference().child(ref);
    }

   private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference reference = mFireDatavase.getRef().child("admin").child(userId);
        ChildEventListener listen = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(listen);
    }

    private static void signIn() {
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mFireAuthLis);

    }
  public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mFireAuthLis);
  }
  public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStoreRef = mStorage.getReference().child("deals pictures");

  }

    public static void opednFBReference(String traveldeals) {
    }
}
