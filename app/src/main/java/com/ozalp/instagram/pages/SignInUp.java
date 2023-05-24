package com.ozalp.instagram.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozalp.instagram.databinding.ActivityMainBinding;

public class SignInUp extends AppCompatActivity {

    private ActivityMainBinding binding;
    EditText emailText;
    EditText passwordText;
    Button loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        emailText = binding.emailText;
        passwordText = binding.passwordText;
        loginButton = binding.loginButton;
        loginButton.setEnabled(true);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(getApplicationContext(),MainStream.class);
            startActivity(intent);
            finish();
        }

    }

    public void login(View view){
        String emailTextData = emailText.getText().toString();
        String passwordTextData = passwordText.getText().toString();

        if(emailTextData.isEmpty() || passwordTextData.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "Enter email or password",
                    Toast.LENGTH_LONG)
                    .show();
        }else{
            loginButton.setEnabled(false);
            auth.signInWithEmailAndPassword(emailTextData,passwordTextData)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            loginButton.setEnabled(true);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(getApplicationContext(), MainStream.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    public void goToCreateAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
        startActivity(intent);
    }
}

