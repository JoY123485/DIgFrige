package com.example.digfrige;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<String> itemList;
    private List<String[]> data;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String url);
    }

    public ItemAdapter(List<String> itemList, List<String[]> data) {
        this.itemList = itemList;
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = itemList.get(position);
        String url = null;
        for (String[] row : data) {
            if (row[0].equals(item) && row.length > 1) {
                url = row[1];
                break;
            }
        }
        holder.bind(item, url);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateList(List<String> newList) {
        itemList.clear();
        itemList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private String url;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && url != null) {
                        listener.onItemClick(url);
                    }
                }
            });
        }

        public void bind(String item, String url) {
            textView.setText(item);
            this.url = url;
        }
    }
}
