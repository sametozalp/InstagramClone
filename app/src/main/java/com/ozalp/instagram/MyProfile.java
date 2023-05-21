package com.ozalp.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozalp.instagram.databinding.ActivityMyProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyProfile extends AppCompatActivity {

    ActivityMyProfileBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        postArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(postArrayList);

        getMyProfileData();
        myProfileRecycleGetData();
    }

    private void myProfileRecycleGetData(){
        try {
            firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null){
                        System.out.println(error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    if(value != null){
                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            Map<String,Object> map = snapshot.getData();
                            String[] data =  {(String) map.get("email"), (String) map.get("comment"), (String) map.get("downloadUri"), (String) map.get("username"), String.valueOf(map.get("date"))};
                            System.out.println(data[0]);

                            Post post = new Post(data[0], data[1], data[2], data[3], data[4]);
                            postArrayList.add(post);

                        }

                        postAdapter.notifyDataSetChanged();


                    }
                }
            });

        }catch (Exception e){
            System.out.println(e.getMessage());
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void getMyProfileData(){
        String myEmail = auth.getCurrentUser().getEmail();
        System.out.println(myEmail);
        firestore.collection("Users").whereEqualTo("email",myEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    String username= (String) list.get(0).getData().get("username");
                    Object post = list.get(0).getData().get("posts");
                    Object followers = list.get(0).getData().get("followers");
                    Object following = list.get(0).getData().get("following");
                    Object profilePhoto = list.get(0).getData().get("profilePhoto");
                    Object name = list.get(0).getData().get("name");
                    Object bio = list.get(0).getData().get("bio");


                    binding.postData.setText(post + "\nPosts");
                    binding.followersData.setText(followers + "\nFollowers");
                    binding.followingData.setText(following + "\nFollowing");
                    Picasso.get().load(Uri.parse(String.valueOf(profilePhoto))).into(binding.profilePhoto);
                    binding.username.setText(username);
                    binding.name.setText((String) name);
                    binding.bio.setText((String) bio);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signoutButton(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("LogOut?");
        alertDialog.setMessage("Do you want to log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    auth.signOut();

                    Intent intent = new Intent(MyProfile.this,SignInUp.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

    }
}