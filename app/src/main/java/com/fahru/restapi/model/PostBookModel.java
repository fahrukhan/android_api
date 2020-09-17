package com.fahru.restapi.model;

import com.google.gson.annotations.SerializedName;

public class PostBookModel {
    private String title;
    private String author;
    private String published_date;
    private int pages;
    @SerializedName("body")
    private String language;

    public PostBookModel(String title, String author, String published_date, int pages, String language) {
        this.title = title;
        this.author = author;
        this.published_date = published_date;
        this.pages = pages;
        this.language = language;
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

    public int getPages() {
        return pages;
    }

    public String getLanguage() {
        return language;
    }
}
