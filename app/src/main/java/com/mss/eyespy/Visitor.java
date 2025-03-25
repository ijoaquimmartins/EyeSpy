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
import static com.mss.eyespy.SharedPreferences.UserFullName;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        if (cb_GetLayout.isChecked()){
            ll_VisitorDateWise.setVisibility(VISIBLE);
        }else {
            ll_VisitorDateWise.setVisibility(GONE);
            tv_FromDate.setText("");
            tv_ToDate.setText("");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

}