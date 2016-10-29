package com.example.pmisi.tumblr_app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import java.util.ArrayList;


public class ResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_results);
        Bundle b = getIntent().getExtras();
        ArrayList<Content> content =  b.getParcelableArrayList("Content");
        recyclerView = (RecyclerView) findViewById(R.id.activity_results_recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if(layoutManager != null){
            if(recyclerView != null){
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                AdapterUserContent mAdapter = new AdapterUserContent(content, getApplicationContext(), new AdapterUserContent.ItemSelectedListener() {
                    @Override
                    public void onItemSelected(View itemView, Content content) {
                        Intent intent = null;
                        if(content.getType().equals("Photo")){
                            intent = new Intent(ResultActivity.this,ContentPhotoActivity.class);
                            intent.putStringArrayListExtra("PhotoURL",content.getContentList());
                        }else if(content.getType().equals("Link") || content.getType().equals("Video") || content.getType().equals("Audio")){
                            intent = new Intent(ResultActivity.this,ContentWebActivity.class);
                            intent.putStringArrayListExtra("WebActivity",content.getContentList());
                        }else if(content.getType().equals("Quote") || content.getType().equals("Text") || content.getType().equals("Chat")) {
                            intent = new Intent(ResultActivity.this, ContentTextActivity.class);
                            intent.putStringArrayListExtra("Text", content.getContentList());
                        }
                        intent.putExtra("Type",content.getType());
                        intent.putExtra("Tags",content.getTagsList());
                        intent.putExtra("Tittle",content.getTittle());
                        intent.putExtra("URL",content.getUrl());
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(mAdapter);
            }
            else
                Log.i("Result","null recycler");
        }else
            Log.i("Result","null layout manager");

        super.onCreate(savedInstanceState);

    }
}
