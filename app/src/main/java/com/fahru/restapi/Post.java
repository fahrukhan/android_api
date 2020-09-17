package com.fahru.restapi;

import com.google.gson.annotations.SerializedName;

public class Post {
    private int id;
    private String title;
    private String author;
    private String published_date;
    private String pages;

    @SerializedName("language")
    private String language;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublished_date() {
        return published_date;
    }

    public String getPages() {
        return pages;
    }

    public String getLanguage() {
        return language;
    }
}
