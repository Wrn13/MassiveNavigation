package massive_navigation.snapchat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


import android.content.IntentSender;
import android.widget.Toast;

import com.example.massivenavigationnodes.Node;
import com.example.massivenavigationnodes.NodeManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.Task;
import com.example.massivenavigationnodes.SensorActivity;

import java.io.IOException;

import massive_navigation.snapchat.Adapter.MainPagerAdapter;
import massive_navigation.snapchat.Fragment.Camera;

import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView chat_btn, story_btn, settings;
    double latitude, longitude;
    private LocationRequest locationRequest;
    private long prevTime = System.currentTimeMillis();
    TextToSpeech textToSpeech;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NodeManager.getInstance().setContentResolver(getContentResolver());
        NodeManager.getInstance().setContext(getApplicationContext());

        try {
            NodeManager.getInstance().testNodes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        final ViewPager viewPager = findViewById(R.id.ma_view_pager);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setCurrentItem(1);
        chat_btn = findViewById(R.id.chat_btn);
        story_btn = findViewById(R.id.story_btn);
        settings = findViewById(R.id.settings);


        chat_btn.setOnClickListener(v -> {
            textToSpeech.speak("Pick where to go.",TextToSpeech.QUEUE_FLUSH,null);
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0, true);
            }
        });

        story_btn.setOnClickListener(v -> {
            textToSpeech.speak("Check out other places.",TextToSpeech.QUEUE_FLUSH,null);
            if (viewPager.getCurrentItem() != 2) {
                viewPager.setCurrentItem(2, true);
            }
        });

        settings.setOnClickListener(view -> {
            textToSpeech.speak("Settings.",TextToSpeech.QUEUE_FLUSH,null);
            if (viewPager.getCurrentItem() != 3) {
                viewPager.setCurrentItem(3, true);
            }
        });
        getGPS();

        NodeManager.getInstance().findShortestPath(0, 1);

        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

    }

    private void getGPS(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    turnOnGPS();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager;
        boolean isEnabled;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public void onResume() {
       super.onResume();
        if(System.currentTimeMillis()- prevTime >1000){
            getGPS();
            prevTime = System.currentTimeMillis();
        }
    }


    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {

            try {
                task.getResult(ApiException.class);
                Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });
    }

}
