package com.mss.eyespy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mss.eyespy.SharedPreferences.*;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VisitorOut extends AppCompatActivity {

    ImageView ivVisitorPhoto, ivVehiclePhoto;
    TextView tvVisitorName, tvVisitorNo, tvVisitingTo, tvVisitingLocation, tvInTime, tvOutTime, tvConfirmed, tvPurpose, tvVehicleno;
    Button btnOut, btnCancel;
    LinearLayout llVehicledetails;
    String formid, stMassage, stVisitorOutUrl = URL + "visitor/save";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_out);

        ivVisitorPhoto = findViewById(R.id.iv_VisitorPhoto);
        ivVehiclePhoto  = findViewById(R.id.ivVehiclePhoto);
        tvVisitorName  = findViewById(R.id.tvVisitorName);
        tvVisitorNo  = findViewById(R.id.tvVisitorNo);
        tvVisitingTo  = findViewById(R.id.tvVisitingTo);
        tvVisitingLocation  = findViewById(R.id.tvVisitingLocation);
        tvInTime  = findViewById(R.id.tvInTime);
        tvOutTime  = findViewById(R.id.tvOutTime);
        tvConfirmed  = findViewById(R.id.tvConfirmed);
        tvPurpose  = findViewById(R.id.tvPurpose);
        tvVehicleno   = findViewById(R.id.tvVehicleno);
        btnOut  = findViewById(R.id.btnOut);
        btnCancel  = findViewById(R.id.btnCancel);
        llVehicledetails = findViewById(R.id.llVehicledetails);

        Bundle bundle = getIntent().getExtras();

        Glide.with(this).load(ImageURL + bundle.getString("photo")).into(ivVisitorPhoto);

        formid = bundle.getString("id");
        tvVisitorName.setText(bundle.getString("visitorname"));
        tvVisitorNo.setText(bundle.getString("contactno"));
        tvVisitingTo.setText(bundle.getString("visitingto"));
        tvVisitingLocation.setText(bundle.getString("location"));
        tvInTime.setText(bundle.getString("idatetime"));

        String outdatetime = bundle.getString("outdatetime");
        String confirmed = bundle.getString("confirmed");



        if (confirmed.equals("NOT CONFIRMED")){
            btnOut.setVisibility(GONE);
            tvConfirmed.setText(bundle.getString("confirmed"));
        }else{
            btnOut.setVisibility(VISIBLE);
            tvConfirmed.setText(bundle.getString("confirmed"));

            if (outdatetime != null ) {
                btnOut.setVisibility(GONE);
                tvOutTime.setVisibility(VISIBLE);
                tvOutTime.setText(outdatetime);
            } else {
                btnOut.setVisibility(VISIBLE);
                tvOutTime.setVisibility(GONE);
            }
        }

        tvPurpose.setText(bundle.getString("purpose"));

        String vehicleno = bundle.getString("vehicleno");

        if (vehicleno != null || !vehicleno.isEmpty()){
            llVehicledetails.setVisibility(VISIBLE);
            tvVehicleno.setText(bundle.getString("vehicleno"));
            if (!bundle.getString("vehiclephoto").equals("")){
                Glide.with(this).load(ImageURL + bundle.getString("vehiclephoto")).into(ivVehiclePhoto);
            }
        }

        btnOut.setOnClickListener(view -> visitorOut() );
        btnCancel.setOnClickListener(view -> finish());

        GlobalClass.DateTimeUtils dateTimeUtils = new GlobalClass.DateTimeUtils();
        boolean isManual = dateTimeUtils.isDateTimeSetManually(this);

        if (isManual) {
            stMassage = "Please set Date Time and Time Zone automatically";
            showAlertDialog();
        }
    }
    private void visitorOut(){

        GlobalClass.DateTimeUtils dateTimeUtils = new GlobalClass.DateTimeUtils();
        boolean isManual = dateTimeUtils.isDateTimeSetManually(this);

        if (isManual) {
            stMassage = "Please set Date Time and Time Zone automatically";
            showAlertDialog();
        }else{
            String editedDatetime;
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            editedDatetime = sdf.format(new Date());

            // Create client
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Data form body
            FormBody formBody = new FormBody.Builder()
                    .add("formid", formid)
                    .add("formtype", "OUTTIME")
                    .add("updated_by", UserId)
                    .build();

            // Put all together to send data
            Request request = new Request.Builder()
                    .url(stVisitorOutUrl)
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
                            Toast.makeText(VisitorOut.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                        } else if (e instanceof SocketTimeoutException) {
                            Toast.makeText(VisitorOut.this, "Connection Timeout", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VisitorOut.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
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
                                if(!msg.equals("")){
                                    stMassage = msg;
                                    showAlertDialog();
                                } else if (!error.equals("")) {
                                    stMassage = error;
                                    showAlertDialog();
                                }
                            });
                        } catch (JSONException e) {
                            runOnUiThread(() -> Toast.makeText(VisitorOut.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(VisitorOut.this, "Server error", Toast.LENGTH_LONG).show());
                    }
                }
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}