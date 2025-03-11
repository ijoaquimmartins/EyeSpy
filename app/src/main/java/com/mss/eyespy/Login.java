package com.mss.eyespy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
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

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    String LoginUrl = URL+"login";
    EditText etMobileNo, etPassword;
    TextView tvVersion;
    Button btnLogin, btnCancel;
    CheckBox cbRememberMe;
    public String mobileno, password, stMassage,stMobileno, stPassword;
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

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            tvVersion.setText("Version  " + versionName);
         //   Toast.makeText(this, "Version: " + versionName, Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        autologin();

        if(!etMobileNo.equals("") && !etPassword.equals("")){
            login();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbRememberMe.isChecked()){
                    savedata();
                    login();
                }else {
                    login();
                }
            }
        });

    }

    public void login(){
        // Base64.getEncoder().encodeToString(userDetails.UserId.getBytes());

        stMobileno = Base64.getEncoder().encodeToString((etMobileNo.getText().toString().trim()).getBytes());
        stPassword = Base64.getEncoder().encodeToString((etPassword.getText().toString().trim()).getBytes());

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
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String error = jsonResponse.optString("error", "");
                            String msg = jsonResponse.optString("msg", ""); // Get 'msg' from response
                            String firstName = jsonResponse.optString("first_name", "Unknown");
                            String userType = jsonResponse.optString("usertype", "USER");
                            String userAccess = jsonResponse.optString("user_access", "");

                            runOnUiThread(() -> {
                                if (msg.equalsIgnoreCase("success")) {
                                    Intent intent = new Intent(Login.this, Loading.class);
                                    intent.putExtra("MOBILE_NO", stMobileno);
                                    intent.putExtra("FIRST_NAME", firstName);
                                    intent.putExtra("USER_TYPE", userType);
                                    intent.putExtra("USER_ACCESS", userAccess);
                                    startActivity(intent);
                                    finish();
                                } else if (msg.equalsIgnoreCase("failed")) {
                                    stMassage = error;
                                    showAlertDialog();
                                }else {
                                    stMassage = msg;
                                    showAlertDialog();
                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(Login.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
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

    public void savedata(){
        android.content.SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE_NO, etMobileNo.getText().toString().trim());
        editor.putString(PASSWORD, etPassword.getText().toString().trim());
        editor.apply();
    }
    public void autologin(){
        android.content.SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mobileno = sharedPreferences.getString(MOBILE_NO, "");
        password = sharedPreferences.getString(PASSWORD, "");
        etMobileNo.setText(mobileno);
        etPassword.setText(password);
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}