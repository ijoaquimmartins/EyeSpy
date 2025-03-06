package com.mss.eyespy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mss.eyespy.SharedPreferences.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    String LoginUrl = URL+"";
    EditText etMobileNo, etPassword;
    TextView tvVersion;
    Button btnLogin, btnCancel;
    CheckBox cbRememberMe;

    public String stMassage,stMobileno, stPassword;
    public static final String SHARED_PREFS = "sharedprefs";
    public static final String MOBILE_NO = "mobileno";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMobileNo = findViewById(R.id.et_MobileNo);
        etPassword = findViewById(R.id.et_Password);
        tvVersion = findViewById(R.id.tv_App_version);
        btnLogin = findViewById(R.id.btn_Login);
        btnCancel = findViewById(R.id.btn_Cancel);
        cbRememberMe = findViewById(R.id.checkboxRemember);


    }

    public void login(){
        // Base64.getEncoder().encodeToString(userDetails.UserId.getBytes());

        stMobileno = Base64.getEncoder().encodeToString((etMobileNo.getText().toString().trim()).getBytes());

        stPassword = etPassword.getText().toString().trim();
        MobileNo = stMobileno.toString();
        if (!stMobileno.isEmpty() && !stPassword.isEmpty()) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            FormBody formBody = new FormBody.Builder()
                    .add("username", stMobileno)
                    .add("password", stPassword)
                    .build();

            Request request = new Request.Builder()
                    .url(LoginUrl)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string().trim();
                        runOnUiThread(() -> {
                            if (responseBody.equals("success")) {
                                Intent intent = new Intent(Login.this, LoadingActivity.class);
                                intent.putExtra(MOBILE_NO, stMobileno);
                                startActivity(intent);
                                finish();
                            } else {
                                stMassage = responseBody;
                                showAlertDialog();
                            }
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(Login.this, "Server error", Toast.LENGTH_LONG).show());
                    }
                }
            });
        } else {
            Toast.makeText(Login.this, "Fields can not be empty", Toast.LENGTH_SHORT).show();
        }
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("success".equals(stMassage)){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra(MOBILE_NO, stMobileno);
                    startActivity(intent);
                    finish();
                }else{
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}