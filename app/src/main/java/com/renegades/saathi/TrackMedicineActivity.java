package com.renegades.saathi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;


public class TrackMedicineActivity extends AppCompatActivity implements LocationListener {

    FloatingActionButton fabAdd;
    TextView defaultText;
    Bitmap imageBitmap;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String imgString;
    protected String latitude, longitude;
    Location location;
    protected boolean gps_enabled, network_enabled;
    String requestbody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_medicine);

        defaultText = findViewById(R.id.deafultText);
        fabAdd = findViewById(R.id.fabAdd);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(TrackMedicineActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackMedicineActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, TrackMedicineActivity.this);
                dispatchTakePictureIntent();
            }
        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            if(imageBitmap!=null){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] profileImage = outputStream.toByteArray();

                imgString = Base64.encodeToString(profileImage,
                        Base64.DEFAULT);
                Toast.makeText(TrackMedicineActivity.this, imgString, Toast.LENGTH_SHORT).show();

                sendData();
            }else{
                imgString ="";
            }


        }


    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = ""+location.getLatitude();
        longitude = ""+location.getLongitude();
        Log.d("Location",latitude+" "+longitude);


    }

    private void sendData() {
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://142.93.223.238/profile/";
            JSONObject jsonObject = new JSONObject();
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            jsonObject.put("photo",imgString);
            jsonObject.put("loc",""+latitude+" "+longitude);
            jsonObject.put("timestamp",timeStamp);
            jsonObject.put("name","Sohail");


            Log.i("BASE64",imgString);
            Log.i("loc",""+latitude+" "+longitude);
            Log.i("timestamp",timeStamp);
            Log.i("name","Sohail");

            requestbody = jsonObject.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RESPONSE",error.toString());
                }
            });
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
