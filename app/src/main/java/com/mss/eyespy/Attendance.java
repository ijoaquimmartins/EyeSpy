package com.mss.eyespy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
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
    ImageView menu, photo;//Navigation drawer
    LinearLayout ll_Home, ll_Register, ll_Attendance, ll_Patrolling, ll_ShiftTimings, ll_Logout, ll_Exit, ll_Visitor;
    TextView tv_App_Ver_Up, tv_UserName;//Navigation drawer
    Button btn_MarkAttendance, btn_MarkAttendanceOut;
    double latitude, longitude;
    String qrData, stMassage, stInOut = InOutStatus, MarkAttendanceUrl= URL+"save-attendance", GetAttnListUrl = URL+"user-attendance-today";
    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private List<AttendanceList> attendanceListList;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    SearchView svSearch;

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
        menu.setOnClickListener(view -> {openDrawer(drawerLayout);});
        tv_UserName.setText(UserFullName);
        ImageHelper.applySavedImage(this, photo);

        ll_Home = findViewById(R.id.ll_Home);
        ll_Home.setOnClickListener(view -> redirectActivity(Attendance.this, MainActivity.class));

        ll_Register = findViewById(R.id.ll_Register);
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));

        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Attendance.setOnClickListener(view -> recreate());

        ll_Patrolling = findViewById(R.id.ll_Patrolling);
        ll_Patrolling.setOnClickListener(view -> redirectActivity(this, Patrolling.class));

        ll_Visitor = findViewById(R.id.ll_Visitor);
        ll_Visitor.setOnClickListener(view -> redirectActivity(this, Visitor.class));
        /* Navigation Drawer*/

        btn_MarkAttendance = findViewById(R.id.btn_MarkAttendance);
        btn_MarkAttendanceOut = findViewById(R.id.btn_MarkAttendanceOut);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendanceListList = new ArrayList<>();

        loadEmployeeData();

        adapter = new AttendanceAdapter(this, attendanceListList);
        recyclerView.setAdapter(adapter);

        getLocation();

        checkPermissions();

        btn_MarkAttendance.setOnClickListener(view ->{
            stInOut = "IN";
            scanQRCode();
            btn_MarkAttendance.setVisibility(GONE);
        });
        btn_MarkAttendanceOut.setOnClickListener(view -> {
            stInOut = "OUT";
            scanQRCode();
            btn_MarkAttendanceOut.setVisibility(GONE);
        });
        svSearch = findViewById(R.id.svSearch);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout); //On pause close Navigation Drawer
    }
    private void loadEmployeeData() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(GetAttnListUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Handle different types of exceptions for better user feedback
                    if (e instanceof UnknownHostException) {
                        Toast.makeText(Attendance.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else if (e instanceof SocketTimeoutException) {
                        Toast.makeText(Attendance.this, "Connection Timeout", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Attendance.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    byte[] myid = Base64.getDecoder().decode(MobileNo);
                    String decodedString = new String(myid);
                    boolean found = false;
                    String mystatus = "";
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);

                        attendanceListList.clear();

                        for (int i = 0; i< jsonArray.length(); i++) {
                            JSONObject attnObject = jsonArray.getJSONObject(i);

                            String id = attnObject.optString("id","");
                            String name = attnObject.optString("name","");
                            String photo = attnObject.optString("photo","");
                            String usertype = attnObject.optString("usertype","");
                            String mobile_no = attnObject.optString("mobile_no","");
                            String login_time = attnObject.optString("login_time","");
                            String status = attnObject.optString("status","");

                            AttendanceList attendance = new AttendanceList(id, name, usertype, mobile_no, login_time, photo, status);

                            boolean finalFound = found;
                            String finalMystatus = mystatus;
                            runOnUiThread(()->{
                                attendanceListList.add(attendance);
                                AttendanceAdapter AttendanceAdapter = new AttendanceAdapter(Attendance.this, attendanceListList);
                                recyclerView.setAdapter(AttendanceAdapter);

                                if (finalFound && finalMystatus.equals("Absent")) {
                                    btn_MarkAttendanceOut.setVisibility(GONE);
                                    btn_MarkAttendance.setVisibility(VISIBLE);
                                } else if (!finalFound) {
                                    btn_MarkAttendanceOut.setVisibility(GONE);
                                    btn_MarkAttendance.setVisibility(VISIBLE);
                                }else if (finalFound && !finalMystatus.isEmpty()){
                                    btn_MarkAttendanceOut.setVisibility(VISIBLE);
                                    btn_MarkAttendance.setVisibility(GONE);
                                }
                            });

                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject obj = jsonArray.getJSONObject(j);
                                String mymobile = obj.getString("mobile_no");

                                if (mymobile.equals(decodedString)) {
                                    mystatus = obj.getString("status");
                                    found = true;
                                    break; // exit loop once found
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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
                    if(stInOut.equals("IN")){
                        MarkAttendance();
                    } else if (stInOut.equals("OUT")) {
                        MarkAttendance();
                    }
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

                btn_MarkAttendance.setEnabled(true);
                btn_MarkAttendanceOut.setEnabled(true);
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
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
        String formtype = "";

        if(stInOut.equals("IN")) {
            formtype = "MOBILEADD";
        } else if (stInOut.equals("OUT")) {
            formtype = "OUTTIME";
        }
        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("formtype", formtype)
                .add("code", Base64.getEncoder().encodeToString((qrData.trim()).getBytes()))
                .add("latitude", String.valueOf(latitude))
                .add("longitude", String.valueOf(longitude))
                .add("device_id", mId)
                .add("user_id", UserId)
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