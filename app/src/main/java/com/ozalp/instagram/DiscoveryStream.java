package com.ozalp.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ozalp.instagram.databinding.ActivityDiscoveryStreamBinding;

public class DiscoveryStream extends AppCompatActivity {

    ActivityDiscoveryStreamBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscoveryStreamBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }
}