package com.shuhart.stepview.sample.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.shuhart.stepview.sample.R

import junit.framework.Assert

class MainAdapter : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    internal var items: List<Item>? = null
    internal var listener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.MainViewHolder {
        return MainViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_main, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainAdapter.MainViewHolder, position: Int) {
        holder.bind(items!![position])
    }

    override fun onViewDetachedFromWindow(holder: MainViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.listener = null
    }

    override fun onViewAttachedToWindow(holder: MainViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.listener = listener
    }

    override fun getItemCount(): Int {
        return if (items == null) 0 else items!!.size
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var listener: ItemClickListener? = null
        lateinit var item: Item

        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val subtitleTextView: TextView = itemView.findViewById(R.id.subtitle)

        fun bind(item: Item) {
            this.item = item
            var title: String? = null
            var subtitle: String? = null
            when (item) {
                MainAdapter.Item.SIMPLE -> {
                    title = titleTextView.context.getString(R.string.main_list_item_simple_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_simple_subtitle)
                }
                MainAdapter.Item.RECYCLER_VIEW -> {
                    title = titleTextView.context.getString(R.string.main_list_item_recyclerview_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_recyclerview_subtitle)
                }
                MainAdapter.Item.SCROLL_VIEW -> {
                    title = titleTextView.context.getString(R.string.main_list_item_scrollview_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_scrollview_subtitle)
                }
                MainAdapter.Item.CUSTOMISE -> {
                    title = titleTextView.context.getString(R.string.main_list_item_customise_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_customise_subtitle)
                }
                MainAdapter.Item.RTL -> {
                    title = titleTextView.context.getString(R.string.main_list_item_rtl_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_rtl_subtitle)
                }
                MainAdapter.Item.DELAYED_INIT -> {
                    title = titleTextView.context.getString(R.string.main_list_item_delayed_init_title)
                    subtitle = subtitleTextView.context.getString(R.string.main_list_item_delayed_init_subtitle)
                }
            }
            Assert.assertNotNull(title)
            Assert.assertNotNull(subtitle)
            titleTextView.text = title
            subtitleTextView.text = subtitle
            itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.onClick(item)
                }
            }
        }
    }

    interface ItemClickListener {
        fun onClick(item: Item)
    }

    enum class Item {
        SIMPLE,
        RECYCLER_VIEW,
        SCROLL_VIEW,
        CUSTOMISE,
        RTL,
        DELAYED_INIT
    }
}
