package com.example.pmisi.tumblr_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class ContentWebActivity extends AppCompatActivity {
    String URL;
    Bundle b;
    ArrayList<String> webList;
    ArrayList<String> tagList;
    String tittle;
    WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_web);

        b = getIntent().getExtras();
        webList = b.getStringArrayList("WebActivity");
        for (String text : webList) {
            Log.i("Text is  ", text);
        }
        tagList = b.getStringArrayList("Tags");
        tittle = b.getString("Tittle");
        URL = b.getString("URL");
        Log.i("Text : ", String.valueOf(webList.size()));
        Log.i("PhotoTagSize : ", String.valueOf(tagList.size()));
        Log.i("URL : ", URL);

        LinearLayout linearLayoutWeb = (LinearLayout) findViewById(R.id.activity_content_web_video_linearLayout);
        webview = (WebView) findViewById(R.id.activity_content_web_video_webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(URL);
        //linearLayoutWeb.addView(webview);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webview.stopLoading();
        webview.destroy();
    }
}
