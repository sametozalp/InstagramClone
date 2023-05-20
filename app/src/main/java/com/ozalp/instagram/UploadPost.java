package com.ozalp.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.ozalp.instagram.databinding.ActivityUploadPostBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UploadPost extends AppCompatActivity {

    ActivityUploadPostBinding binding;
    FirebaseFirestore firestore;
    Uri imageData;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    Button uploadButton;

    private void getImage(){
        Intent intent = getIntent();
        imageData = Uri.parse(intent.getStringExtra("data"));
        binding.galleyImage.setImageURI(imageData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        uploadButton = binding.uploadButton;
        uploadButton.setEnabled(true);
        getImage();


    }

    public void uploadButton(View view){

        uploadButton.setEnabled(false);
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
        System.out.println(imageData);

        try{
            firebaseStorage.getReference()
                            .child("images/"+ uuid + ".png")
                            .putFile(imageData)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    firebaseStorage.getReference("images/"+uuid+ ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUri = uri.toString();



                                            String myEmail = auth.getCurrentUser().getEmail();
                                            firestore.collection("Users").whereEqualTo("email",myEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if(!queryDocumentSnapshots.isEmpty()){
                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                        String username= (String) list.get(0).getData().get("username");
                                                        String comment = binding.commentText.getText().toString();
                                                        FirebaseUser userMe = auth.getCurrentUser();
                                                        String email = userMe.getEmail();
                                                        HashMap <String,Object> map = new HashMap<>();
                                                        map.put("downloadUri",downloadUri);
                                                        map.put("comment",comment.trim());
                                                        map.put("email",email);
                                                        map.put("username",username);
                                                        map.put("date", FieldValue.serverTimestamp());
                                                        System.out.println(map);

                                                        firestore.collection("Posts").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {



                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });


                                        }
                                    });

                                    Intent intent = new Intent(getApplicationContext(),DiscoveryStream.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
        }catch (Exception e){
            System.out.println(e.getMessage());
            uploadButton.setEnabled(true);
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
}