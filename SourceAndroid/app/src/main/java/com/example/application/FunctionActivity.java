package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.ActivityFunctionBinding;


public class FunctionActivity extends AppCompatActivity {
    ActivityFunctionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFunctionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar  = getSupportActionBar();
        actionBar.setTitle("Tính năng");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        binding.buttonControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (FunctionActivity.this,ControlActivity.class );
                startActivity(intent);
            }
        });

        binding.buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FunctionActivity.this,DataSensor1.class);
                startActivity(intent);
            }
        });

        binding.buttonInspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (FunctionActivity.this,InspectActivity.class );
                startActivity(intent);
            }
        });

    }





}

