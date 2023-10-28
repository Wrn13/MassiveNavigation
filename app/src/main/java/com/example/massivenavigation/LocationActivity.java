package com.example.massivenavigation;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executor;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class LocationActivity extends Activity {

    private TextView errorView;
    private ListView accessPoints;
    private WifiRttManager wifiRttManager;
    private LocationActivity la = this;
    final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };
    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissions(PERMISSIONS)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }
    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.location_display);
        errorView = (TextView) findViewById(R.id.Error);
        accessPoints = (ListView) findViewById(R.id.AccessPoints);
        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        registerForActivityResult(multiplePermissionsContract){
            Log.d("PERMISSIONS", "Launcher result: " + isGranted);
            if (isGranted.contains(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionLauncher.launch(PERMISSIONS);
            }
        });

        askPermissions(multiplePermissionLauncher);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            errorView.setText("ERROR: Context is invalid.");
            return;
        }
        boolean contextHasWIFIRTT = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
        if (!contextHasWIFIRTT) {
            errorView.setText("ERROR: Context does not have WIFI RTT");
            return;
        }
        wifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        IntentFilter filter =
                new IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED);
        if (!wifiRttManager.isAvailable()) {
            errorView.setText("ERROR: System does not have WIFI_RTT_SERVICE");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            errorView.setText("ERROR: Insufficient Permissions");
            return;
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        RangingRequest.Builder builder = new RangingRequest.Builder();
        for(ScanResult result :  scanResults){
            builder.addAccessPoint(result);
        }

        RangingRequest req = builder.build();
        Executor executor = getMainExecutor();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wifiRttManager.startRanging(req, executor, new RangingResultCallback() {

                @Override
                public void onRangingFailure(int code) {
                    errorView.setText("ERROR: Ranging Request Failed");
                }

                @Override
                public void onRangingResults(@NonNull List<RangingResult> list) {
                    ArrayAdapter<RangingResult> adapter = new ArrayAdapter<>(la, R.layout.location_display, list);
                    accessPoints.setAdapter(adapter);
                }
            });
        }


    }
}
