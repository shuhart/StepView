package com.shuhart.stepview.sample.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuhart.stepview.sample.R;
import com.shuhart.stepview.sample.examples.customise.CustomiseActivity;
import com.shuhart.stepview.sample.examples.delayed.DelayedInitActivity;
import com.shuhart.stepview.sample.examples.recyclerview.RecyclerViewExampleActivity;
import com.shuhart.stepview.sample.examples.rtl.RTLActivity;
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
            add(MainAdapter.Item.RTL);
            add(MainAdapter.Item.DELAYED_INIT);
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
                    case RTL:
                        startActivity(new Intent(MainActivity.this, RTLActivity.class));
                        break;
                    case DELAYED_INIT:
                        startActivity(new Intent(MainActivity.this, DelayedInitActivity.class));
                        break;
                }
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
