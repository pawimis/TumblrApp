package com.example.pmisi.tumblr_app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;


public class ContentWebActivity extends AppCompatActivity {
    String URL;
    Bundle b;
    ArrayList<String> webList;
    ArrayList<String> tagList;
    String tittle;
    WebView webview;
    VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_web);

        b = getIntent().getExtras();
        webList = b.getStringArrayList("WebActivity");
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

           /* LinearLayout linearLayoutWeb = (LinearLayout) findViewById(R.id.activity_content_web_video_linearLayout);
            //webview = (WebView) findViewById(R.id.activity_content_web_video_webView);
            videoView = (VideoView) findViewById(R.id.activity_content_web_video_VideoView);
            webview.getSettings().setJavaScriptEnabled(true);
            MediaController mc = new MediaController(this);
            mc.setAnchorView(videoView);
            mc.setMediaPlayer(videoView);
            webview.loadUrl(URL);
            //linearLayoutWeb.addView(webview);*/
            try {
                VideoView videoView = (VideoView) findViewById(R.id.activity_content_web_video_VideoView);
                final ProgressBar pbLoading = new ProgressBar(ContentWebActivity.this);
                pbLoading.setVisibility(View.VISIBLE);
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                Uri video = Uri.parse(webList.get(0));
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(video);
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        pbLoading.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
