package com.tonyocallimoutou.realestatemanager.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.User;

public class UtilContactUser {

    private static Activity mActivity;
    private static ActivityResultLauncher<String> mPermission;

    private static String phoneNumber;

    public static void getContactOfUser(Activity activity, User user, ActivityResultLauncher<String> permission) {

        mPermission = permission;

        mActivity = activity;

        View view = activity.getLayoutInflater().inflate(R.layout.alert_dialog_contact_user, null);

        LinearLayout phoneChoice = view.findViewById(R.id.alert_dialog_phone_choice);
        LinearLayout emailChoice = view.findViewById(R.id.alert_dialog_email_choice);
        TextView phone = view.findViewById(R.id.alert_dialog_phone_number);
        TextView email = view.findViewById(R.id.alert_dialog_email);

        if (user.getPhoneNumber() != null && ! user.getPhoneNumber().isEmpty()) {
            phoneNumber = user.getPhoneNumber();
            phone.setText(user.getPhoneNumber());
        }
        else {
            phoneChoice.setVisibility(View.GONE);
        }

        email.setText(user.getEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(true);

        AlertDialog alert = builder.create();
        alert.show();

        phoneChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOrPermission();
            }
        });

        emailChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + user.getEmail()));

                mActivity.startActivity(intent);
            }
        });
    }

    // CALL

    private static void callOrPermission() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
        } else {
            callUser();
        }
    }

    private static void askForPermission() {
        mPermission.launch(Manifest.permission.CALL_PHONE);
    }

    public static void onRequestPermissionsResult(boolean result) {
        if (result){
            callUser();
        } else {
            explain();
        }
    }

    private static void explain() {
        Toast.makeText(mActivity, mActivity.getString(R.string.explain_call), Toast.LENGTH_SHORT).show();
    }

    private static void callUser() {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        mActivity.startActivity(intent);
    }
}
