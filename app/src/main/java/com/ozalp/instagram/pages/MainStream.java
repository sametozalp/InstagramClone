package com.ozalp.instagram.pages;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
    Uri imageData;
    FirebaseAuth auth;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher <String> permissionLauncher;

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

        registerLauncher();
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
                    if(!queryDocumentSnapshots.isEmpty()){
                        String username = (String) queryDocumentSnapshots.getDocuments().get(0).getData().get("username");
                        firestore.collection("Users").whereEqualTo("username",username).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()){
                                    List followList = new ArrayList();
                                    followList = (List) queryDocumentSnapshots.getDocuments().get(0).getData().get("Following");
                                    boolean a = false;
                                    if (followList == null || followList.size() == 0){
                                        a = true;
                                    }
                                    if(a == false) {
                                        firestore.collection("Posts")
                                                .orderBy("date", Query.Direction.DESCENDING)
                                                .whereIn("username",followList)
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                                Post post = new Post(data[0], data[1], data[2], data[3], data[4],data[5]);
                                                                postArrayList.add(post);

                                                            }

                                                            postAdapter.notifyDataSetChanged();


                                                        }
                                                    }
                                                });
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());
                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
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

    public void goToMainStream(View view){
        if(getClass().getSimpleName().matches("MainStream")){

        }
    }

    public void goToDiscoveryStream(View view){
        Intent intent = new Intent(getApplicationContext(), DiscoveryStream.class);
        startActivity(intent);
    }

    public void goToFile(View view){
        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            System.out.println("PERMISSON DENIED");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,READ_EXTERNAL_STORAGE)){
                //-----Click link and give permission
                Snackbar.make(view,"Not available to gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission!", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ask permission
                        permissionLauncher.launch(READ_EXTERNAL_STORAGE);
                    }
                }).show();
                //-------
            } else{
                //ask permission
                permissionLauncher.launch(READ_EXTERNAL_STORAGE);
            }
        }else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();

                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        System.out.println("image url:" + imageData);

                        Intent uploadPage = new Intent(getApplicationContext(), UploadPost.class);
                        uploadPage.putExtra("data",imageData.toString());
                        startActivity(uploadPage);

                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(getApplicationContext(),"Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}