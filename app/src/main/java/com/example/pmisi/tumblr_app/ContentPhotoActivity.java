package com.example.pmisi.tumblr_app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContentPhotoActivity extends AppCompatActivity{
    private String url;
    private ArrayList<String> photoUrlList;
    private ArrayList<String> tagList;
    private String tittle;
    private LinearLayout linearLayout;
    private TextView textViewTittle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_photo);
        Bundle b = getIntent().getExtras();
        photoUrlList = b.getStringArrayList("PhotoURL");
        tagList = b.getStringArrayList("Tags");
        tittle = b.getString("Tittle");
        url = b.getString("Url");


        linearLayout = (LinearLayout) findViewById(R.id.activity_content_photo_linearLayout);
        textViewTittle = (TextView) findViewById(R.id.activity_content_photo_TextView_tittle);
        serviceUi();

    }

    private void serviceUi() {
        TextView textViewUrl = new TextView(ContentPhotoActivity.this);
        textViewTittle.setText(tittle);
        textViewTittle.setGravity(Gravity.CENTER_HORIZONTAL);

        textViewUrl.setClickable(true);
        textViewUrl.setMovementMethod(LinkMovementMethod.getInstance());
        textViewUrl.setText(url);
        textViewUrl.setTextColor(Color.WHITE);
        textViewUrl.setPadding(20, 20, 20, 20);
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(url);
            }
        });

        for (String photo : photoUrlList) {
            if (photo.contains("250.gif")) {
                loadGif(photo, linearLayout);
            } else {
                WebView webView = new WebView(ContentPhotoActivity.this);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl(photo);
                linearLayout.addView(webView);
            }
        }
        StringBuilder fullTag = new StringBuilder();
        for (String tag : tagList) {
            fullTag.append("#");
            fullTag.append(tag);
            fullTag.append(" ");
        }
        if (!fullTag.toString().equals("")) {
            TextView textView = new TextView(ContentPhotoActivity.this);
            textView.setText(fullTag);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(0, 20, 0, 20);
            linearLayout.addView(textView);
        }
        linearLayout.addView(textViewUrl);
    }

    private void loadGif(String url, LinearLayout linearLayout) {
        InputStream stream = null;
        try {
            stream = getAssets().open(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebView webView = new WebView(ContentPhotoActivity.this);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        WebService view = new WebService(this, stream);
        linearLayout.addView(view);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.toString());
            return true;
        }
    }
}
