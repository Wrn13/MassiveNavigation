package nisaefendioglu.snapchat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import nisaefendioglu.snapchat.Adapter.MainPagerAdapter;
import nisaefendioglu.snapchat.Fragment.Camera;
import androidx.annotation.NonNull;
import android.content.IntentSender;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {

    ImageView CaptureBtn, chat_btn, story_btn, settings;

    private TextView AddressText;
    private Button LocationButton;
    private LocationRequest locationRequest;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddressText = findViewById(R.id.addressText);
        LocationButton = findViewById(R.id.locationButton);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        /*final ViewPager viewPager = findViewById(R.id.ma_view_pager);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setCurrentItem(1);
        CaptureBtn = findViewById(R.id.capture_photo_btn);
        chat_btn = findViewById(R.id.chat_btn);
        story_btn = findViewById(R.id.story_btn);
        settings = findViewById(R.id.settings);

        CaptureBtn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1, true);
            } else {
                Camera fragment = (Camera) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.ma_view_pager + ":" + viewPager.getCurrentItem());
                fragment.TakePhoto();
            }
        });


        chat_btn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0, true);
            }
        });

        story_btn.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 2) {
                viewPager.setCurrentItem(2, true);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 3) {
                    viewPager.setCurrentItem(3, true);
                }
            }
        });*/



        LocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

                                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                                int index = locationResult.getLocations().size() - 1;
                                                double latitude = locationResult.getLocations().get(index).getLatitude();
                                                double longitude = locationResult.getLocations().get(index).getLongitude();
                                                System.out.println("Latitude: " + latitude + "/n" + "Longitude: " + longitude);
                                                AddressText.setText("Latitude: " + latitude + "n" + "Longitude: " + longitude);
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
        });
    }

            private boolean isGPSEnabled() {
                LocationManager locationManager = null;
                boolean isEnabled = false;

                if (locationManager == null) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                }

                isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                return isEnabled;

        }


    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
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
            }
        });

    }

}
