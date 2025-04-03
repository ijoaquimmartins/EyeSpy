package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mss.eyespy.GlobalClass.closeDrawer;
import static com.mss.eyespy.GlobalClass.exitApp;
import static com.mss.eyespy.GlobalClass.logout;
import static com.mss.eyespy.GlobalClass.openDrawer;
import static com.mss.eyespy.GlobalClass.redirectActivity;
import static com.mss.eyespy.GlobalClass.setAppVersion;
import static com.mss.eyespy.SharedPreferences.URL;
import static com.mss.eyespy.SharedPreferences.UserFullName;
import static com.mss.eyespy.SharedPreferences.UserTableId;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Visitor extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu, photo;
    LinearLayout ll_Home, ll_Register, ll_Attendance, ll_Patrolling, ll_ShiftTimings, ll_Logout, ll_Exit, ll_Visitor;
    TextView tv_App_Ver_Up, tv_UserName;
    FloatingActionButton floating_action_button,floating_action_accept;
    CheckBox cb_GetLayout;
    LinearLayout ll_VisitorDateWise;
    TextView tv_FromDate, tv_ToDate;
    Button btn_FatchData;
    String visitorListUrl = URL+"get-visitor-list";
    private RecyclerView recyclerView;
    private List<VisitorList> visitorLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);

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
        ll_Attendance.setOnClickListener(view -> redirectActivity(this, Attendance.class));

        ll_Patrolling = findViewById(R.id.ll_Patrolling);
        ll_Patrolling.setOnClickListener(view -> redirectActivity(this, Patrolling.class));

        ll_Visitor = findViewById(R.id.ll_Visitor);
        ll_Visitor.setOnClickListener(view -> recreate());

        floating_action_button = findViewById(R.id.floating_action_button);
        floating_action_button.setOnClickListener(view -> {
            Intent i = new Intent(this, VisitorAdd.class);
            startActivity(i);
        });
        floating_action_accept = findViewById(R.id.floating_action_accept);
        floating_action_accept.setOnClickListener(view -> {
            Intent i = new Intent(this, VisitorConfirm.class);
            startActivity(i);
        });

        cb_GetLayout = findViewById(R.id.cb_GetLayout);
        ll_VisitorDateWise = findViewById(R.id.ll_VisitorDateWise);
        tv_FromDate = findViewById(R.id.tv_FromDate);
        tv_ToDate = findViewById(R.id.tv_ToDate);
        btn_FatchData = findViewById(R.id.btn_FatchData);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        tv_FromDate.setText(sdf.format(new Date()));
        tv_ToDate.setText(sdf.format(new Date()));

        if (cb_GetLayout.isChecked()){
            ll_VisitorDateWise.setVisibility(VISIBLE);
        }else {
            ll_VisitorDateWise.setVisibility(GONE);
        }
        cb_GetLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cb_GetLayout.isChecked()){
                    ll_VisitorDateWise.setVisibility(VISIBLE);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    tv_FromDate.setText(sdf.format(new Date()));
                    tv_ToDate.setText(sdf.format(new Date()));
                }else {
                    ll_VisitorDateWise.setVisibility(GONE);
                    tv_FromDate.setText("");
                    tv_ToDate.setText("");
                }
            }
        });
        tv_FromDate.setOnClickListener(view -> showDatePicker("1"));
        tv_ToDate.setOnClickListener(view -> showDatePicker("2"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        visitorLists = new ArrayList<>();
        btn_FatchData.setOnClickListener(view -> getVisitorList());
        getVisitorList();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                //    recreate();
                    getVisitorList();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
    private void showDatePicker(String date_type) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String formattedDate = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
                if(date_type.equals("1")){
                    tv_FromDate.setText(formattedDate);
                }else if (date_type.equals("2")){
                    tv_ToDate.setText(formattedDate);
                }
            }, year, month, day);
        datePickerDialog.show();
    }
    private void getVisitorList(){
        String fromDate = tv_FromDate.getText().toString().trim();
        String toDate = tv_ToDate.getText().toString().trim();
        String userId = SharedPreferences.UserId;
        String UserType = SharedPreferences.UserType;

        // Create client
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Data form body
        FormBody formBody = new FormBody.Builder()
                .add("src_fromdate", fromDate)
                .add("src_todate", toDate)
                .add("user_id", userId)
                .add("user_type", UserType)
                .build();

        // Put all together to send data
        Request request = new Request.Builder()
                .url(visitorListUrl)
                .post(formBody)
                .build();

        // Connect to client and get response
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Handle different types of exceptions for better user feedback
                    if (e instanceof UnknownHostException) {
                        Toast.makeText(Visitor.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else if (e instanceof SocketTimeoutException) {
                        Toast.makeText(Visitor.this, "Connection Timeout", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Visitor.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string().trim();
                    try {
                        JSONArray jsonResponseArray = new JSONArray(responseBody);

                        // Clear previous visitor data to load fresh data
                        visitorLists.clear();

                        for (int i = 0; i < jsonResponseArray.length(); i++) {
                            JSONObject visitorJson = jsonResponseArray.getJSONObject(i);

                            String id = visitorJson.optString("id", "");
                            String code = visitorJson.optString("code","");
                            String visitingTo = visitorJson.optString("visiting_to", "");
                            String flatNo = visitorJson.optString("flat_no", "");
                            String visitorsName = visitorJson.optString("visitors_name", "");
                            String contactNo = visitorJson.optString("contact_no", "");
                            String photo = visitorJson.optString("photo", "");
                            String vehicleno = visitorJson.optString("vehicleno", "");
                            String vehiclePhoto = visitorJson.optString("vehicle_photo", "");
                            String inDatetime = visitorJson.optString("in_datetime", "");
                            String outDatetime = visitorJson.optString("out_datetime", "");
                            String purpose = visitorJson.optString("purpose", "");
                            String confirmBy = visitorJson.optString("confirm_by", "");

                            VisitorList visitor = new VisitorList(id, code, visitingTo, flatNo, visitorsName, contactNo, photo, vehicleno, vehiclePhoto, inDatetime, outDatetime, purpose, confirmBy);

                            runOnUiThread(() ->{
                                visitorLists.add(visitor);
                                VisitorAdapter VisitorAdapter = new VisitorAdapter(Visitor.this, visitorLists);
                                recyclerView.setAdapter(VisitorAdapter);
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(Visitor.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Visitor.this, "Server error", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}