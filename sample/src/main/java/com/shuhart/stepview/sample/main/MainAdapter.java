package com.shuhart.stepview.sample.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuhart.stepview.sample.R;

import org.junit.Assert;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    List<Item> items;
    ItemClickListener listener;

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_main, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MainViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.listener = null;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MainViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {

        ItemClickListener listener;
        Item item;

        private TextView titleTextView;
        private TextView subtitleTextView;

        MainViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            subtitleTextView = itemView.findViewById(R.id.subtitle);
        }

        void bind(final Item item) {
            this.item = item;
            String title = null;
            String subtitle = null;
            switch (item) {
                case SIMPLE: {
                    title = titleTextView.getContext().getString(R.string.main_list_item_simple_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_simple_subtitle);
                    break;
                }
                case RECYCLER_VIEW: {
                    title = titleTextView.getContext().getString(R.string.main_list_item_recyclerview_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_recyclerview_subtitle);
                    break;
                }
                case SCROLL_VIEW: {
                    title = titleTextView.getContext().getString(R.string.main_list_item_scrollview_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_scrollview_subtitle);
                    break;
                }
                case CUSTOMISE:
                    title = titleTextView.getContext().getString(R.string.main_list_item_customise_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_customise_subtitle);
                    break;
                case RTL:
                    title = titleTextView.getContext().getString(R.string.main_list_item_rtl_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_rtl_subtitle);
                    break;
                case DELAYED_INIT:
                    title = titleTextView.getContext().getString(R.string.main_list_item_delayed_init_title);
                    subtitle = subtitleTextView.getContext().getString(R.string.main_list_item_delayed_init_subtitle);
                    break;
            }
            Assert.assertNotNull(title);
            Assert.assertNotNull(subtitle);
            titleTextView.setText(title);
            subtitleTextView.setText(subtitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(item);
                    }
                }
            });
        }
    }

    public interface ItemClickListener {
        void onClick(Item item);
    }

    public enum Item {
        SIMPLE,
        RECYCLER_VIEW,
        SCROLL_VIEW,
        CUSTOMISE,
        RTL,
        DELAYED_INIT
    }
}
