package com.example.pmisi.tumblr_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;


public class ContentWebActivity extends AppCompatActivity {
    private String url;
    private ArrayList<String> webList;
    private ArrayList<String> tagList;
    private String tittle;
    private LinearLayout linearLayout;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_web);

        Bundle b = getIntent().getExtras();
        webList = b.getStringArrayList("WebActivity");
        if (webList != null) {
            tagList = b.getStringArrayList("Tags");
            tittle = b.getString("Tittle");
            url = b.getString("Url");

            linearLayout = (LinearLayout) findViewById(R.id.activity_content_web_linearLayout);
            try {
                videoView = (VideoView) findViewById(R.id.activity_content_web_video_VideoView);
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
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ContentWebActivity.this);
                        dlgAlert.setMessage("Would you like to open in browser");
                        dlgAlert.setTitle("Cannot play video");
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
                        return false;
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
            }
            setupUi();
        }
    }

    private void setupUi() {
        StringBuilder fullTag = new StringBuilder();
        for (String tag : tagList) {
            fullTag.append("#");
            fullTag.append(tag);
            fullTag.append(" ");
        }
        if (!fullTag.toString().equals("")) {
            Log.i("FullTag", fullTag.toString());
            TextView textView = new TextView(ContentWebActivity.this);
            textView.setText(fullTag);
            textView.setPadding(0, 20, 0, 20);
            linearLayout.addView(textView);
        }
    }

    public void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            finish();
        }
    }
}
