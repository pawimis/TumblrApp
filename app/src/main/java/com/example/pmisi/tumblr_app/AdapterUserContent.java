package com.example.pmisi.tumblr_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;


class AdapterUserContent  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "AdapterUserContent";
    private LayoutInflater inflater;
    private Context appContext;
    private ArrayList<Content> content;


    AdapterUserContent(ArrayList<Content> contentList, Context applicationContext) {
        this.appContext = applicationContext;
        inflater = LayoutInflater.from(applicationContext);
        this.content = contentList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.container_content,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        Content data = content.get(position);
        if(!data.getTittle().isEmpty())
            myHolder.tittleTextView.setText(data.getTittle());
        else
            myHolder.tittleTextView.setText(R.string.no_tittle);
        myHolder.typeTextView.setText(data.getType());
        myHolder.dateTextView.setText(data.getDate());
        if(data.getType().equals("Photo")){
            if(data.getBitmap() == null)
                downloadImage(myHolder.imageView,data);
            else
                myHolder.imageView.setImageBitmap(data.getBitmap());
        }else if(data.getType().equals("Link")){
            myHolder.imageView.setImageResource(R.drawable.link);
        }else if(data.getType().equals("Chat")){
            myHolder.imageView.setImageResource(R.drawable.chat);
        }else if(data.getType().equals("Video")){
            myHolder.imageView.setImageResource(R.drawable.video);
        }else if(data.getType().equals("Quote")){
            myHolder.imageView.setImageResource(R.drawable.quote);
        }else if(data.getType().equals("Audio")){
            myHolder.imageView.setImageResource(R.drawable.audio);
        }else if(data.getType().equals("Text")){
            myHolder.imageView.setImageResource(R.drawable.text);
        }

    }

    @Override
    public int getItemCount() {
        return content.size();
    }
    private class MyHolder extends RecyclerView.ViewHolder{
        TextView tittleTextView;
        TextView dateTextView;
        TextView typeTextView;
        ImageView imageView;
        MyHolder(View itemView) {
            super(itemView);
            tittleTextView = (TextView) itemView.findViewById(R.id.container_content_textView_tittle);
            dateTextView = (TextView) itemView.findViewById(R.id.container_content_textView_date);
            typeTextView = (TextView) itemView.findViewById(R.id.container_content_textView_type);
            imageView = (ImageView) itemView.findViewById(R.id.container_content_imageView);
        }
    }
    private void downloadImage(ImageView imageView ,Content data){
         class DownloadImageTask extends AsyncTask<String,Void,Bitmap>{
             private ImageView imageView;
             private Content data;
             private DownloadImageTask(ImageView bitmap,Content data){
                 this.imageView = bitmap;
                 this.data = data;
             }
             @Override
             protected Bitmap doInBackground(String... url) {
                 String urlDisplay = url[0];
                 if(urlDisplay.contains("500.png")){
                     urlDisplay=urlDisplay.replaceAll("500.png","75sq.png");
                 }else if(urlDisplay.contains("500.jpg")){
                     urlDisplay=  urlDisplay.replaceAll("500.png","75sq.jpg");
                 }else if(urlDisplay.contains("250.gif")) {
                     urlDisplay= urlDisplay.replaceAll("500.png", "75sq.gif");
                 }
                 Bitmap image = null;
                 try{
                     InputStream input = new java.net.URL(urlDisplay).openStream();
                     image = BitmapFactory.decodeStream(input);
                 }catch (Exception e){
                     Log.e("Error", e.getMessage());
                 }
                 return image;
             }

             @Override
             protected void onPostExecute(Bitmap bitmap) {
                 imageView.setImageBitmap(bitmap);
                 data.setBitmap(bitmap);

             }
         }
        DownloadImageTask download = new DownloadImageTask(imageView,data);
        download.execute(data.getContentList().get(0));

    }
}
