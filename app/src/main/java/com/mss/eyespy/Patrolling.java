package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Patrolling extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu, photo;
    LinearLayout ll_Home, ll_Register, ll_Attendance, ll_Patrolling, ll_ShiftTimings, ll_Logout, ll_Exit ;
    TextView tv_App_Ver_Up, tv_UserName;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private PatrollingAdaptar patrollingAdaptar;
    private List<PatrollingList> patrollingLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrolling);
        drawerLayout = findViewById(R.id.layoutdrawer);
        menu = findViewById(R.id.main_menu);
        photo = findViewById(R.id.iv_Photo);
        tv_UserName = findViewById(R.id.tv_UserName);
        ll_Home = findViewById(R.id.ll_Home);
        ll_Register = findViewById(R.id.ll_Register);
        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Patrolling = findViewById(R.id.ll_Patrolling);
        ll_ShiftTimings = findViewById(R.id.ll_ShiftTimings);
        ll_Logout = findViewById(R.id.ll_Logout);
        ll_Exit = findViewById(R.id.ll_Exit);
        tv_App_Ver_Up = findViewById(R.id.tv_App_Ver_Up);

        ll_Logout.setOnClickListener(view -> {logout(this);});
        ll_Exit.setOnClickListener(view -> {exitApp(this);});
        setAppVersion(this, tv_App_Ver_Up);
        menu.setOnClickListener(view -> {openDrawer(drawerLayout);});
        tv_UserName.setText(UserFullName);

        ll_Home.setOnClickListener(view -> redirectActivity(this, MainActivity.class));
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));
        ll_Attendance.setOnClickListener(view -> redirectActivity(this, Attendance.class));
        ll_Patrolling.setOnClickListener(view -> recreate());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);
        getTimingsList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void getTimingsList(){
        patrollingLists = databaseHelper.getTimings();
        patrollingAdaptar = new PatrollingAdaptar(this, patrollingLists); // Pass context
        recyclerView.setAdapter(patrollingAdaptar);
    }

}