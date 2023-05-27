package com.ozalp.instagram.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozalp.instagram.databinding.ActivityCreateAccountBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateAccount extends AppCompatActivity {

    ActivityCreateAccountBinding binding;
    EditText nameCreateText, emailCreateText, passwordCreateText, passwordCreateText2, usernameCreateText;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    boolean a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        nameCreateText = binding.nameCreateText;
        emailCreateText = binding.emailCreateText;
        passwordCreateText = binding.PasswordCreateText;
        passwordCreateText2 = binding.PasswordCreateText2;
        usernameCreateText = binding.usernameCreateText;

        a = false;
    }

    public void createAccount(View view) {
        String name = nameCreateText.getText().toString();
        String email = emailCreateText.getText().toString();
        String password = passwordCreateText.getText().toString();
        String password2 = passwordCreateText2.getText().toString();
        String username = usernameCreateText.getText().toString();
        System.out.println(username);
        System.out.println(email);

        a = false;
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || username.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Can'not be blank", Toast.LENGTH_LONG).show();
        } else
        {
            firestore.collection("Users")
                     .whereEqualTo("username", username)
                     .get()
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                         }
                     }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                         @Override
                         public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                             if (queryDocumentSnapshots.isEmpty()) {
                                firestore.collection("Users")
                                        .whereEqualTo("email",email)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty())
                                                {
                                                    Toast.makeText(getApplicationContext(), "Email is in use.", Toast.LENGTH_LONG).show();
                                                } else
                                                {
                                                    if (!password.equals(password2))
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Passwords are not the same", Toast.LENGTH_LONG).show();
                                                    } else
                                                    {
                                                        auth.createUserWithEmailAndPassword(email, password)
                                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                    @Override
                                                                    public void onSuccess(AuthResult authResult) {

                                                                        HashMap<String, Object> map = new HashMap<>();
                                                                        map.put("followers", 0);
                                                                        map.put("following", 0);
                                                                        map.put("posts", 0);
                                                                        map.put("bio","");
                                                                        map.put("name", name);
                                                                        map.put("profilePhoto",
                                                                                "https://firebasestorage.googleapis.com/v0/b/instagram-155fd.appspot.com/o/nophoto.jpg?alt=media&token=3596685c-39ae-48bc-a5b1-cee53732dd12");
                                                                        map.put("email", email);
                                                                        map.put("username", username);
                                                                        List l = new ArrayList<>();
                                                                        map.put("Following",l);
                                                                        map.put("Followers",l);
                                                                        map.put("signUpDate", FieldValue.serverTimestamp());

                                                                        firestore.collection("Users").document(username).set(map)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Toast.makeText(getApplicationContext(), "Created user",
                                                                                                Toast.LENGTH_SHORT).show();
                                                                                        finish();
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
                                                                                                Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });

                                                                        finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG)
                                                                                .show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                             }else
                             {
                                 Toast.makeText(getApplicationContext(), "Username is in use.", Toast.LENGTH_LONG).show();
                             }


                         }
                     });


        }
    }
}