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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;



public class ContentTextActivity extends AppCompatActivity {
    String URL;
    Bundle b;
    ArrayList<String> textList;
    ArrayList<String> tagList;
    String tittle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_text);
        b = getIntent().getExtras();
        textList = b.getStringArrayList("Text");
        if (textList != null) {
            for (String text : textList) {
                Log.i("Text is  ", text);
            }
            tagList = b.getStringArrayList("Tags");
            tittle = b.getString("Tittle");
            URL = b.getString("URL");
            Log.i("Text : ", String.valueOf(textList.size()));
            Log.i("PhotoTagSize : ", String.valueOf(tagList.size()));
            Log.i("URL : ", URL);

            TextView textViewTittle = (TextView) findViewById(R.id.activity_content_text_TextView_tittle);
            TextView textViewUrl = new TextView(ContentTextActivity.this);
            LinearLayout linearLayoutText = (LinearLayout) findViewById(R.id.activity_content_text__text_linearLayout);
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

            for (String text : textList) {
                TextView textView = new TextView(ContentTextActivity.this);
                textView.setText(text);
                textView.setPadding(20, 20, 20, 20);
                linearLayoutText.addView(textView);
            }
            String fullTag = "";
            for (String tag : tagList) {
                Log.i("Tag", "Wczytane");
                fullTag += "#" + tag + " ";
            }
            if (!fullTag.isEmpty()) {
                Log.i("FullTag", fullTag);
                TextView textView = new TextView(ContentTextActivity.this);
                textView.setText(fullTag);
                textView.setPadding(0, 20, 0, 20);
                linearLayoutText.addView(textView);
            }
            linearLayoutText.addView(textViewUrl);
        }
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
