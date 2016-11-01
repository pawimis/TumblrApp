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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;



public class ContentTextActivity extends AppCompatActivity {
    private String url;
    private ArrayList<String> textList;
    private ArrayList<String> tagList;
    private String tittle;
    private TextView textViewTittle;
    private TextView textViewUrl;
    private LinearLayout linearLayoutText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_text);
        Bundle b = getIntent().getExtras();
        textList = b.getStringArrayList("Text");
        if (textList != null) {
            tagList = b.getStringArrayList("Tags");
            tittle = b.getString("Tittle");
            url = b.getString("Url");

            textViewTittle = (TextView) findViewById(R.id.activity_content_text_TextView_tittle);
            textViewUrl = new TextView(ContentTextActivity.this);
            linearLayoutText = (LinearLayout) findViewById(R.id.activity_content_text_linearLayout);
            serviceUi();

        }
    }

    private void serviceUi() {
        textViewTittle.setText(tittle);
        textViewTittle.setGravity(Gravity.CENTER_HORIZONTAL);

        textViewUrl.setClickable(true);
        textViewUrl.setMovementMethod(LinkMovementMethod.getInstance());
        textViewUrl.setText(url);
        textViewUrl.setPadding(0, 20, 0, 20);
        textViewUrl.setTextColor(Color.WHITE);
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(url);
            }
        });

        for (String text : textList) {
            TextView textView = new TextView(ContentTextActivity.this);
            textView.setText(text);
            textView.setBackgroundColor(Color.parseColor("#242f3e"));
            textView.setTextColor(Color.WHITE);
            textView.setPadding(0, 20, 20, 20);
            linearLayoutText.addView(textView);
        }
        StringBuilder fullTag = new StringBuilder();
        for (String tag : tagList) {
            fullTag.append("#");
            fullTag.append(tag);
            fullTag.append(" ");
        }
        if (!fullTag.toString().equals("")) {
            TextView textView = new TextView(ContentTextActivity.this);
            textView.setText(fullTag);
            textView.setPadding(0, 20, 0, 20);
            textView.setTextColor(Color.WHITE);
            linearLayoutText.addView(textView);
        }
        linearLayoutText.addView(textViewUrl);
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
