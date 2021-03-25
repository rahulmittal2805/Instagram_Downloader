package com.example.instagramdownloader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagramdownloader.R;
import com.example.instagramdownloader.fragments.DownloadFragment;
import com.example.instagramdownloader.fragments.HomeFragments;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    LinearLayout llhome, llDownload;
    ImageView ivhome, ivDownload;
    TextView tvDownload, tvhome, tvToolbarname;
    Button btn_instagram;
    String PERMISSIONS[] = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    // String Permission=android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int STORAGE_PERMISSION_CODE = 23;
    //  String url="https://www.instagram.com/p/B7WG7A8lMhI/?igshid=o91ye2q6pe0j";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermission();



    }

    public void init() {
        llhome = findViewById(R.id.llhome);
        llDownload = findViewById(R.id.llDownload);
        ivhome = findViewById(R.id.ivhome);
        ivDownload = findViewById(R.id.ivDownload);
        tvDownload = findViewById(R.id.tvDownload);
        tvhome = findViewById(R.id.tvhome);
        btn_instagram = findViewById(R.id.btn_instagram);
        tvToolbarname = findViewById(R.id.tvToolbarname);

        llhome.setOnClickListener(this);
        llDownload.setOnClickListener(this);
        loadFragment(new HomeFragments());

        btn_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (launchIntent != null) {
                    try {
                        startActivity(launchIntent);
                    } catch (ActivityNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Toast.makeText(Main2Activity.this, "Instagram is not installed, Please install first!", Toast.LENGTH_LONG).show();
                }

            }
        });


        tvToolbarname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fl_Container, fragment, "");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llhome:
                loadFragment(new HomeFragments());
                break;
            case R.id.llDownload:
                loadFragment(new DownloadFragment());
                break;

        }
    }


    public void checkPermission() {

        /*int ResultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ResultStorage1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ResultStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else if (ResultStorage1 != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            init();
        }*/

        Toast.makeText(getApplicationContext(),"eefffffffffffffffffffff",Toast.LENGTH_LONG);

        Dexter.withActivity(Main2Activity.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                }


                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // show alert dialog navigating to Settings
                    //  showSettingsDialog();
                    Toast.makeText(getApplicationContext(), " permissions are denied!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        }).check();

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else
                checkPermission();
        }
    }
}
