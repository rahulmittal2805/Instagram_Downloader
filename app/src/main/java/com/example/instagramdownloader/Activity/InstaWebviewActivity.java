package com.example.instagramdownloader.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramdownloader.R;
import com.example.instagramdownloader.SharedPref;

import java.util.List;

import okhttp3.Cookie;

public class InstaWebviewActivity extends AppCompatActivity {
    WebView mywebview;
    CookieManager cookieManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_webview_activitiy);
        sharedPreferences=getSharedPreferences(getPackageName(), Activity.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);


        mywebview = (WebView) findViewById(R.id.webView);
        mywebview.setWebViewClient(new load());
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setDomStorageEnabled(true);
        mywebview.loadUrl("https://www.instagram.com");

    }

    private class load extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {

            view.loadUrl(request);
            if (getCookie("sessionid")) {
                Intent successData = new Intent();
                successData.putExtra("success", true);
               // pref.putBoolean("private_session", true)
                editor.putBoolean("private_session", true);
                editor.commit();
                setResult(1, successData);
                finish();
            }

            return false;
        }
    }


    public boolean getCookie(String cookieName ){
        String cookies = cookieManager.getCookie("https://www.instagram.com");
        String[] cookieList = cookies.split(";");
        for (String cookie : cookieList) {
            if (cookie.contains(cookieName)) {
                String[] tempCookie  = cookie.split("=");
                Log.e("cookie", tempCookie[1]);
              //  pref.putString("sessionid", tempCookie[1])
                editor.putString("sessionid", tempCookie[1]);
                editor.commit();
                return true;
            }
        }

        return false;
    }
}
