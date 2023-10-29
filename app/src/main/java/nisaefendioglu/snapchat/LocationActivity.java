package nisaefendioglu.snapchat;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import java.util.List;
import java.util.concurrent.Executor;

public class LocationActivity extends AppCompatActivity {

    private TextView errorView;
    private ListView accessPoints;
    private WifiRttManager wifiRttManager;
    private LocationActivity la = this;
    Bundle b;
    final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
    };


    private boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int a : grantResults) {
                if (a != PackageManager.PERMISSION_GRANTED) {
                    errorView.append("ERROR: PERMISSION " + a + " IS MISSING!");
                }

            }
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        b = bundle;
        super.onCreate(bundle);
        setContentView(R.layout.location_display);
        errorView = (TextView) findViewById(R.id.Error);
        accessPoints = (ListView) findViewById(R.id.AccessPoints);
        if (!hasPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
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
            errorView.append("ERROR: ACCESS FINE LOCATION IS MISSING: Insufficient Permissions");
            return;
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        List<ScanResult> scanResults = wifiManager.getScanResults().subList(0, 3);
        RangingRequest.Builder builder = new RangingRequest.Builder();
        RangingRequest.getMaxPeers();
        for (ScanResult result : scanResults) {
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
                    RangingResultsAdapter adapter = new RangingResultsAdapter(la, list);
                    accessPoints.setAdapter(adapter);
                }
            });
        }


    }

    public static class RangingResultsAdapter extends ArrayAdapter<RangingResult>{

        public RangingResultsAdapter(@NonNull Context context, List<RangingResult> resource) {
            super(context, 0, resource);
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position

            RangingResult rr = getItem(position);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if(rr.getStatus() != RangingResult.STATUS_SUCCESS){
                    return convertView;
                }
            }
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranging_result, parent, false);

            }

            // Lookup view for data population
            TextView mac = (TextView) convertView.findViewById(R.id.mac);

            TextView  time = (TextView) convertView.findViewById(R.id.time);

            TextView distance = (TextView) convertView.findViewById(R.id.distance);

            // Populate the data into the template view using the data object

            System.out.println(convertView + " IS HERE");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mac.setText(rr.getMacAddress().toString());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                float dist = (float)(rr.getDistanceMm()/1000.0);
                distance.setText("" + dist);
            }



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                time.setText("" + (float) (rr.getRangingTimestampMillis()/1000.0));
            }

            // Return the completed view to render on screen

            return convertView;

        }
    }
}
