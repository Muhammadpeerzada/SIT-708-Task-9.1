package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShowAllPosts extends AppCompatActivity implements PostAdapter.OnItemClickListener {
    DatabaseHelper db;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_posts);
        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.rv_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this::onItemClick);
        recyclerView.setAdapter(postAdapter);

        postList.addAll((Collection<? extends Post>) db.getAllPosts());
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Post post) {
        Intent intent = new Intent(this, PostDetail.class);
        intent.putExtra("post_id", String.valueOf(post.getPost_id()));
        startActivity(intent);
    }
}
