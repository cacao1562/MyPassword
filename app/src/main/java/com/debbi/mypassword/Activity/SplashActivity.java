package com.debbi.mypassword.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.debbi.mypassword.R;
import com.debbi.mypassword.Utils.RootUtil;
import com.debbi.mypassword.biometric.BiometricCallback;
import com.debbi.mypassword.biometric.BiometricManager;
import com.debbi.mypassword.biometric.BiometricUtils;

public class SplashActivity extends AppCompatActivity implements BiometricCallback {

    private BiometricManager mBiometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (RootUtil.isDeviceRooted()) {

            showAlert("alert", "루팅된 기기입니다.");
            return;
        }

        authBio();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void authBio() {

        if(!BiometricUtils.isFingerprintAvailable(this)) {

//            Toast.makeText(this,"지문 정보가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT );
            showAlert("alert", "지문 정보가 등록되어 있지 않습니다.");

        }else {

            runFingerPrint();
        }

    }

    private void runFingerPrint() {

        mBiometricManager = new BiometricManager.BiometricBuilder(this)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                .build();
        mBiometricManager.authenticate(this);
    }


    private void showAlert(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("ok", (d,w) -> {
            d.dismiss();
            finish();
        });
//        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                finish();
////                DialogDismisser.dismiss(dialog);
////                ((Activity)context).finish();
////                ((Activity)context).finishAffinity();
////                ((Activity)context).finishAndRemoveTask();
////                System.runFinalization();
////                System.exit(0);
//            }
//        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSdkVersionNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {

    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {

    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {

    }

    @Override
    public void onAuthenticationFailed() {

    }

    @Override
    public void onAuthenticationCancelled() {

    }

    @Override
    public void onAuthenticationSuccessfull() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
