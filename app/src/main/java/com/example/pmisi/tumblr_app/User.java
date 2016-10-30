package com.example.pmisi.tumblr_app;

import java.util.ArrayList;

class User {
    private String name;
    private String title;
    private ArrayList<Content> contentList;

    User(String name, String title, ArrayList<Content> contentList) {
        this.name = name;
        this.title = title;
        this.contentList = contentList;
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


}
