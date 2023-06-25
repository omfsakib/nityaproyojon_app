package com.myapp.nityaproyojon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import static android.webkit.WebChromeClient.FileChooserParams.parseResult;

public class MainActivity extends AppCompatActivity {
    private WebView mywebView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UploadSupport uploadSupport;
    private ValueCallback<Uri[]> mUMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        uploadSupport = new UploadSupport(MainActivity.this);

        mywebView = findViewById(R.id.webview);
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null){
            mywebView.loadUrl("https://nityaproyojon.com/");
        }
        WebSettings mywebsettings = mywebView.getSettings();
        mywebsettings.setJavaScriptEnabled(true);
        mywebView.setWebViewClient(new WebViewClient());
        mywebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mywebView.getSettings().setAppCacheEnabled(true);
        mywebView.getSettings().setAllowContentAccess(true);
        mywebView.getSettings().setAllowFileAccess(true);
        mywebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebsettings.setDomStorageEnabled(true);
        mywebsettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mywebsettings.setUseWideViewPort(true);
        mywebsettings.setSavePassword(true);
        mywebsettings.setSaveFormData(true);
        mywebsettings.setEnableSmoothTransition(true);


        progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        mywebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadSupport.showFileChooser(filePathCallback);
                mUMA = filePathCallback;
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    mywebView.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mywebView.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        mywebView.reload();
                    }
                }, 1500);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == UploadSupport.uploadCode){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                uploadSupport.showFileChooser(mUMA);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==UploadSupport.FCR){
            if (uploadSupport.mUMA!=null){
                uploadSupport.mUMA.onReceiveValue(parseResult(resultCode,data));
            }
        }
    }

    public class mywebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onBackPressed(){
        if(mywebView.canGoBack()) {
            mywebView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}