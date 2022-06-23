package com.tonyocallimoutou.realestatemanager.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyocallimoutou.realestatemanager.BuildConfig;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsProfilePictureManager {

    private static final int REQUEST_IMAGE_CAMERA = 123;
    private static final int REQUEST_IMAGE_FOLDER = 456;

    private static ImageView actualPicture;

    private static String newProfilePicture;

    private static String imagePath;

    private static Activity mActivity;


    public static void getImagePicture(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {
            newProfilePicture = "file://" + imagePath;
            updatePictureInImageView();
        }
        if (requestCode == REQUEST_IMAGE_FOLDER && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            mActivity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            newProfilePicture = uri.toString();
            updatePictureInImageView();
        }
    }

    private static void updatePictureInImageView() {
        Glide.with(mActivity)
                .load(newProfilePicture)
                .apply(RequestOptions.circleCropTransform())
                .into(actualPicture);
    }

    public static void createAlertDialog(Activity activity, ViewModelUser viewModelUser, User currentUser) {

        mActivity = activity;

        View view = activity.getLayoutInflater().inflate(R.layout.alert_dialog_profile_picture, null);

        actualPicture = view.findViewById(R.id.alert_dialog_actual_picture);

        LinearLayout filesChoice = view.findViewById(R.id.alert_dialog_files_choice);
        LinearLayout cameraChoice = view.findViewById(R.id.alert_dialog_camera_choice);
        FloatingActionButton removePicture = view.findViewById(R.id.alert_dialog_remove_picture);
        Button saveButton = view.findViewById(R.id.alert_dialog_save_button);

        newProfilePicture = currentUser.getUrlPicture();
        updatePictureInImageView();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
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

        removePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newProfilePicture = Utils.convertDrawableResourcesToUri(activity.getApplicationContext(), R.drawable.ic_no_image_available).toString();

                updatePictureInImageView();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModelUser.setCurrentUserPicture(newProfilePicture);
                alert.cancel();
            }
        });
    }

    private static void takePictureWithCamera() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
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
        Intent getIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        mActivity.startActivityForResult(chooserIntent, REQUEST_IMAGE_FOLDER);
    }
}
