package com.example.pmisi.tumblr_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ContentAudioActivity extends AppCompatActivity {
    private String url;
    private ArrayList<String> webList;
    private ArrayList<String> tagList;
    private String tittle;
    private WebView webview;
    private LinearLayout linearLayout;
    private TextView tittleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_audio);
        Bundle b = getIntent().getExtras();
        tagList = b.getStringArrayList("Tags");
        tittle = b.getString("Tittle");
        url = b.getString("Url");
        webList = b.getStringArrayList("AudioActivity");
        webview = (WebView) findViewById(R.id.activity_content_audio_webView);
        linearLayout = (LinearLayout) findViewById(R.id.activity_content_audio_linearLayout);
        tittleTextView = (TextView) findViewById(R.id.activity_content_audio_TextView_tittle);
        if (webList != null) {
            serviceUi();
        }
    }

    private void serviceUi() {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContentAudioActivity.this);
                dlgAlert.setMessage("Would you like to open in browser");
                dlgAlert.setTitle("Cannot play audio");
                dlgAlert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                openWebPage(url);
                            }
                        });
                dlgAlert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.loadData(webList.get(0), "text/html", "uft-8");
        webview.getSettings().setLoadWithOverviewMode(true);

        StringBuilder fullTag = new StringBuilder();
        for (String tag : tagList) {
            fullTag.append("#");
            fullTag.append(tag);
            fullTag.append(" ");
        }
        if (!fullTag.toString().equals("")) {
            TextView textView = new TextView(ContentAudioActivity.this);
            textView.setText(fullTag);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(0, 20, 0, 20);
            linearLayout.addView(textView);
        }
        tittleTextView.setText(tittle);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webview.stopLoading();
        webview.destroy();
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();
        }
    }
}
