package sg.edu.rp.c302.id19034275.gettingmylocationps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient client;
    Button btnLocationUpdate, btnRemoveUpdate, btnRecord;
    TextView tvLatLng;
    String folderLocation;
    private GoogleMap map;
    MarkerOptions north;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = LocationServices.getFusedLocationProviderClient(this);
        btnLocationUpdate = findViewById(R.id.btnLocationUpdate);
        btnRemoveUpdate = findViewById(R.id.btnRemoveLocation);
        btnRecord = findViewById(R.id.btnRecords);
        tvLatLng = findViewById(R.id.tvLatLng);
        folderLocation = getFilesDir().getAbsolutePath() + "/P09PS";
        File folder = new File(folderLocation);

        if (!folder.exists()) {
            boolean result = folder.mkdir();
            if (result)
                Log.d("File Read/Write", "Folder created");
            else
                Log.d("File Read/Write", "Folder failed to create");
        } else
            Log.d("File Read/Write", "Folder already exist");

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (checkPermission()) {
                    map = googleMap;
                    client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            String msg;
                            if (location != null) {
                                msg = "Last known location: \nLatititude: " + location.getLatitude() + "\nLongtitude: " + location.getLongitude();
                                LatLng lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 11));

                                map.addMarker(new MarkerOptions().position(lastKnownLocation));
                                try {
                                    String folder = getFilesDir().getAbsolutePath() + "/P09PS";
                                    File file = new File(folder, "locationData.txt");
                                    if (file.createNewFile())
                                        Log.i("create file: ", "file created");
                                    else
                                        Log.i("create file: ", "file creation failed");
                                    FileWriter writer = new FileWriter(file, true);
                                    writer.write(location.getLatitude() + " " + location.getLongitude() + "\n");
                                    writer.flush();
                                    writer.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                msg = "No last location found";
                            }
                            tvLatLng.setText(msg);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                }

            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                String msg = "";
                if (locationResult != null) {
                    Location data = locationResult.getLastLocation();

                    msg ="Latitude: " + data.getLatitude() +
                                    "\nLongitude: " + data.getLongitude();

//                    map.clear();
                    LatLng Singapore = new LatLng(data.getLatitude(), data.getLongitude());
                    UiSettings ui = map.getUiSettings();
                    ui.setCompassEnabled(true);
                    ui.setZoomControlsEnabled(true);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Singapore, 12));

                    map.addMarker(new MarkerOptions().position(Singapore));
                    try {
                        String folder = getFilesDir().getAbsolutePath() + "/P09PS";
                        File file = new File(folder, "locationData.txt");
                        if (file.createNewFile())
                            Log.i("create file: ", "file created");
                        else
                            Log.i("create file: ", "file creation failed");
                        FileWriter writer = new FileWriter(file, true);
                        writer.write(data.getLatitude() + " " + data.getLongitude() + "\n");
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    msg = "No Location Found";
                }
                tvLatLng.setText(msg);
            }
        };

        btnLocationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean permissionResult = checkPermission();
                if (permissionResult) {
                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(30);
                    mLocationRequest.setSmallestDisplacement(500);
                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                }
            }
        });

        btnRemoveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.removeLocationUpdates(mLocationCallback);
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CheckRecord.class);
                startActivity(i);
            }
        });
    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            Log.d("permissions", "yes");
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }
    }
}