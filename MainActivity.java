package com.example.blogmanagementsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQUEST_BLOG_FORM = 100;

    private LinearLayout blogContainer;
    private TextView tvBlogCount;
    private BlogDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blogContainer = findViewById(R.id.blogContainer);
        tvBlogCount = findViewById(R.id.tvBlogCount);
        Button btnAddBlog = findViewById(R.id.btnAddBlog);

        databaseHelper = new BlogDatabaseHelper(this);

        btnAddBlog.setOnClickListener(v -> openAddForm());

        loadBlogs();
    }

    private void openAddForm() {
        Intent intent = new Intent(
                MainActivity.this, BlogFormActivity.class);
        startActivityForResult(intent, REQUEST_BLOG_FORM);
    }

    private void openEditForm(Blog blog) {
        Intent intent = new Intent(
                MainActivity.this, BlogFormActivity.class);

        intent.putExtra("blog_id", blog.getId());
        intent.putExtra("blog_title", blog.getTitle());
        intent.putExtra("blog_author", blog.getAuthor());
        intent.putExtra("blog_content", blog.getContent());

        startActivityForResult(intent, REQUEST_BLOG_FORM);
    }

    private void loadBlogs() {
        blogContainer.removeAllViews();

        ArrayList<Blog> blogs = databaseHelper.getAllBlogs();

        tvBlogCount.setText("Your Blogs (" + blogs.size() + ")");

        if (blogs.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText(
                    "No blogs yet.\nTap 'Add New Blog' to get started.");
            emptyView.setTextSize(16);
            emptyView.setTextColor(Color.GRAY);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setPadding(20, 60, 20, 60);

            blogContainer.addView(emptyView);
            return;
        }

        for (Blog blog : blogs) {
            addBlogCard(blog);
        }
    }

    private void addBlogCard(Blog blog) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(20);
        background.setStroke(1, Color.rgb(225, 229, 238));
        card.setBackground(background);
        card.setElevation(3);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        cardParams.setMargins(0, 0, 0, 16);
        blogContainer.addView(card, cardParams);

        TextView title = new TextView(this);
        title.setText(blog.getTitle());
        title.setTextSize(20);
        title.setTextColor(Color.rgb(23, 28, 66));
        title.setTypeface(null, Typeface.BOLD);
        card.addView(title);

        TextView author = new TextView(this);
        author.setText("By " + blog.getAuthor());
        author.setTextSize(13);
        author.setTextColor(Color.DKGRAY);
        author.setPadding(0, 6, 0, 4);
        card.addView(author);

        TextView date = new TextView(this);
        date.setText(blog.getDate());
        date.setTextSize(12);
        date.setTextColor(Color.GRAY);
        date.setPadding(0, 0, 0, 10);
        card.addView(date);

        TextView content = new TextView(this);
        String text = blog.getContent();
        if (text.length() > 120) {
            text = text.substring(0, 120) + "...";
        }
        content.setText(text);
        content.setTextSize(14);
        content.setTextColor(Color.rgb(75, 75, 85));
        card.addView(content);

        // Buttons are displayed in one row.
        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setGravity(Gravity.CENTER_VERTICAL);
        buttonRow.setPadding(0, 12, 0, 0);
        card.addView(buttonRow);

        Button viewButton = createButton("View", "#455A64");
        Button editButton = createButton("Edit", "#304FFE");
        Button deleteButton = createButton("Delete", "#D32F2F");

        buttonRow.addView(viewButton, buttonParams());
        buttonRow.addView(editButton, buttonParams());
        buttonRow.addView(deleteButton, buttonParams());

        viewButton.setOnClickListener(v -> showBlogDetails(blog));
        editButton.setOnClickListener(v -> openEditForm(blog));
        deleteButton.setOnClickListener(v -> confirmDelete(blog));
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f);

        params.setMargins(3, 0, 3, 0);
        return params;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(12);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor(color)));
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(2, 0, 2, 0);
        return button;
    }

    private void showBlogDetails(Blog blog) {
        new AlertDialog.Builder(this)
                .setTitle(blog.getTitle())
                .setMessage(
                        "Author: " + blog.getAuthor() +
                                "\nDate: " + blog.getDate() +
                                "\n\n" + blog.getContent())
                .setPositiveButton("Close", null)
                .setNeutralButton("Edit",
                        (dialog, which) -> openEditForm(blog))
                .show();
    }

    private void confirmDelete(Blog blog) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Blog")
                .setMessage(
                        "Are you sure you want to delete \"" +
                                blog.getTitle() + "\"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result =
                            databaseHelper.deleteBlog(blog.getId());

                    if (result > 0) {
                        Toast.makeText(this,
                                "Blog deleted successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Unable to delete blog",
                                Toast.LENGTH_SHORT).show();
                    }

                    loadBlogs();
                })
                .show();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BLOG_FORM
                && resultCode == RESULT_OK) {
            loadBlogs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBlogs();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}