package com.sadee.adminapp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(3,5,10));
        getWindow().setNavigationBarColor(Color.rgb(3,5,10));
        WebView w = new WebView(this);
        w.setBackgroundColor(Color.rgb(3,5,10));
        w.setWebViewClient(new WebViewClient());
        w.setWebChromeClient(new WebChromeClient());
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setDatabaseEnabled(true);
        s.setBuiltInZoomControls(false);
        w.loadUrl("file:///android_asset/admin/index.html");
        setContentView(w);
    }
}
