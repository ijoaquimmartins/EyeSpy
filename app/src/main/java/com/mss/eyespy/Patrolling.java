package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Patrolling extends AppCompatActivity {

    DrawerLayout drawerLayout; //Navigation drawer
    ImageView menu, photo;//Navigation drawer
    LinearLayout ll_Home, ll_Register, ll_Attendance, ll_Patrolling, ll_ShiftTimings, ll_Logout, ll_Exit, ll_Visitor;
    TextView tv_App_Ver_Up, tv_UserName;//Navigation drawer
    private Context context;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private PatrollingAdaptar patrollingAdaptar;
    private List<PatrollingList> patrollingLists;
    String qrData, stMassage, uploadqQrData = URL+"save-qrscan";
    double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    FloatingActionButton fabQrScan, fabUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrolling);

        /* Navigation Drawer*/
        drawerLayout = findViewById(R.id.layoutdrawer);
        menu = findViewById(R.id.main_menu);
        photo = findViewById(R.id.iv_Photo);
        tv_UserName = findViewById(R.id.tv_UserName);

        ll_ShiftTimings = findViewById(R.id.ll_ShiftTimings);
        ll_Logout = findViewById(R.id.ll_Logout);
        ll_Exit = findViewById(R.id.ll_Exit);
        tv_App_Ver_Up = findViewById(R.id.tv_App_Ver_Up);

        ll_Logout.setOnClickListener(view -> {logout(this);});
        ll_Exit.setOnClickListener(view -> {exitApp(this);});
        setAppVersion(this, tv_App_Ver_Up);
        menu.setOnClickListener(view -> {openDrawer(drawerLayout);});
        tv_UserName.setText(UserFullName);
        ImageHelper.applySavedImage(this, photo);

        ll_Home = findViewById(R.id.ll_Home);
        ll_Home.setOnClickListener(view -> redirectActivity(Patrolling.this, MainActivity.class));

        ll_Register = findViewById(R.id.ll_Register);
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));

        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Attendance.setOnClickListener(view -> redirectActivity(this, Attendance.class));

        ll_Patrolling = findViewById(R.id.ll_Patrolling);
        ll_Patrolling.setOnClickListener(view -> recreate());

        ll_Visitor = findViewById(R.id.ll_Visitor);
        ll_Visitor.setOnClickListener(view -> redirectActivity(this, Visitor.class));
        /* Navigation Drawer*/

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);
        fabQrScan = findViewById(R.id.fab_qrScan);
        fabUpload = findViewById(R.id.fab_upload);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        patrollingLists = databaseHelper.getAllScannedQRs();

        patrollingAdaptar = new PatrollingAdaptar(this, patrollingLists);
        recyclerView.setAdapter(patrollingAdaptar);

        fabQrScan.setOnClickListener(view -> checkPermissions());
        fabUpload.setOnClickListener(view -> uploadQrDataToServer());

/*        if (isNetworkAvailable()) {
             uploadQrDataToServer();
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout); //On pause close Navigation Drawer

//        if (isNetworkAvailable()) {
//            uploadQrDataToServer();
//        }
    }
    // Closing of Database Helper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    //Launch, Scan and send qr data to server
    public void scanQRCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        getLocation();
        qrCodeLauncher.launch(options);
    }
    //Open Scanner
    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    qrData = result.getContents();
                    getLocation();
                    showConfirmationDialog();
                }
            });
    //Open scanner confirmation dialog
    public void showConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Confirm Scan")
            .setMessage("Scan completed")
            .setPositiveButton("Yes", (dialog, which) -> {
                String stlatitude = Double.valueOf(latitude).toString();
                String stlongitude = Double.valueOf(longitude).toString();
                databaseHelper.insertScannedQR(qrData, stlatitude, stlongitude, "0");
                patrollingLists.clear();
                patrollingLists.addAll(databaseHelper.getAllScannedQRs());
                patrollingAdaptar.notifyDataSetChanged();
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .show();
    }

    //Get GPS location
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(Patrolling.this, "Location not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    Toast.makeText(Patrolling.this, "Location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            getLocation();
            scanQRCode();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanQRCode();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Uploade the scanned qr code data
    public void uploadQrDataToServer() {

        List<QrData> qrDataList = databaseHelper.getUnuploadedQrData();

        if (qrDataList.isEmpty()) {
            Log.d("UPLOAD", "No new data to upload.");
            return;
        }

        JSONArray jsonArray = new JSONArray();
        for (QrData data : qrDataList) {
            jsonArray.put(data.toJson());
        }

        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("data", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(jsonPayload.toString(), JSON);

        Request request = new Request.Builder()
                .url(uploadqQrData)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("UPLOAD", "Failed to upload data: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray responseArray = new JSONArray(responseBody);
                        databaseHelper.updateQrDataFromServer(responseArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("UPLOAD", "JSON parsing error");
                    }
                } else {
                    Log.e("UPLOAD", "Server error: " + response.code());
                }
            }
        });
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}