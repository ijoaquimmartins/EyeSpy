package com.mss.eyespy;

import static android.view.View.VISIBLE;

import android.Manifest;
import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Attendance extends AppCompatActivity {

    DrawerLayout drawerLayout; //Navigation drawer
    ImageView menu, photo; //Navigation drawer
    LinearLayout ll_Home, ll_Register, ll_ShiftTimings, ll_Attendance, ll_Patrolling, ll_Logout, ll_Exit ; //Navigation drawer
    TextView tv_App_Ver_Up, tv_UserName; //Navigation drawer
    Button btn_MarkAttendance;
    double latitude, longitude;
    String qrData, stMassage, MarkAttendanceUrl= URL+"user/save";

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private List<AttendanceList> attendanceListList;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

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
        tv_UserName.setText(UserFullName);

        menu.setOnClickListener(view -> {openDrawer(drawerLayout);});

        ll_Home = findViewById(R.id.ll_Home);
        ll_Home.setOnClickListener(view -> redirectActivity(this, MainActivity.class));

        ll_Register = findViewById(R.id.ll_Register);
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));

        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Attendance.setOnClickListener(view -> recreate());

        ll_Patrolling = findViewById(R.id.ll_Patrolling);
        ll_Patrolling.setOnClickListener(view -> redirectActivity(this, Patrolling.class));
        /* Navigation Drawer*/

        btn_MarkAttendance = findViewById(R.id.btn_MarkAttendance);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceListList = new ArrayList<>();
        loadEmployeeData();

        adapter = new AttendanceAdapter(this, attendanceListList);
        recyclerView.setAdapter(adapter);

        getLocation();
        checkPermissions();
        btn_MarkAttendance.setOnClickListener(view -> scanQRCode());
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout); //On pause close Navigation Drawer
    }
    private void loadEmployeeData() {
        // Static Data (Replace with API Data)
        attendanceListList.add(new AttendanceList("JJ (Security Guard)", "9860294407", "12/03/2025 09:00 AM", "http://100.168.10.74/photo/Manoj.jpg"));
        attendanceListList.add(new AttendanceList("JJ Office (House Keeping)", "8380015831", "12/03/2025 09:15 AM", "http://100.168.10.74/photo/Paveen.jpg"));
        attendanceListList.add(new AttendanceList("Paritosh (House Keeping)", "9284839598", "12/03/2025 09:30 AM", "http://100.168.10.74/photo/Malika.jpg"));
    }

    //Launch, Scan and send qr data to server
    private void scanQRCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);

        qrCodeLauncher.launch(options);
    }
    //Open Scanner
    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    qrData = result.getContents();
                    showConfirmationDialog();
                }
            });
    //Open scanner confirmation dialog
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Attendance")
                .setMessage("Do you want to mark attendance?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    MarkAttendance();
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
        locationRequest.setInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(Attendance.this, "Location not found", Toast.LENGTH_SHORT).show();
                    return;
                }
 //               Toast.makeText(Attendance.this, "Location found", Toast.LENGTH_SHORT).show();
                  btn_MarkAttendance.setEnabled(true);
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    sendAttendanceToServer(qrData, latitude, longitude);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
        //    scanQRCode();
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
    private void MarkAttendance(){

        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("qrData", qrData)
                .add("location", String.valueOf(latitude +", "+ longitude))
                .add("deviceid", mId)
                .add("id", UserTableId)
                .build();
        Request request = new Request.Builder()
                .url(MarkAttendanceUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Attendance.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string().trim();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String error = jsonResponse.optString("error", "");
                        String msg = jsonResponse.optString("msg", "");
                        runOnUiThread(() -> {
                            if(msg.equalsIgnoreCase("success")){
                                stMassage = msg;
                                showAlertDialog();
                            } else if (error.equalsIgnoreCase("failed")) {
                                stMassage = error;
                                showAlertDialog();
                            }else{
                                stMassage = msg;
                                showAlertDialog();
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(Attendance.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }else {
                    runOnUiThread(() ->
                            Toast.makeText(Attendance.this, "Server error", Toast.LENGTH_LONG).show());
                }
            }
        });
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