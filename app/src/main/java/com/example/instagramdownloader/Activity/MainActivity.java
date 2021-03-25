package com.example.instagramdownloader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    LinearLayout llhome, llDownload;
    ImageView ivhome, ivDownload;
    TextView tvDownload, tvhome, tvToolbarname;
    Button btn_instagram;
    String PERMISSIONS[] = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private int STORAGE_PERMISSION_CODE = 23;
    //  String url="https://www.instagram.com/p/B7WG7A8lMhI/?igshid=o91ye2q6pe0j";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //checkPermission();

        init();
        //get data from share icon
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ("android.intent.action.SEND".equals(action) && type != null && "text/plain".equals(type)) {
            Log.println(Log.ASSERT,"shareablTextExtra",intent.getStringExtra("android.intent.extra.TEXT"));
        }

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
                    Toast.makeText(MainActivity.this, "Instagram is not installed, Please install first!", Toast.LENGTH_LONG).show();
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

        int ResultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ResultStorage1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ResultStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else if (ResultStorage1 != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            init();
        }



    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, STORAGE_PERMISSION_CODE);
            //Toast.makeText(this, "Tihis is very importent permission", Toast.LENGTH_LONG).show();

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

                ///Toast.makeText(this, "Yo denied the permission", Toast.LENGTH_LONG).show();
        }
    }
}
