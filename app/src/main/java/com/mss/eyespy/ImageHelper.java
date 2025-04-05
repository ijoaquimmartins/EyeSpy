package com.mss.eyespy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import java.io.FileOutputStream;

public class ImageHelper {

    private static final String IMAGE_NAME = "profilepic.jpg";
    private static final String TAG = "ImageHelper";

    // Downloads the image and saves it locally if not already saved
    public static void downloadAndSaveImage(Context context, String imageUrl) {
        File file = getSavedImageFile(context);

        if (file.exists()) {
            Log.d(TAG, "Image already exists: " + file.getAbsolutePath());
            return; // Image already downloaded
        }

        // Download and save the image using Glide
        Glide.with(context)
                .asBitmap()
                .circleCrop()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            Log.d(TAG, "Image saved at: " + file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error saving image: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                        // Not used
                    }
                });
    }

    // Applies the saved image to the given ImageView
    public static void applySavedImage(Context context, ImageView imageView) {
        File file = getSavedImageFile(context);
        if (file.exists()) {
            Glide.with(context).load(file).circleCrop().into(imageView);
            Log.d(TAG, "Applied saved image from: " + file.getAbsolutePath());
        } else {
            Log.d(TAG, "No saved image found to apply.");
        }
    }

    // Returns the File where the image will be saved
    private static File getSavedImageFile(Context context) {
        File directory = new File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), "MyApp");
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            Log.d(TAG, "Created directory: " + directory.getAbsolutePath() + " success: " + created);
        }
        return new File(directory, IMAGE_NAME);
    }
}
