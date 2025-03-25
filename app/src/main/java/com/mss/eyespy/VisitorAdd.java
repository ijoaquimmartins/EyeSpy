package com.mss.eyespy;

import static com.mss.eyespy.GlobalClass.*;
import static com.mss.eyespy.SharedPreferences.*;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.CompoundButton.*;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VisitorAdd extends AppCompatActivity {

    EditText et_VisitorFullName, et_VisitorMobileNo, et_VisitorVehicleNo, et_Visiting_To, et_Visiting_Location, et_Purpose;
    Button btn_VisitorPhoto, btn_VisitorVehiclePhoto, btn_Add, btn_cancel;
    ImageView iv_VisitorPhoto, iv_VisitorVehiclePhoto;
    SwitchCompat sw_Vehicle;
    LinearLayout linearLayout;
    String stMassage, error, msg, VisitoraddURL = URL+"visitor/save";
    private Uri imageUri1, imageUri2;
    private String uploadImage1, uploadImage2;
    private static final int REQUEST_CAMERA_1 = 101;
    private static final int REQUEST_GALLERY_1 = 102;
    private static final int REQUEST_CAMERA_2 = 103;
    private static final int REQUEST_GALLERY_2 = 104;


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

        sw_Vehicle.setChecked(false);

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

        btn_VisitorPhoto.setOnClickListener(view -> showImagePickerDialog(1));
        btn_VisitorVehiclePhoto.setOnClickListener(view -> showImagePickerDialog(2));
        btn_Add.setOnClickListener(view -> addvisitor());
        btn_cancel.setOnClickListener(view -> this.finish());

    }

    private void showImagePickerDialog(int imageNumber) {
        String[] options = {"Open Camera", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera(imageNumber);
                    else openGallery(imageNumber);
                })
                .show();
    }

    private void openCamera(int imageNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, imageNumber == 1 ? REQUEST_CAMERA_1 : REQUEST_CAMERA_2);
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
            Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            if (imageNumber == 1) {
                imageUri1 = imageUri;
                startActivityForResult(intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1), REQUEST_CAMERA_1);
            } else {
                imageUri2 = imageUri;
                startActivityForResult(intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2), REQUEST_CAMERA_2);
            }
        }
    }

    private void openGallery(int imageNumber) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, imageNumber == 1 ? REQUEST_GALLERY_1 : REQUEST_GALLERY_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_1 || requestCode == REQUEST_GALLERY_1) {
                if (requestCode == REQUEST_GALLERY_1) imageUri1 = data.getData();
                iv_VisitorPhoto.setVisibility(View.VISIBLE);
                iv_VisitorPhoto.setImageURI(imageUri1);
                uploadImage1 = (imageUri1 != null) ? getRealPathFromURI(imageUri1) : null;
            } else if (requestCode == REQUEST_CAMERA_2 || requestCode == REQUEST_GALLERY_2) {
                if (requestCode == REQUEST_GALLERY_2) imageUri2 = data.getData();
                iv_VisitorVehiclePhoto.setVisibility(View.VISIBLE);
                iv_VisitorVehiclePhoto.setImageURI(imageUri2);
                uploadImage2 = (imageUri2 != null) ? getRealPathFromURI(imageUri2) : null;
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    private String getRealPathFromURI(Uri uri) {
        if (uri == null) return null; // Skip if no image is selected

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File compressedFile = compressImage(uri);
            return compressedFile != null ? compressedFile.getAbsolutePath() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File compressImage(Uri imageUri) {
        if (imageUri == null) return null;

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            int actualWidth = options.outWidth;
            int actualHeight = options.outHeight;

            int sampleSize = 1;
            while ((actualWidth / sampleSize) * (actualHeight / sampleSize) > 1000000) {
                sampleSize *= 2;
            }

            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            File compressedFile = new File(getCacheDir(), "compressed_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(compressedFile);

            int quality = 90;
            do {
                outputStream.flush();
                outputStream.close();
                outputStream = new FileOutputStream(compressedFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                quality -= 10;
            } while (compressedFile.length() > 1024 * 1024 && quality > 30);

            outputStream.flush();
            outputStream.close();
            return compressedFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private void addvisitor(){

        String editedDatetime;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        editedDatetime = sdf.format(new Date());

        if (areFieldsEmpty(et_VisitorFullName, et_VisitorMobileNo, et_Visiting_To, et_Visiting_Location, et_Purpose)) {
            stMassage = "All (*) marked fields are mandatory";
            showAlertDialog();
            return;
        }

        File imageFile1 = (uploadImage1 != null) ? new File(uploadImage1) : null;
        File imageFile2 = (uploadImage2 != null) ? new File(uploadImage2) : null;


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("formid", "")
                .addFormDataPart("formtype", "ADD")
                .addFormDataPart("visitors_name", et_VisitorFullName.getText().toString().trim())
                .addFormDataPart("contact_no", et_VisitorMobileNo.getText().toString().trim())
                .addFormDataPart("vehicleno", et_VisitorVehicleNo.getText().toString().trim())
                .addFormDataPart("visiting_to", et_Visiting_To.getText().toString().trim())
                .addFormDataPart("flat_no", et_Visiting_Location.getText().toString().trim())
                .addFormDataPart("purpose", et_Purpose.getText().toString().trim())
                .addFormDataPart("in_datetime", editedDatetime)
                .addFormDataPart("created_by", UserId)
                .addFormDataPart("updated_by", UserId);

        if (imageFile1 != null) {
            formBodyBuilder.addFormDataPart("image1", imageFile1.getName(),
                    RequestBody.create(imageFile1, MediaType.parse("image/jpeg")));
        }
        if (imageFile2 != null) {
            formBodyBuilder.addFormDataPart("image2", imageFile2.getName(),
                    RequestBody.create(imageFile2, MediaType.parse("image/jpeg")));
        }

        RequestBody requestBody = formBodyBuilder.build();

        Request request = new Request.Builder()
                .url(VisitoraddURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(VisitorAdd.this, e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string().trim();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        error = jsonResponse.optString("error", "");
                        msg = jsonResponse.optString("msg", "");
                        String exitcode = jsonResponse.optString("code", "");

                        runOnUiThread(() -> {
                            if (!msg.equalsIgnoreCase("")) {
                                stMassage = exitcode + " Visit confirmation code";
                                showAlertDialog();
                            }else if (!error.equalsIgnoreCase("")) {
                                stMassage = error;
                                showAlertDialog();
                            } else {
                                stMassage = msg.isEmpty() ? error : msg;
                                showAlertDialog();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(VisitorAdd.this, "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(VisitorAdd.this, "Server error", Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!msg.equals("")){
                    dialog.dismiss();
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

    public boolean areFieldsEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().trim().isEmpty()) {
                editText.setError("This field is required");
                return true;
            }
        }
        return false;
    }
}