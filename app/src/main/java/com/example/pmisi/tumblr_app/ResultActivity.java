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

    private RecyclerView recyclerView;
    private TextView username;
    private TextView tittle;
    private ArrayList<Content> content;
    private String usernameString;
    private String tittleString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_results);
        Bundle b = getIntent().getExtras();
        content = b.getParcelableArrayList("Content");
        usernameString = b.getString("UserName");
        tittleString = b.getString("UserTittle");
        recyclerView = (RecyclerView) findViewById(R.id.activity_results_recyclerView);
        username = (TextView) findViewById(R.id.activity_result_textView_userName);
        tittle = (TextView) findViewById(R.id.activity_result_textView_userTittle);
        serviceUi();
        super.onCreate(savedInstanceState);
    }

    private void serviceUi() {
        if (tittleString != null)
            tittle.setText(tittleString);
        username.setText(usernameString);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        UserContentAdapter mAdapter = new UserContentAdapter(content, getApplicationContext(), new UserContentAdapter.ItemSelectedListener() {
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
                    case "Audio":
                        intent = new Intent(ResultActivity.this, ContentAudioActivity.class);
                        intent.putStringArrayListExtra("AudioActivity", content.getContentList());
                        break;
                    case "Link":
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(content.getContentList().get(0)));
                        break;
                    case "Question":
                    case "Quote":
                    case "Text":
                    case "Chat":
                        intent = new Intent(ResultActivity.this, ContentTextActivity.class);
                        intent.putStringArrayListExtra("Text", content.getContentList());
                        break;
                }
                if (intent != null) {
                    intent.putExtra("Type", content.getType());
                    intent.putExtra("Tags", content.getTagsList());
                    intent.putExtra("Tittle", content.getTittle());
                    intent.putExtra("Url", content.getUrl());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
    }
}
