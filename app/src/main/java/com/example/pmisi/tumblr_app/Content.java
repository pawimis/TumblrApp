package com.example.pmisi.tumblr_app;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

class Content implements Parcelable{
    private String url;
    private String type;
    private String date;
    private String tittle;
    private Bitmap bitmap;
    private ArrayList<String> contentList;
    private ArrayList<String> tagsList;

    protected Content(Parcel in) {
        url = in.readString();
        type = in.readString();
        date = in.readString();
        tittle = in.readString();
        contentList = in.createStringArrayList();
        tagsList = in.createStringArrayList();
    }
    Content(String url, String date, String tittle) {
        this.url = url;
        this.date = date;
        this.tittle = tittle;
        this.contentList = new ArrayList<>();
        this.tagsList = new ArrayList<>();

    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    void appendToTittle(String toAppend){
        this.tittle += (" " +toAppend);
    }
    String getType() {
        return type;
    }
    void setType(String type) {
        this.type = type;
    }
    ArrayList<String> getContentList() {
        return contentList;
    }
    void addContent(String quote) {
        if(!contentList.contains(quote))
            contentList.add(quote);
    }
    public ArrayList<String> getTagsList() {return tagsList;}
    void addTag(String tag) {tagsList.add(tag);}
    String getTittle() {
        return tittle;
    }
    void setTittle(String tittle) {
        this.tittle = tittle;
    }
    String getDate() {
        return date;
    }
    public String getUrl() {
        return url;
    }




    public static final Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(type);
        dest.writeString(date);
        dest.writeString(tittle);
        dest.writeStringList(contentList);
        dest.writeStringList(tagsList);
    }
}

