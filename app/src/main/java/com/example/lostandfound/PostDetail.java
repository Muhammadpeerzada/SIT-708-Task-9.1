package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetail extends AppCompatActivity {
    DatabaseHelper db;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TextView title = findViewById(R.id.titleTextView);
        TextView date = findViewById(R.id.postTime);
        TextView description = findViewById(R.id.descriptionTextView);
        TextView location = findViewById(R.id.postLocation);
        Button removePost = findViewById(R.id.removePost);
        db = new DatabaseHelper(this);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("post_id");
        Post post = db.getPostById(Integer.parseInt(postId));

        title.setText(post.getState() + " " + post.getPostName());
        date.setText(getTimeAgo(post.getDate()));
        description.setText(post.getDescription());
        location.setText(post.getLocation());


        removePost.setOnClickListener(view -> {
            String result;
            result = removeCurrentPost(post.getPost_id());
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            finish();
        });


    }
    private String removeCurrentPost(int post_id) {
        long result = db.deletePost(post_id);

        if (result > 0) return "Post has been removed successfully..!";
        return "There was an error removing the post. Please try again later";
    }

    private static String getTimeAgo(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.forLanguageTag("en-AU"));
        try {
            Date date = dateFormat.parse(dateString);
            long timeInMillis = date.getTime();

            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(timeInMillis, now, DateUtils.MINUTE_IN_MILLIS);
            return ago.toString();
        } catch (ParseException e) {
            Log.e("ParseException", e.getMessage(), e);
        }
        return dateString;
    }


}