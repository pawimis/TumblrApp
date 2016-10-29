package com.example.pmisi.tumblr_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;



public class ContentTextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_text);
        Bundle b = getIntent().getExtras();
        ArrayList<String> textList = b.getStringArrayList("Text");
        ArrayList<String> tagList = b.getStringArrayList("Tags");
        String tittle = b.getString("Tittle");
        String URL = b.getString("URL");
        Log.i("Text : " , String.valueOf(textList.size()));
        Log.i("PhotoTagSize : " , String.valueOf(tagList.size()));
        Log.i("URL : " , URL);

        TextView textViewTittle = (TextView) findViewById(R.id.activity_content_text_TextView_tittle);
        TextView textViewUrl = (TextView) findViewById(R.id.activity_content_text_TextView_url);
        LinearLayout linearLayoutText = (LinearLayout) findViewById(R.id.activity_content_text__text_linearLayout);
        LinearLayout linearLayoutTag = (LinearLayout) findViewById(R.id.activity_content_text_tag_linearLayout);
        textViewTittle.setText(tittle);
        textViewUrl.setText(URL);
        for(String text : textList){
            TextView textView = new TextView(ContentTextActivity.this);
            textView.setText(text);
            textView.setPadding(20,20,20,20);
            linearLayoutText.addView(textView);
        }
        for(String tag : tagList){
            TextView textView = new TextView(ContentTextActivity.this);
            textView.setText(tag);
            linearLayoutTag.addView(textView);
        }


    }
}
