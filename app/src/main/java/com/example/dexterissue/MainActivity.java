package com.example.dexterissue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SETTINGS = 333;
    private static final String TAG = "MainActivity";

    StringBuilder missingPermissions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        checkPermissions();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }


    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



    public void checkPermissions() {

        List<String> permissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionList.add(Manifest.permission.SEND_SMS);
        permissionList.add(Manifest.permission.READ_PHONE_STATE);


        Dexter.withContext(this.getApplicationContext())
                .withPermissions(
                        permissionList
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                deniedPermissionsAsString(report.getDeniedPermissionResponses());

                if (report.areAllPermissionsGranted()) {

                    Toast.makeText(getApplicationContext(), "All permissions granted", Toast.LENGTH_SHORT).show();

                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    // check for permanent denial of any permission then show dialog that navigates to device Settings
                    if(missingPermissions != null && missingPermissions.length() > 0)
                        showSettingsDialog();

                }

            }

            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Error occurred! ",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: " + error.toString() );
            }
        })
                .onSameThread()
                .check();
    }

    /**
     * Get a list if denied permissions
     * @param deniedResponses which permissions the user chose to deny
     */
    private void deniedPermissionsAsString(List<PermissionDeniedResponse> deniedResponses){
        if(missingPermissions != null)
            missingPermissions = null;

        missingPermissions = new StringBuilder();
        for (PermissionDeniedResponse response : deniedResponses) {
            if (!missingPermissions.toString().contains(response.getPermissionName()))
                missingPermissions.append("\n").append(response.getPermissionName());
        }
    }

    /**
     * Showing Alert Dialog with a button to open device Settings
     */
    private void showSettingsDialog() {
        Log.d(TAG, "showSettingsDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("The following permissions are required for this app to function properly. You can change "+
                "this in app settings.\n" + missingPermissions);

        //When the user clicks 'GOTO SETTINGS'
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        //When the user clicks 'Cancel'
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                onPermissionAreSet();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    /**
     * Navigating the user to app settings
     */
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Returning from Device settings
        if (requestCode == REQUEST_SETTINGS) {

            Log.d(TAG, "onActivityResult: ");
//            onPermissionAreSet();
        }
    }


}