package com.example.pmisi.tumblr_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContentPhotoActivity extends AppCompatActivity{
    String URL;
    Bundle b;
    ArrayList<String> photoUrlList;
    ArrayList<String> tagList;
    String tittle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_photo);
        b = getIntent().getExtras();
        photoUrlList = b.getStringArrayList("PhotoURL");
        tagList = b.getStringArrayList("Tags");
        tittle = b.getString("Tittle");
        URL = b.getString("URL");
        Log.i("PhotoUrlSize : " , String.valueOf(photoUrlList.size()));
        Log.i("PhotoTagSize : " , String.valueOf(tagList.size()));
        Log.i("URL : " , URL);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_content_photo_linearLayout);
        TextView textViewTittle = (TextView) findViewById(R.id.activity_content_photo_TextView_tittle);
        TextView textViewUrl = new TextView(ContentPhotoActivity.this);
        textViewTittle.setText(tittle);
        textViewTittle.setGravity(Gravity.CENTER_HORIZONTAL);

        textViewUrl.setClickable(true);
        textViewUrl.setMovementMethod(LinkMovementMethod.getInstance());
        textViewUrl.setText(URL);
        textViewUrl.setPadding(0, 20, 0, 20);
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(URL);
            }
        });

        for (String photo : photoUrlList) {
            Log.i("Photo", "Wczytane " + photo);
            if (photo.contains("250.gif")) {
                Log.i("Gif", "Wczytane");
                loadGif(photo, linearLayout);
            } else {
                WebView webView = new WebView(ContentPhotoActivity.this);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.loadUrl(photo);
                linearLayout.addView(webView);
            }
        }
        String fullTag = "";
        for (String tag : tagList) {
            Log.i("Tag", "Wczytane");
            fullTag += "#" + tag + " ";
        }
        if (!fullTag.isEmpty()) {
            Log.i("FullTag", fullTag);
            TextView textView = new TextView(ContentPhotoActivity.this);
            textView.setText(fullTag);
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

        GifWebView view = new GifWebView(this, stream);
        linearLayout.addView(view);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    ///ToDo coś z tym
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
