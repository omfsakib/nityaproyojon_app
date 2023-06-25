package com.myapp.nityaproyojon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadSupport {
    public ValueCallback<Uri> mUM;
    public ValueCallback<Uri[]> mUMA;
    public String mCM;
    public static int FCR = 863;
    public static int uploadCode = 9438162;
    private Activity activity;

    public UploadSupport(Activity activity){
        this.activity = activity;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
    }
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                FCR);
    }
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public void showFileChooser(ValueCallback<Uri[]> filePathCallback){
        if (Build.VERSION.SDK_INT>=23){
            if (ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ==PackageManager.PERMISSION_GRANTED){
                manageFileChooser(filePathCallback);
            }else {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},uploadCode);
            }
        }else {
            manageFileChooser(filePathCallback);
        }

    }

    private void manageFileChooser(ValueCallback<Uri[]> filePathCallback){
        if (mUMA != null) {
            mUMA.onReceiveValue(null);
        }

        mUMA = filePathCallback;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCM);
            } catch (IOException exc) {
                Log.e("TAG", "Image file creation failed", exc);
            }
            if (photoFile != null) {
                mCM = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an Action");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        activity.startActivityForResult(chooserIntent,FCR);
    }

}
