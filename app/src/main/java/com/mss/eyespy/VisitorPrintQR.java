package com.mss.eyespy;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.io.IOException;

public class VisitorPrintQR extends AppCompatActivity {

    ImageView iv_qrprint;
    String code;
    Button btnPrint, btnCancel;
    Bitmap qrCodeBitmap;
    TextView tv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_print_qr);

        iv_qrprint = findViewById(R.id.iv_qrprint);
        btnPrint = findViewById(R.id.btn_Print);
        btnCancel = findViewById(R.id.btn_cancel);
        tv_code = findViewById(R.id.tv_code);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");

        if (code != null) {
            try {
                qrCodeBitmap = generateQRCode(code);
                iv_qrprint.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            tv_code.setText(code);
            Toast.makeText(this, "Received Code: " + code, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Code Not Received Code: ", Toast.LENGTH_LONG).show();
        }

        btnPrint.setOnClickListener(view -> printQRCode(qrCodeBitmap));
        btnCancel.setOnClickListener(view -> finish());
    }
    private Bitmap generateQRCode(String data) throws WriterException {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("QR code data cannot be empty or null");
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 500, 500);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        return bitmap;
    }

    public void printQRCode(Bitmap qrCodeBitmap) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        if (printManager != null) {
            PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle bundle) {
                    callback.onLayoutFinished(new PrintDocumentInfo.Builder("qr_code_print").build(), true);
                }

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    try (FileOutputStream out = new FileOutputStream(destination.getFileDescriptor())) {
                        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            printManager.print("QRCode", printAdapter, new PrintAttributes.Builder().build());
        }
    }
}