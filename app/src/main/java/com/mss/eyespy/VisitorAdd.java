package com.mss.eyespy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VisitorAdd extends AppCompatActivity {

    EditText et_VisitorFullName, et_VisitorMobileNo, et_VisitorVehicleNo, et_Visiting_To, et_Purpose;
    Button btn_VisitorPhoto, btn_VisitorVehiclePhoto;
    ImageView iv_VisitorPhoto, iv_VisitorVehiclePhoto;
    Spinner sp_Visiting_To;
    ToggleButton tb_Vehicle;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_add);

        et_VisitorFullName = findViewById(R.id.et_VisitorFullName);
        et_VisitorMobileNo = findViewById(R.id.et_VisitorMobileNo);
        et_VisitorVehicleNo = findViewById(R.id.et_VisitorVehicleNo);
        et_Visiting_To = findViewById(R.id.et_Visiting_To);
        et_Purpose = findViewById(R.id.et_Purpose);
        btn_VisitorPhoto = findViewById(R.id.btn_VisitorPhoto);
        btn_VisitorVehiclePhoto = findViewById(R.id.btn_VisitorVehiclePhoto);
        iv_VisitorPhoto = findViewById(R.id.iv_VisitorPhoto);
        iv_VisitorVehiclePhoto = findViewById(R.id.iv_VisitorVehiclePhoto);
        sp_Visiting_To = findViewById(R.id.sp_Visiting_To);
        tb_Vehicle = findViewById(R.id.tb_Vehicle);
        linearLayout = findViewById(R.id.linearLayout);

        tb_Vehicle.setOnClickListener(view -> {
            if (tb_Vehicle.isChecked()) {
                et_VisitorVehicleNo.setVisibility(VISIBLE);
                linearLayout.setVisibility(VISIBLE);
                tb_Vehicle.setBackgroundColor(Color.GREEN);
            }else {
                et_VisitorVehicleNo.setVisibility(GONE);
                linearLayout.setVisibility(GONE);
                tb_Vehicle.setBackgroundColor(Color.GRAY);
            }
        });

    }
}