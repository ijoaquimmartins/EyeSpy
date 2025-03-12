package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class Attendance extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu, photo;
    LinearLayout ll_Home, ll_Register, ll_ShiftTimings, ll_Attendance, ll_Logout, ll_Exit ;
    TextView tv_App_Ver_Up, tv_UserName;

    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        drawerLayout = findViewById(R.id.layoutdrawer);
        menu = findViewById(R.id.main_menu);
        photo = findViewById(R.id.iv_Photo);
        tv_UserName = findViewById(R.id.tv_UserName);
        ll_Home = findViewById(R.id.ll_Home);
        ll_Register = findViewById(R.id.ll_Register);
        ll_ShiftTimings = findViewById(R.id.ll_ShiftTimings);
        ll_Attendance = findViewById(R.id.ll_Attendance);
        ll_Logout = findViewById(R.id.ll_Logout);
        ll_Exit = findViewById(R.id.ll_Exit);
        tv_App_Ver_Up = findViewById(R.id.tv_App_Ver_Up);

        ll_Logout.setOnClickListener(view -> {logout(this);});
        ll_Exit.setOnClickListener(view -> {exitApp(this);});
        setAppVersion(this, tv_App_Ver_Up);

        menu.setOnClickListener(view -> {openDrawer(drawerLayout);});
        ll_Attendance.setOnClickListener(view -> recreate());
        ll_Home.setOnClickListener(view -> redirectActivity(this, MainActivity.class));
        ll_Register.setOnClickListener(view -> redirectActivity(this, RegisterActivity.class));


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load Employees
        employeeList = new ArrayList<>();
        loadEmployeeData();

        adapter = new EmployeeAdapter(this, employeeList);
        recyclerView.setAdapter(adapter);

    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void loadEmployeeData() {
        // Static Data (Replace with API Data)
        employeeList.add(new Employee("Manoj Kumar (Security Guard)", "9876543210", "12/03/2025 09:00 AM", "http://100.168.10.74/photo/Manoj.jpg"));
        employeeList.add(new Employee("Paveen (House Keeping)", "8765432109", "12/03/2025 09:15 AM", "http://100.168.10.74/photo/Paveen.jpg"));
        employeeList.add(new Employee("Malika (House Keeping)", "7654321098", "12/03/2025 09:30 AM", "http://100.168.10.74/photo/Malika.jpg"));
    }
}