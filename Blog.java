package com.example.blogmanagementsystem;

public class Blog {
    private int id;
    private String title;
    private String author;
    private String content;
    private String date;

    public Blog(int id, String title, String author,
                String content, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}