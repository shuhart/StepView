package com.shuhart.stepview.sample.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shuhart.stepview.sample.R;
import com.shuhart.stepview.sample.examples.customise.CustomiseActivity;
import com.shuhart.stepview.sample.examples.recyclerview.RecyclerViewExampleActivity;
import com.shuhart.stepview.sample.examples.scrollview.ScrollViewExampleActivity;
import com.shuhart.stepview.sample.examples.simple.SimpleActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainAdapter adapter = new MainAdapter();
        adapter.items = new ArrayList<MainAdapter.Item>() {{
            add(MainAdapter.Item.SIMPLE);
            add(MainAdapter.Item.RECYCLER_VIEW);
            add(MainAdapter.Item.SCROLL_VIEW);
            add(MainAdapter.Item.CUSTOMISE);
        }};
        adapter.listener = new MainAdapter.ItemClickListener() {
            @Override
            public void onClick(MainAdapter.Item item) {
                switch (item) {
                    case SIMPLE:
                        startActivity(new Intent(MainActivity.this, SimpleActivity.class));
                        break;
                    case RECYCLER_VIEW:
                        startActivity(new Intent(MainActivity.this, RecyclerViewExampleActivity.class));
                        break;
                    case SCROLL_VIEW:
                        startActivity(new Intent(MainActivity.this, ScrollViewExampleActivity.class));
                        break;
                    case CUSTOMISE:
                        startActivity(new Intent(MainActivity.this, CustomiseActivity.class));
                        break;
                }
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
