package com.mss.eyespy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import java.io.FileOutputStream;

public class ImageHelper {

    private static final String IMAGE_NAME = "profilepic.jpg";

    public static void downloadAndSaveImage(Context context, String imageUrl) {
        File file = getSavedImageFile();

        if (file.exists()) {
            return; // Image already downloaded, do nothing
        }

        // Download and save the image
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void applySavedImage(Context context, ImageView imageView) {
        File file = getSavedImageFile();
        if (file.exists()) {
            Glide.with(context).load(file).into(imageView);
        }
    }

    private static File getSavedImageFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        if (!directory.exists()) directory.mkdirs();
        return new File(directory, IMAGE_NAME);
    }
}
