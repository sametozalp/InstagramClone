package com.ozalp.instagram;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.ozalp.instagram.databinding.ActivityDiscoveryStreamBinding;
import com.ozalp.instagram.databinding.FooterBinding;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscoveryStream extends AppCompatActivity {

    Uri imageData;
    ActivityDiscoveryStreamBinding binding;
    ActivityResultLauncher <Intent> activityResultLauncher;
    ActivityResultLauncher <String> permissionLauncher;
    RecyclerView discoveryRecycleView;
    FirebaseFirestore firestore;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscoveryStreamBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        discoveryRecycleView = binding.discoveryRecycleView;
        postArrayList = new ArrayList<>();

        registerLauncher();
        recycleData();

        binding.discoveryRecycleView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.discoveryRecycleView.setAdapter(postAdapter);

    }

    public void recycleData(){
        try {
            firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null){
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
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
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

    public void goToMyProfile(View view){
        Intent intent = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intent);
    }
}