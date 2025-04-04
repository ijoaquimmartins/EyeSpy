package com.mss.eyespy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mss.eyespy.SharedPreferences.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VisitorConfirm extends AppCompatActivity {
    LinearLayout ll_VisitorDetails, ll_Button;
    Button btn_ScanCode, btn_EnterCode, btn_confirm, btn_cancel, btn_close, btn_ok;
    TextView tv_VisitorName, tv_VisitorMobile, tv_VehicleNo, tv_InTime, tv_Error ;
    EditText et_Code;
    ImageView iv_VisitorPhoto, iv_VehiclePhoto;
    String stMassage, stCode, GetVisitorUrl = URL+"get-visitor-bycode", ConfirmVisitorUrl = URL+"visitor-confirmation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_confirm);

        ll_VisitorDetails = findViewById(R.id.ll_VisitorDetails);
        ll_Button  = findViewById(R.id.ll_Button);
        btn_ScanCode = findViewById(R.id.btn_ScanCode);
        btn_EnterCode  = findViewById(R.id.btn_EnterCode);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel  = findViewById(R.id.btn_cancel);
        tv_VisitorName = findViewById(R.id.tv_VisitorName);
        tv_VisitorMobile = findViewById(R.id.tv_VisitorMobile);
        tv_VehicleNo = findViewById(R.id.tv_VehicleNo);
        tv_InTime = findViewById(R.id.tv_InTime);
        iv_VisitorPhoto = findViewById(R.id.iv_VisitorPhoto);
        iv_VehiclePhoto = findViewById(R.id.iv_VehiclePhoto);

        btn_ScanCode.setOnClickListener(view -> scanQRCode());
        btn_EnterCode.setOnClickListener(view -> CodeConfirm());
        btn_confirm.setOnClickListener(view -> ConfirmVisitor());
        btn_cancel.setOnClickListener(view -> finish());

    }

    //Launch, Scan and send qr data to server
    public void scanQRCode() {
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
                    stCode = result.getContents();
                    showConfirmationDialog();
                }
            });
    //Open scanner confirmation dialog
    public void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Scan")
                .setMessage("Scan completed")
                .setPositiveButton("Yes", (dialog, which) -> {

                    ll_VisitorDetails.setVisibility(VISIBLE);
                    GetVisitorData();

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void CodeConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisitorConfirm.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.visitor_confirm_code, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        et_Code = view.findViewById(R.id.et_Code);
        tv_Error = view.findViewById(R.id.tv_Error);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_close = view.findViewById(R.id.btn_close);

        btn_ok.setOnClickListener(view1 -> {
            String inputCode = et_Code.getText().toString().trim();
                if(inputCode.length() == 4){
                    stCode = inputCode;
                    GetVisitorData();
                    dialog.dismiss();
                } else {
                    tv_Error.setVisibility(VISIBLE);
                }
        });
        btn_close.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }
    public void GetVisitorData(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("code", stCode)
                .build();

        Request request = new Request.Builder()
                .url(GetVisitorUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(VisitorConfirm.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string().trim();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String error = jsonResponse.optString("error", "");
                        String msg = jsonResponse.optString("msg", ""); // Get 'msg' from response
                        String id = jsonResponse.optString("id", "");
                        String VisitorName = jsonResponse.optString("visitors_name", "");
                        String VisitorMobile = jsonResponse.optString("contact_no", "");
                        String VisitorPhoto = jsonResponse.optString("photo", "");
                        String VehicleNo = jsonResponse.optString("vehicleno", "");
                        String VehiclePhoto = jsonResponse.optString("vehicle_photo", "");
                        String InTime = jsonResponse.optString("in_datetime", "");

                        runOnUiThread(() -> {

                            if (!error.isEmpty()) {
                                stMassage = error;
                                showAlertDialog();
                            }else {
                                ll_VisitorDetails.setVisibility(VISIBLE);
                                ll_Button.setVisibility(GONE);

                                if (!VisitorPhoto.isEmpty()) {
                                    Glide.with(VisitorConfirm.this).load(ImageURL + VisitorPhoto).into(iv_VisitorPhoto);
                                }
                                tv_VisitorName.setText(VisitorName);
                                tv_VisitorMobile.setText(VisitorMobile);
                                tv_InTime.setText(InTime);
                                tv_VehicleNo.setText(VehicleNo);
                                if (!VehiclePhoto.isEmpty()) {
                                    iv_VehiclePhoto.setVisibility(VISIBLE);
                                    Glide.with(VisitorConfirm.this).load(ImageURL + VehiclePhoto).into(iv_VehiclePhoto);
                                }
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(VisitorConfirm.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(VisitorConfirm.this, "Server error", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private void ConfirmVisitor(){

        String code = Base64.getEncoder().encodeToString((stCode.trim()).getBytes());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("user_id", UserId)
                .build();

        Request request = new Request.Builder()
                .url(ConfirmVisitorUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(VisitorConfirm.this, e.getMessage(), Toast.LENGTH_LONG).show());
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
                            if(!msg.isEmpty()){
                                stMassage = msg;
                                showAlertDialog();
                            } else if (!error.isEmpty()) {
                                stMassage = error;
                                showAlertDialog();
                            }else {
                                stMassage = jsonResponse.toString();
                                showAlertDialog();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(VisitorConfirm.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(VisitorConfirm.this, "Server error", Toast.LENGTH_LONG).show());
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
                finish();
            }
        });
        builder.setCancelable(false);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}