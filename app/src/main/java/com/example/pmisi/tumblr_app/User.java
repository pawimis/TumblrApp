package com.example.pmisi.tumblr_app;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

///TODO bez serializacji
class User implements Parcelable{
    private String name;
    private String title;
    private ArrayList<Content> contentList;

    User(String name, String title, ArrayList<Content> contentList) {
        this.name = name;
        this.title = title;
        this.contentList = contentList;
    }

    protected User(Parcel in) {
        name = in.readString();
        title = in.readString();
        contentList = in.readArrayList(null);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    ArrayList<Content> getContentList() {
        return contentList;
    }
    void addToContentList(Content content) {
        this.contentList.add(content);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(title);
        dest.writeTypedList(contentList);
    }
}
