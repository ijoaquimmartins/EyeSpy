package com.mss.eyespy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.CompoundButton.*;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class VisitorAdd extends AppCompatActivity {

    EditText et_VisitorFullName, et_VisitorMobileNo, et_VisitorVehicleNo, et_Visiting_To, et_Visiting_Location, et_Purpose;
    Button btn_VisitorPhoto, btn_VisitorVehiclePhoto, btn_Add, btn_cancel;
    ImageView iv_VisitorPhoto, iv_VisitorVehiclePhoto;
    SwitchCompat sw_Vehicle;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_add);

        et_VisitorFullName = findViewById(R.id.et_VisitorFullName);
        et_VisitorMobileNo = findViewById(R.id.et_VisitorMobileNo);
        et_VisitorVehicleNo = findViewById(R.id.et_VisitorVehicleNo);
        et_Visiting_To = findViewById(R.id.et_Visiting_To);
        et_Visiting_Location = findViewById(R.id.et_Visiting_Location);
        et_Purpose = findViewById(R.id.et_Purpose);
        btn_VisitorPhoto = findViewById(R.id.btn_VisitorPhoto);
        btn_VisitorVehiclePhoto = findViewById(R.id.btn_VisitorVehiclePhoto);
        iv_VisitorPhoto = findViewById(R.id.iv_VisitorPhoto);
        iv_VisitorVehiclePhoto = findViewById(R.id.iv_VisitorVehiclePhoto);
        sw_Vehicle = findViewById(R.id.sw_Vehicle);
        linearLayout = findViewById(R.id.linearLayout);
        btn_Add = findViewById(R.id.btn_Add);
        btn_cancel = findViewById(R.id.btn_cancel);

        sw_Vehicle.setChecked(true);

        sw_Vehicle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    et_VisitorVehicleNo.setVisibility(VISIBLE);
                    linearLayout.setVisibility(VISIBLE);
                }else{
                    et_VisitorVehicleNo.setVisibility(GONE);
                    linearLayout.setVisibility(GONE);
                }
            }
        });

        btn_cancel.setOnClickListener(view -> this.finish());


    }


}