package com.renegades.saathi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    String name,dob,gender,state;
    TextView tvName,tvGender,tvState,tvDoB;
    Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        tvName = findViewById(R.id.tvName);
        tvDoB = findViewById(R.id.tvDoB);
        tvGender = findViewById(R.id.tvGender);
        tvState = findViewById(R.id.tvState);
        btSubmit = findViewById(R.id.btSubmit);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent predictIntent = new Intent(InfoActivity.this,PredictionActivity.class);
                startActivity(new Intent(InfoActivity.this,PredictionActivity.class));
                predictIntent.putExtra("yob",dob);
                predictIntent.putExtra("gender",gender);
                startActivity(predictIntent);
            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        dob = intent.getStringExtra("yob");
        gender = intent.getStringExtra("gender");
        state = intent.getStringExtra("state");

        if(gender.contains("M")){
            tvGender.setText("Gender - Male");
        }else if(gender.contains("F")){
            tvGender.setText("Gender - Female");
        }

        tvName.setText("Name - "+name);
        tvState.setText("State - "+state);
        tvDoB.setText("YoB - "+dob);



    }
}
