package com.tonyocallimoutou.realestatemanager.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;

import com.tonyocallimoutou.realestatemanager.BuildConfig;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UtilsRealEstatePictureManager {

    public static final int REQUEST_IMAGE_CAMERA = 100;
    public static final int REQUEST_IMAGE_FOLDER = 200;

    private static List<String> newPicture = new ArrayList<>();

    private static String imagePath;

    private static AlertDialog alert;

    private static Activity mActivity;

    public static List<Photo> getImagePicture(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {
            newPicture.add("file://" + imagePath);
            alert.cancel();
            return getPhotoFromList();
        }
        if (requestCode == REQUEST_IMAGE_FOLDER && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                for (int i=0; i< data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    mActivity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    newPicture.add(uri.toString());
                }
            }
            else {
                Uri uri = data.getData();
                mActivity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                newPicture.add(uri.toString());
            }

            alert.cancel();
            return getPhotoFromList();
        }

        return new ArrayList<>();

    }

    private static List<Photo> getPhotoFromList() {
        List<Photo> photos = new ArrayList<>();
        for (String ref : newPicture) {
            Photo photo = new Photo(ref, null);
            photos.add(photo);
        }

        return photos;
    }

    public static void createAlertDialog(Activity activity) {

        mActivity = activity;

        newPicture = new ArrayList<>();

        View view = activity.getLayoutInflater().inflate(R.layout.alert_real_estate_picture, null);

        LinearLayout filesChoice = view.findViewById(R.id.alert_dialog_files_choice);
        LinearLayout cameraChoice = view.findViewById(R.id.alert_dialog_camera_choice);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(true);

        alert = builder.create();
        alert.show();

        cameraChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureWithCamera();
            }
        });

        filesChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureIntoFolder();
            }
        });
    }

    private static void takePictureWithCamera() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",  /* suffix */
                    storageDir);

            Uri uri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider",image);

            imagePath= image.getAbsolutePath();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            mActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void takePictureIntoFolder() {
        Intent getIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        mActivity.startActivityForResult(chooserIntent, REQUEST_IMAGE_FOLDER);
    }
}
