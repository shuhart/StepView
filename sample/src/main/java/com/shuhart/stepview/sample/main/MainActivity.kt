package com.shuhart.stepview.sample.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.shuhart.stepview.sample.R
import com.shuhart.stepview.sample.examples.customise.CustomiseActivity
import com.shuhart.stepview.sample.examples.delayed.DelayedInitActivity
import com.shuhart.stepview.sample.examples.recyclerview.RecyclerViewExampleActivity
import com.shuhart.stepview.sample.examples.rtl.RTLActivity
import com.shuhart.stepview.sample.examples.scrollview.ScrollViewExampleActivity
import com.shuhart.stepview.sample.examples.simple.SimpleActivity

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        adapter.items = object : ArrayList<MainAdapter.Item>() {
            init {
                add(MainAdapter.Item.SIMPLE)
                add(MainAdapter.Item.RECYCLER_VIEW)
                add(MainAdapter.Item.SCROLL_VIEW)
                add(MainAdapter.Item.CUSTOMISE)
                add(MainAdapter.Item.RTL)
                add(MainAdapter.Item.DELAYED_INIT)
            }
        }
        adapter.listener = object : MainAdapter.ItemClickListener {
            override fun onClick(item: MainAdapter.Item) {
                when (item) {
                    MainAdapter.Item.SIMPLE -> startActivity(Intent(this@MainActivity, SimpleActivity::class.java))
                    MainAdapter.Item.RECYCLER_VIEW -> startActivity(Intent(this@MainActivity, RecyclerViewExampleActivity::class.java))
                    MainAdapter.Item.SCROLL_VIEW -> startActivity(Intent(this@MainActivity, ScrollViewExampleActivity::class.java))
                    MainAdapter.Item.CUSTOMISE -> startActivity(Intent(this@MainActivity, CustomiseActivity::class.java))
                    MainAdapter.Item.RTL -> startActivity(Intent(this@MainActivity, RTLActivity::class.java))
                    MainAdapter.Item.DELAYED_INIT -> startActivity(Intent(this@MainActivity, DelayedInitActivity::class.java))
                }
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
