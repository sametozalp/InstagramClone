package com.ozalp.instagram.pages;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.ozalp.instagram.Post;
import com.ozalp.instagram.PostAdapter;
import com.ozalp.instagram.databinding.ActivityMyProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyProfile extends AppCompatActivity {

    ActivityMyProfileBinding binding;
    Uri imageData;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    FirebaseAuth auth;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;
    ImageView profilePhoto;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher <String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        postArrayList = new ArrayList<>();
        postAdapter = new PostAdapter(postArrayList);

        getMyProfileData();

        registerLauncher();
        profilePhoto = binding.profilePhoto;

        profilePhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                System.out.println("select profile photo");
                if(ContextCompat.checkSelfPermission(MyProfile.this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    System.out.println("PERMISSON DENIED");
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MyProfile.this,READ_EXTERNAL_STORAGE)){
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
                return true;
            }
        });

    }

    public void goToEditProfile(View view){
        Intent intent = new Intent(getApplicationContext(),EditProfile.class);
        startActivity(intent);
    }

    private void getMyProfileData(){
        try {
            String myEmail = auth.getCurrentUser().getEmail();
            System.out.println(myEmail);
            firestore.collection("Users").whereEqualTo("email",myEmail).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
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
                    if(error!= null){
                        Toast.makeText(getApplicationContext(),"No data", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
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

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();

                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        System.out.println("image url:" + imageData);
                        String email = auth.getCurrentUser().getEmail();
                        firestore.collection("Users").whereEqualTo("email",email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String username = (String) queryDocumentSnapshots.getDocuments().get(0).getData().get("username");
                                UUID uuid = UUID.randomUUID();
                                firebaseStorage.getReference().child("profilePhotos/"+uuid+".png").putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        firebaseStorage.getReference("profilePhotos/"+uuid+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Map map = new HashMap<>();
                                                map.put("profilePhoto",uri.toString());
                                                firestore.collection("Users").document(username).set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getApplicationContext(),"Changed profile photo",Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
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