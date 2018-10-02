package com.renegades.saathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class PredictionActivity extends AppCompatActivity {

    EditText etSymptoms;
    ImageView ivSpeak;
    Button btDiagnose;
    String st,yob,gender;
    String doctorType;
    private RequestQueue mQueue;
    TextView tvCause;
    Button btDoctors;
    private ArrayList<String> mEntries;
    ArrayList<String>causeList;
    ArrayList<String>accuracyList;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    ProgressDialog progressDialog,doctorssearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        getSupportActionBar().setTitle("Quick Diagnose");

        checkPermission();

        causeList = new ArrayList<>();
        accuracyList = new ArrayList<>();

        doctorssearch = new ProgressDialog(this);
        doctorssearch.setMessage("Searching for doctors...");
        doctorssearch.setTitle("Searching");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching causes...");
        progressDialog.setTitle("Fetching");


        etSymptoms = findViewById(R.id.etSymptoms);
        btDoctors = findViewById(R.id.btDoctors);
        btDoctors.setVisibility(View.INVISIBLE);
        tvCause = findViewById(R.id.tvCause);
        ivSpeak = findViewById(R.id.ivSpeak);
        btDiagnose = findViewById(R.id.btDiagonse);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        yob = intent.getStringExtra("yob");
        gender = intent.getStringExtra("gender");

        btDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorssearch.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(PredictionActivity.this,DoctorListActivity.class));
                        doctorssearch.dismiss();
                    }
                },200);

            }
        });


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    etSymptoms.setText(matches.get(0));
                    st = etSymptoms.getText().toString();


                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        ivSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {


                    case MotionEvent.ACTION_UP:

                        mSpeechRecognizer.stopListening();
                        etSymptoms.setHint("You will see the input here");
                        break;

                    case MotionEvent.ACTION_DOWN:

                        etSymptoms.setText("");
                        etSymptoms.setHint("Listening...");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                        break;
                }
                return false;
            }
        });

        btDiagnose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                st = etSymptoms.getText().toString();
                st = st.toUpperCase();

                String s = st.substring(0,1).concat(st.substring(1).toLowerCase());

                progressDialog.show();

                jsonParse(s);

            }
        });
    }

    private void jsonParse(final String st) {

        String url = "https://healthservice.priaid.ch/symptoms?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNpZGRoYXJ0aGNvb2xzMTJAZ21haWwuY29tIiwicm9sZSI6IlVzZXIiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9zaWQiOiI0NzQiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ZlcnNpb24iOiIxMDgiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xpbWl0IjoiMTA1IiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwIjoiQmFzaWMiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xhbmd1YWdlIjoiZW4tZ2IiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL2V4cGlyYXRpb24iOiIyMDk5LTEyLTMxIiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwc3RhcnQiOiIyMDAwLTAxLTAxIiwiaXNzIjoiaHR0cHM6Ly9hdXRoc2VydmljZS5wcmlhaWQuY2giLCJhdWQiOiJodHRwczovL2hlYWx0aHNlcnZpY2UucHJpYWlkLmNoIiwiZXhwIjoxNTM4NDgzNDI5LCJuYmYiOjE1Mzg0NzYyMjl9.SJ0k-4keP9I0p5PPrgzSbV6OkJMw-YVKSIzRefmsBUg&format=json&language=en-gb";
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            String ID = jsonObject.optString("ID");
                            String Name = jsonObject.optString("Name");

                            if(Name.contains(st)) {

                                predict(ID,yob,gender);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PredictionActivity.this, "Unable to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        mEntries = new ArrayList<>();
        mQueue.add(request);

    }

    private void predict(String id, String yob, String gender) {

        if(gender.contains("M")){
            gender = "male";
        }else{
            gender = "female";
        }



        String url = "https://healthservice.priaid.ch/diagnosis?symptoms=["+id+"]&gender="+gender+"&year_of_birth="+yob+"&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNpZGRoYXJ0aGNvb2xzMTJAZ21haWwuY29tIiwicm9sZSI6IlVzZXIiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9zaWQiOiI0NzQiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ZlcnNpb24iOiIxMDgiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xpbWl0IjoiMTA1IiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwIjoiQmFzaWMiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xhbmd1YWdlIjoiZW4tZ2IiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL2V4cGlyYXRpb24iOiIyMDk5LTEyLTMxIiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwc3RhcnQiOiIyMDAwLTAxLTAxIiwiaXNzIjoiaHR0cHM6Ly9hdXRoc2VydmljZS5wcmlhaWQuY2giLCJhdWQiOiJodHRwczovL2hlYWx0aHNlcnZpY2UucHJpYWlkLmNoIiwiZXhwIjoxNTM4NDgzNDgwLCJuYmYiOjE1Mzg0NzYyODB9.so1L0UcQ6fm2oqpP1dMnholjFB6hitqiqjVk9w3cu-0&format=json&language=en-gb";

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        String cause =  "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            JSONObject issueObject = jsonObject.optJSONObject("Issue");
                            String name = issueObject.optString("Name");
                            String accuracy = issueObject.optString("Accuracy");
                            try {
                                JSONArray specialistArray = jsonObject.getJSONArray("Specialisation");
                                JSONObject specialist = specialistArray.getJSONObject(0);
                                doctorType = specialist.optString("Name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            causeList.add(name);
                            accuracyList.add(accuracy);


                            cause = cause +" " +name;


                            tvCause.setText("Causes - "+cause+"\n\nSpecialist - "+doctorType);
                            btDoctors.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();





                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PredictionActivity.this, "Unable to predict: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });



        mEntries = new ArrayList<>();
        mQueue.add(request);
    }


    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+ getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }



}
