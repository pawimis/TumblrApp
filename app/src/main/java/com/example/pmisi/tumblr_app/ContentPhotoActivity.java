package com.example.pmisi.tumblr_app;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by pmisi on 28.10.2016.
 */

public class ContentPhotoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_photo);
        Bundle b = getIntent().getExtras();
        ArrayList<String> photoUrlList = b.getStringArrayList("PhotoURL");
        ArrayList<String> tagList = b.getStringArrayList("Tags");
        String tittle = b.getString("Tittle");
        String URL = b.getString("URL");
        Log.i("PhotoUrlSize : " , String.valueOf(photoUrlList.size()));
        Log.i("PhotoTagSize : " , String.valueOf(tagList.size()));
        Log.i("URL : " , URL);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_content_photo_linearLayout);
        LinearLayout linearLayoutTag = (LinearLayout) findViewById(R.id.activity_content_photo_tag_linearLayout);
        TextView textViewTittle = (TextView) findViewById(R.id.activity_content_photo_TextView_tittle);
        TextView textViewUrl = (TextView) findViewById(R.id.activity_content_photo_url_linearLayout);
        textViewTittle.setText(tittle);
        textViewUrl.setText(URL);
        for (String text: tagList) {
            TextView textView = new TextView(ContentPhotoActivity.this);
            textView.setText("#" + text);
            linearLayoutTag.addView(textView);
        }
        downloadImages(linearLayout,photoUrlList);
    }
    private void downloadImages(LinearLayout linearLayout , ArrayList<String> photoUrlList){
        class DownloadImageTask extends AsyncTask<ArrayList<String>,Void,ArrayList<Bitmap>> {
            private LinearLayout linearLayout;
            private ProgressDialog loading;
            private DownloadImageTask(LinearLayout linearLayout){
                this.linearLayout = linearLayout;
            }
            @Override
            protected ArrayList<Bitmap> doInBackground(ArrayList<String>... url) {
                ArrayList<Bitmap> image = new ArrayList<>();
                for (String path: url[0]) {
                    try{
                        //InputStream input = new java.net.URL(path).openStream();
                        //image.add(BitmapFactory.decodeStream(input));
                    }catch (Exception e){
                        Log.e("Error", e.getMessage());
                    }
                }
                return image;
            }
            @Override
            protected void onPostExecute(ArrayList<Bitmap> image) {
                //for (Bitmap picture: image) {
                int i = 0;
                while (i < 5){
                    ImageView imageView = new ImageView(ContentPhotoActivity.this);
                imageView.setImageResource(R.drawable.audio);
                linearLayout.addView(imageView);
                  }
                //}
                loading.dismiss();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ContentPhotoActivity.this,"Fetch","Fetching data",true,true);

            }
        }
        DownloadImageTask download = new DownloadImageTask(linearLayout);
        download.execute(photoUrlList);

    }
}
