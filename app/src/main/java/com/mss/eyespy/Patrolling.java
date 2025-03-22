package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Patrolling extends AppCompatActivity {

    private Context context;
    DrawerLayout drawerLayout; //Navigation drawer
    ImageView menu, photo;//Navigation drawer
    LinearLayout ll_Home, ll_Register, ll_Attendance, ll_Patrolling, ll_ShiftTimings, ll_Logout, ll_Exit ; //Navigation drawer
    TextView tv_App_Ver_Up, tv_UserName; //Navigation drawer
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private PatrollingAdaptar patrollingAdaptar;
    private List<PatrollingList> patrollingLists;
    String qrid, qrData, stMassage, ScanQRUrl = URL+"user/save";
    double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

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

        ll_Home = findViewById(R.id.ll_Home);
        ll_Home.setOnClickListener(view -> redirectActivity(this, MainActivity.class));

        ll_Register = findViewById(R.id.ll_Register);
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));

        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Attendance.setOnClickListener(view -> recreate());

        ll_Patrolling.setOnClickListener(view -> recreate());
        /* Navigation Drawer  */

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);


    }


    @Override
    protected void onPause() {
        super.onPause();

        closeDrawer(drawerLayout); //On pause close Navigation Drawer

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
    public void scanQRCode(String qrid) {
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

                    uploadQrScanData();

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
                    Toast.makeText(Patrolling.this, "Location not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                 Toast.makeText(Patrolling.this, "Location found", Toast.LENGTH_SHORT).show();

                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };
    }

    //Uploade the scanned qr code data
    private void uploadQrScanData(){
        String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //Create client
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        //Data formbody
        FormBody formBody = new FormBody.Builder()
                .add("qrData", qrData)
                .add("location", String.valueOf(latitude +", "+ longitude))
                .add("deviceid", mId)
                .add("id", UserTableId)
                .build();
        //Put all together to send data
        Request request = new Request.Builder()
                .url(ScanQRUrl)
                .post(formBody)
                .build();
        //connect to client and get responce
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Patrolling.this, e.getMessage(), Toast.LENGTH_LONG).show());
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

                                AlarmReceiver alarmReceiver = new AlarmReceiver(); //Connect to AlarmReceiver
                                alarmReceiver.clearAlarm(context, Integer.parseInt(qrid)); // Call the Clear Alarm Function

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
                        runOnUiThread(() -> Toast.makeText(Patrolling.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }else {
                    runOnUiThread(() ->
                            Toast.makeText(Patrolling.this, "Server error", Toast.LENGTH_LONG).show());
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