package com.Activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.DbUtil.Database;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PERMISSION_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionStatusInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionStatusAccessNetworkState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        if (permissionStatusInternet == PackageManager.PERMISSION_GRANTED && permissionStatusAccessNetworkState == PackageManager.PERMISSION_GRANTED) {
            connect();
            openFragment();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_CODE_PERMISSION_INTERNET);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Database.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_INTERNET:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connect();
                    openFragment();
                }
                return;
        }
    }

    private void connect() {
        Database database = new Database();
    }

    private void openFragment() {
        AuthorizationFragment authorizationFragment = new AuthorizationFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frgmCont, authorizationFragment).commit();
    }
}