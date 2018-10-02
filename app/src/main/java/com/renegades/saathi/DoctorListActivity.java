package com.renegades.saathi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DoctorListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        getSupportActionBar().setTitle("Doctor List");
    }
}
