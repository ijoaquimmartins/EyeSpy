package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static android.view.View.VISIBLE;
import static com.mss.eyespy.GlobalClass.exitApp;
import static com.mss.eyespy.GlobalClass.logout;
import static com.mss.eyespy.GlobalClass.setAppVersion;
import static com.mss.eyespy.SharedPreferences.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView menu, photo;
    LinearLayout ll_Home, ll_Register, ll_ShiftTimings, ll_AssignShift, ll_Attendance, ll_Logout, ll_Exit ;
    TextView tv_App_Ver_Up, tv_UserName;
    Spinner spUserType;
    EditText selectedEditText;
    EditText et_DoJ, et_FirstName, et_MiddleName, et_LastName, et_DateOfBirth, et_MobileNo, et_Email,
            et_AadharCard, et_PAN, et_PassportNo, et_PermanentAddress, et_CurrentAddress, et_Password, et_Confirm_Password;
    CheckBox cbSameAsAbove;
    Button btn_Clickphoto, btn_Submit, btn_Cancel;
    ImageView iv_ProfilePhoto;
    String stMassage, stPassword, uploadImage, stConPassword, RegisterUrl= URL+"user/save";
    private Uri imageUri;
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

/* Drawer Code*/
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

        ll_Home.setOnClickListener(view -> redirectActivity(RegisterActivity.this, MainActivity.class));
        ll_Register.setOnClickListener( view -> recreate());


/*Drawer Code*/

        spUserType = findViewById(R.id.spUserType);
        et_DoJ = findViewById(R.id.et_DoJ);
        et_FirstName = findViewById(R.id.et_FirstName);
        et_MiddleName = findViewById(R.id.et_MiddleName);
        et_LastName = findViewById(R.id.et_LastName);
        et_DateOfBirth = findViewById(R.id.et_DateOfBirth);
        et_MobileNo = findViewById(R.id.et_MobileNo);
        et_Email = findViewById(R.id.et_Email);
        et_AadharCard = findViewById(R.id.et_AadharCard);
        et_PAN = findViewById(R.id.et_PAN);
        et_PassportNo = findViewById(R.id.et_PassportNo);
        et_PermanentAddress = findViewById(R.id.et_PermanentAddress);
        cbSameAsAbove = findViewById(R.id.cbSameAsAbove);
        et_CurrentAddress = findViewById(R.id.et_CurrentAddress);
        et_Password = findViewById(R.id.et_Password);
        et_Confirm_Password  = findViewById(R.id.et_Confirm_Password);
        iv_ProfilePhoto = findViewById(R.id.iv_ProfilePhoto);
        btn_Clickphoto = findViewById(R.id.btn_Clickphoto);
        btn_Submit = findViewById(R.id.btn_Submit);
        btn_Cancel = findViewById(R.id.btn_Cancel);

        et_DoJ.setOnClickListener(view -> showDatePicker(et_DoJ));
        et_DateOfBirth.setOnClickListener(view -> showDatePicker(et_DateOfBirth));
        cbSameAsAbove.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                et_CurrentAddress.setText(et_PermanentAddress.getText().toString());
            } else {
                et_CurrentAddress.setText("");
            }
        });
        btn_Clickphoto.setOnClickListener(view -> showImagePickerDialog());
        btn_Cancel.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
        btn_Submit.setOnClickListener(view -> SubmitForm());


    }
    /* Drawer Code*/
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
    /* Drawer Code*/

    private void showDatePicker(EditText editText) {
        selectedEditText = editText;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedEditText.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
    private void showImagePickerDialog() {
        String[] options = {"Open Camera", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                iv_ProfilePhoto.setVisibility(VISIBLE);
                iv_ProfilePhoto.setImageURI(imageUri);
                uploadImage = getRealPathFromURI(imageUri);
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                iv_ProfilePhoto.setVisibility(VISIBLE);
                iv_ProfilePhoto.setImageURI(imageUri);
                uploadImage = getRealPathFromURI(imageUri);
            }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        if (uri.getScheme().equals("content")) {
            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                File tempFile = createTempFileFromStream(inputStream);
                filePath = tempFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (uri.getScheme().equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }
    private File createTempFileFromStream(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return tempFile;
    }
    private void SubmitForm(){

        stPassword = Base64.getEncoder().encodeToString(et_Password.getText().toString().trim().getBytes());
        stConPassword = Base64.getEncoder().encodeToString(et_Confirm_Password.getText().toString().trim().getBytes());

        if (areFieldsEmpty(et_DoJ, et_FirstName, et_LastName, et_DateOfBirth, et_MobileNo, et_Email, et_PermanentAddress, et_CurrentAddress, et_Password, et_Confirm_Password )) {
            stMassage = "All (*) marked fields are mandatory";
            showAlertDialog();
            return;
        }
        if(!et_Password.getText().equals(et_Confirm_Password.getText())){
            stMassage = "Password Do not match";
            showAlertDialog();
            return;
        }
        if (uploadImage == null || uploadImage.isEmpty()) {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        File imageFile = new File(uploadImage);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);

        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("usertype", String.valueOf(spUserType))
                .addFormDataPart("dt_joning", et_DoJ.getText().toString().trim())
                .addFormDataPart("first_name", et_FirstName.getText().toString().trim())
                .addFormDataPart("middle_name", et_MiddleName.getText().toString().trim())
                .addFormDataPart("last_name", et_LastName.getText().toString().trim())
                .addFormDataPart("dateofbirth", et_DateOfBirth.getText().toString().trim())
                .addFormDataPart("mobileno", et_MobileNo.getText().toString().trim())
                .addFormDataPart("email", et_Email.getText().toString().trim())
                .addFormDataPart("aadhar_no", et_AadharCard.getText().toString().trim())
                .addFormDataPart("pancard_no", et_PAN.getText().toString().trim())
                .addFormDataPart("passport_no", et_PassportNo.getText().toString().trim())
                .addFormDataPart("permanent_address", et_PermanentAddress.getText().toString().trim())
                .addFormDataPart("present_address", et_CurrentAddress.getText().toString().trim())
                .addFormDataPart("password", stPassword)
                .addFormDataPart("confirm_password", stConPassword)
                .addFormDataPart("photo", imageFile.getName(), imageRequestBody)
                .build();

        Request request = new Request.Builder()
                .url(RegisterUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show());
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
                        if(msg.equalsIgnoreCase("success")){
                            stMassage = msg;
                            showAlertDialog();
                        } else if (error.equalsIgnoreCase("failed")) {
                            stMassage = error;
                            showAlertDialog();
                        }else{
                            stMassage = msg;
                            showAlertDialog();
                        }
                    });
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }else {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "Server error", Toast.LENGTH_LONG).show());
                }
            }
        });
    }
    private boolean areFieldsEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("This field is required");
                return true;
            }
        }
        return false;
    }
    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}