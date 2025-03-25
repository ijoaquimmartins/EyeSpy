package com.mss.eyespy;

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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VisitorConfirm extends AppCompatActivity {
    LinearLayout ll_VisitorDetails;
    Button btn_ScanCode, btn_EnterCode, btn_confirm, btn_cancel;
    TextView tv_VisitorName, tv_VisitorMobile, tv_VehicleNo, tv_InTime, tv_Error ;
    EditText et_Code;
    ImageView iv_VisitorPhoto, iv_VehiclePhoto;
    String stCode, GetVisitorUrl = URL+"" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_confirm);

        ll_VisitorDetails = findViewById(R.id.ll_VisitorDetails);
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
        LayoutInflater inflater =getLayoutInflater();
        View view = inflater.inflate(R.layout.visitor_confirm_code, null);
        builder.setView(view);

        et_Code = view.findViewById(R.id.et_Code);
        tv_Error = view.findViewById(R.id.tv_Error);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(!et_Code.equals("") || et_Code.length() == 4){
                    tv_Error.setVisibility(VISIBLE);
                } else {
                    GetVisitorData();
                    ll_VisitorDetails.setVisibility(VISIBLE);
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void GetVisitorData(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("confirmcode", stCode)
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
                        String VisitorPhoto = jsonResponse.optString("mobileno", "");
                        String VisitorName = jsonResponse.optString("userid", "");
                        String VisitorMobile = jsonResponse.optString("first_name", "");
                        String VehicleNo = jsonResponse.optString("middle_name", "");
                        String InTime = jsonResponse.optString("last_name", "");
                        String VehiclePhoto = jsonResponse.optString("user_access", "");

                        runOnUiThread(() -> {
                            if (!msg.equals("")) {
                                Glide.with(VisitorConfirm.this).load(ImageURL+VisitorPhoto).into(iv_VisitorPhoto);
                                tv_VisitorName.setText(VisitorName);
                                tv_VisitorMobile.setText(VisitorMobile);
                                tv_InTime.setText(InTime);
                                tv_VehicleNo.setText(VehicleNo);
                                if(!VehiclePhoto.equals("")){
                                    Glide.with(VisitorConfirm.this).load(ImageURL+VehiclePhoto).into(iv_VehiclePhoto);
                                }

                            } else if (msg.equalsIgnoreCase("failed")) {

                            }else if (msg.equalsIgnoreCase("update")) {

                            }
                            else {

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

    }
}