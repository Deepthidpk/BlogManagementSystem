package com.example.blogmanagementsystem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BlogFormActivity extends Activity {

    private EditText etTitle, etAuthor, etContent;
    private TextView tvFormHeading;
    private Button btnSaveBlog, btnCancel;
    private BlogDatabaseHelper databaseHelper;
    private int blogId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_form);

        databaseHelper = new BlogDatabaseHelper(this);

        tvFormHeading = findViewById(R.id.tvFormHeading);
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etContent = findViewById(R.id.etContent);
        btnSaveBlog = findViewById(R.id.btnSaveBlog);
        btnCancel = findViewById(R.id.btnCancel);

        // If an ID is provided, this form is in edit mode.
        blogId = getIntent().getIntExtra("blog_id", -1);

        if (blogId != -1) {
            tvFormHeading.setText("Edit Blog");
            btnSaveBlog.setText("Update Blog");

            etTitle.setText(
                    getIntent().getStringExtra("blog_title"));
            etAuthor.setText(
                    getIntent().getStringExtra("blog_author"));
            etContent.setText(
                    getIntent().getStringExtra("blog_content"));
        }

        btnSaveBlog.setOnClickListener(v -> saveBlog());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveBlog() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Please enter a blog title");
            etTitle.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            etAuthor.setError("Please enter the author name");
            etAuthor.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etContent.setError("Please enter blog content");
            etContent.requestFocus();
            return;
        }

        if (blogId == -1) {
            long result = databaseHelper.addBlog(
                    title, author, content);

            if (result != -1) {
                Toast.makeText(this,
                        "Blog added successfully",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this,
                        "Unable to save blog",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int result = databaseHelper.updateBlog(
                    blogId, title, author, content);

            if (result > 0) {
                Toast.makeText(this,
                        "Blog updated successfully",
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this,
                        "Unable to update blog",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}