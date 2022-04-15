package com.example.publicsafetycommission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {
    ImageView registerComplaint, viewComplaints, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerComplaint=findViewById(R.id.registerComplaint);
        viewComplaints=findViewById(R.id.viewComplaint);
        profile=findViewById(R.id.profilee);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(HomeActivity.this, viewProfile.class);
                startActivity(newIntent);
            }
        });

        registerComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, registercomp.class);
                startActivity(myIntent);
            }
        });
        viewComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, viewComplaints.class);
                startActivity(myIntent);
            }
        });


    }



}