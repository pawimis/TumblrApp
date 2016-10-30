package com.example.pmisi.tumblr_app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_results);
        Bundle b = getIntent().getExtras();
        ArrayList<Content> content =  b.getParcelableArrayList("Content");
        String usernameString = b.getString("UserName");
        String tittleString = b.getString("UserTittle");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_results_recyclerView);
        TextView username = (TextView) findViewById(R.id.activity_result_userName);
        TextView tittle = (TextView) findViewById(R.id.activity_result_userTittle);
        if (tittleString != null)
            tittle.setText(tittleString);
        username.setText(usernameString);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        AdapterUserContent mAdapter = new AdapterUserContent(content, getApplicationContext(), new AdapterUserContent.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Content content) {
                Intent intent = null;
                switch (content.getType()) {
                    case "Photo":
                        intent = new Intent(ResultActivity.this, ContentPhotoActivity.class);
                        intent.putStringArrayListExtra("PhotoURL", content.getContentList());
                        break;
                    case "Video":
                        intent = new Intent(ResultActivity.this, ContentWebActivity.class);
                        intent.putStringArrayListExtra("WebActivity", content.getContentList());
                        break;
                    case "Quote":
                    case "Text":
                    case "Chat":
                        intent = new Intent(ResultActivity.this, ContentTextActivity.class);
                        intent.putStringArrayListExtra("Text", content.getContentList());
                        break;
                    case "Audio":
                        intent = new Intent(ResultActivity.this, ContentAudioActivity.class);
                        intent.putStringArrayListExtra("AudioActivity", content.getContentList());
                        break;
                    case "Link":
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(content.getContentList().get(0)));
                        break;
                }
                if (intent != null) {
                    intent.putExtra("Type", content.getType());
                    intent.putExtra("Tags", content.getTagsList());
                    intent.putExtra("Tittle", content.getTittle());
                    intent.putExtra("URL", content.getUrl());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);


        super.onCreate(savedInstanceState);

    }
}
