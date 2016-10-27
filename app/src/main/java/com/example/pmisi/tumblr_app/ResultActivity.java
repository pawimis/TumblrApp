package com.example.pmisi.tumblr_app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
                AdapterUserContent mAdapter = new AdapterUserContent(content,getApplicationContext());
                recyclerView.setAdapter(mAdapter);
            }
            else
                Log.i("Result","null recycler");
        }else
            Log.i("Result","null layout manager");

        super.onCreate(savedInstanceState);

    }
}
