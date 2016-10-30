package com.example.pmisi.tumblr_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class ContentAudioActivity extends AppCompatActivity {
    String URL;
    Bundle b;
    ArrayList<String> webList;
    ArrayList<String> tagList;
    String tittle;
    WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_audio);

        b = getIntent().getExtras();
        webList = b.getStringArrayList("AudioActivity");
        if (webList != null) {
            for (String text : webList) {
                Log.i("Text is  ", text);
            }
            tagList = b.getStringArrayList("Tags");
            tittle = b.getString("Tittle");
            URL = b.getString("URL");
            Log.i("Text : ", String.valueOf(webList.size()));
            Log.i("PhotoTagSize : ", String.valueOf(tagList.size()));
            Log.i("URL : ", URL);

            LinearLayout linearLayoutWeb = (LinearLayout) findViewById(R.id.activity_content_audio_audio_linearLayout);
            webview = new WebView(ContentAudioActivity.this);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

            });
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.loadData(webList.get(0), "text/html", "uft-8");
            webview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayoutWeb.addView(webview);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webview.stopLoading();
        webview.destroy();
    }
}
