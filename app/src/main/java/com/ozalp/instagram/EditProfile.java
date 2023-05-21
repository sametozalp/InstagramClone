package com.ozalp.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    Map<String,Object> allData;

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

        allData = new HashMap<>();

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
                        allData = queryDocumentSnapshots.getDocuments().get(0).getData();
                        Map <String, Object> map = queryDocumentSnapshots.getDocuments().get(0).getData();
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
        Map map = new HashMap<>();

        if(!name.equals(tempName)){
            if(!tempName.isEmpty()){
                map.put("name",tempName);
            }else {
                Toast.makeText(getApplicationContext(),"Name value cannot be left blank",Toast.LENGTH_LONG).show();
            }
        }

        if(!bio.equals(tempBio)){
            map.put("bio", tempBio);
        }

        if(!username.equals(tempUsername)){
            if(!tempUsername.isEmpty()){
                map.put("username",tempUsername);
            }else {
                Toast.makeText(getApplicationContext(),"Username value cannot be left blank",Toast.LENGTH_LONG).show();
            }
        }else{
            map.put("username",username);
        }

        firestore.collection("Users").document(username).set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                username = tempUsername;
                firestore.collection("Users").document(username).set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Edited profile",Toast.LENGTH_LONG).show();
                        finish();
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