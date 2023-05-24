package com.ozalp.instagram.pages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozalp.instagram.Post;
import com.ozalp.instagram.PostAdapter;
import com.ozalp.instagram.R;
import com.ozalp.instagram.databinding.ActivityMainStreamBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainStream extends AppCompatActivity {

    ActivityMainStreamBinding binding;
    FirebaseFirestore firestore;
    PostAdapter postAdapter;
    ArrayList<Post> postArrayList;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainStreamBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        postArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(postArrayList);

        recycleData();
        binding.mainRecycleView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.mainRecycleView.setAdapter(postAdapter);

    }

    public void recycleData(){
        try {

            String email = auth.getCurrentUser().getEmail();
            firestore.collection("Users").whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String username = (String) queryDocumentSnapshots.getDocuments().get(0).getData().get("username");
                    firestore.collection("Users").whereEqualTo("username",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List followList = new ArrayList();
                            followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Following");
                            System.out.println(followList);

                            firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).whereIn("username",followList).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(error!=null){
                                        System.out.println(error.getMessage());
                                        Toast.makeText(getApplicationContext(), error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }

                                    if(value != null){
                                        for (DocumentSnapshot snapshot : value.getDocuments()){
                                            Map<String,Object> map = snapshot.getData();
                                            String[] data =  {(String) map.get("email"), (String) map.get("comment"), (String) map.get("downloadUri"), (String) map.get("username"), String.valueOf(map.get("date")), (String)map.get("profilePhoto")};
                                            System.out.println(data[0]);

                                            Post post = new Post(data[0], data[1], data[2], data[3], data[4],data[5]);
                                            postArrayList.add(post);

                                        }

                                        postAdapter.notifyDataSetChanged();


                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage());
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            System.out.println(e.getMessage());
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void goToMyProfile(View view){
        Intent intent = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intent);
    }
}