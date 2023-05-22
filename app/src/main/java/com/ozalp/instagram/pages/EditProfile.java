package com.ozalp.instagram.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.ozalp.instagram.databinding.ActivityEditProfileBinding;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    EditText nameEditText, usernameEditText, emailEditText, passwordEditText;
    TextView signUpDateText;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String name, email,username,signUpDate,bio;
    String rEmail;
    Map<String,Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        nameEditText = binding.nameEditText;
        usernameEditText = binding.usernameEditText;
        emailEditText = binding.emailEditText;
        signUpDateText = binding.signUpDateText;
        passwordEditText = binding.passwordEditText;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getData();

    }

    private void getData() {
        try {
            rEmail = auth.getCurrentUser().getEmail();
            firestore.collection("Users").whereEqualTo("email",rEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){
                        Toast.makeText(getApplicationContext(),"No Data",Toast.LENGTH_LONG).show();
                    }else {
                        map = queryDocumentSnapshots.getDocuments().get(0).getData();
                        System.out.println(map);
                        name = map.get("name").toString();
                        email = map.get("email").toString();
                        username = map.get("username").toString();
                        signUpDate = map.get("signUpDate").toString();
                        bio = map.get("bio").toString();

                        long c = Long.parseLong(String.valueOf(signUpDate.substring(18,28)));
                        c = c * 1000;
                        Timestamp a = new Timestamp(c);

                        binding.nameEditText.setText(name);
                        binding.emailEditText.setText(email);
                        binding.emailEditText.setEnabled(false);
                        binding.emailEditText.setFocusable(false);
                        binding.usernameEditText.setText(username);
                        binding.signUpDateText.setText(Html.fromHtml("<b>SignUpDate:</b> <i>"+ a.toString().substring(0,a.toString().indexOf(".")-3)));
                        binding.bioEditText.setText(bio);
                        System.out.println(map);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public void editProfile(View view){

        String tempName = binding.nameEditText.getText().toString();
        String tempUsername = binding.usernameEditText.getText().toString();
        String tempBio = binding.bioEditText.getText().toString();
        String tempPassword = binding.passwordEditText.getText().toString();
        boolean usernameChanged = false;

        Map mapTemp = new HashMap<>();

        if(!name.equals(tempName)){
            if(!tempName.isEmpty()){
                mapTemp.put("name",tempName);
            }else {
                Toast.makeText(getApplicationContext(),"Name value cannot be left blank",Toast.LENGTH_LONG).show();
            }
        }

        if(!bio.equals(tempBio)){
            mapTemp.put("bio", tempBio);
        }

        if(!username.equals(tempUsername)){
            if(!tempUsername.isEmpty()){
                mapTemp.put("username",tempUsername);
                usernameChanged = true;
            }else {
                Toast.makeText(getApplicationContext(),"Username value cannot be left blank",Toast.LENGTH_LONG).show();
            }
        }

        if(usernameChanged == false){
            firestore.collection("Users").document(username).set(mapTemp,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if(!tempPassword.isEmpty()){
                        auth.getCurrentUser().updatePassword(tempPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                finish();
                                Toast.makeText(getApplicationContext(),"Edited profile", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    finish();
                    Toast.makeText(getApplicationContext(),"Edited profile", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else {
            firestore.collection("Users").whereEqualTo("username",tempUsername).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        Toast.makeText(getApplicationContext(),"The username is use",Toast.LENGTH_LONG).show();
                    }else{
                        map.putAll(mapTemp);
                        firestore.collection("Users").document(tempUsername).set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                firestore.collection("Users").document(username).delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        if(!tempPassword.isEmpty()){
                                            auth.getCurrentUser().updatePassword(tempPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    finish();
                                                    Toast.makeText(getApplicationContext(),"Edited profile", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }else{
                                            finish();
                                            Toast.makeText(getApplicationContext(),"Edited profile", Toast.LENGTH_SHORT).show();
                                        }
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"The username is use",Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}