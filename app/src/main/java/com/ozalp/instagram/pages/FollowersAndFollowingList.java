package com.ozalp.instagram.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozalp.instagram.FollowAdapter;
import com.ozalp.instagram.PostAdapter;
import com.ozalp.instagram.R;
import com.ozalp.instagram.databinding.ActivityFollowersAndFollowingListBinding;

import java.util.ArrayList;
import java.util.List;

public class FollowersAndFollowingList extends AppCompatActivity {

    ActivityFollowersAndFollowingListBinding binding;
    String takenUsername;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    List followList;
    String id;
    FollowAdapter followAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowersAndFollowingListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        followList = new ArrayList();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();

        takenUsername = intent.getStringExtra("takenUsername");
        System.out.println("takenusername: "+ takenUsername);
        id = intent.getStringExtra("id");

        title();
        getList();



    }

    private void setAdapter(){
        binding.followerRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        followAdapter = new FollowAdapter(followList);
        binding.followerRecycleView.setAdapter(followAdapter);
    }
    private void getList(){
        try {
            if(!(takenUsername == null)){
                firestore.collection("Users").whereEqualTo("username",takenUsername).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            if(id.matches("followersData")){
                                followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Followers");
                                System.out.println(followList);
                                setAdapter();
                            }else{
                                followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Following");
                                System.out.println(followList);
                                setAdapter();
                            }

                        }else {
                            Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else {
                String email = auth.getCurrentUser().getEmail();
                firestore.collection("Users").whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            if(id.matches("followersData")){
                                followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Followers");
                                System.out.println(followList);
                                setAdapter();
                            }else{
                                followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Following");
                                System.out.println(followList);
                                setAdapter();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void title(){
        if(id.matches("followersData")){
            binding.title.setText("Follower List");
        }else if (id.matches("followingData")){
            binding.title.setText("Following List");
        }
    }
}