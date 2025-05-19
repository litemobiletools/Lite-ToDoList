package com.litemobiletools.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Item> itemList;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    private OnCheckBoxClickListener checkboxListener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(Item item);
    }
    public interface OnCheckBoxClickListener {
        void onCheckChanged(Item item, boolean isChecked);
    }

    public MyAdapter(List<Item> itemList, OnItemClickListener listener, OnDeleteClickListener deleteListener, OnCheckBoxClickListener checkboxListener) {
        this.itemList = itemList;
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.checkboxListener = checkboxListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item, listener, deleteListener, checkboxListener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItemList(List<Item> newItemList) {
        itemList = newItemList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, dateTime;
        private ImageButton buttonDelete;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textView);
            dateTime = itemView.findViewById(R.id.dateTime);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(final Item item, final OnItemClickListener listener, final OnDeleteClickListener deleteListener, final OnCheckBoxClickListener checkboxListener) {
            itemName.setText(item.getName());
            dateTime.setText(item.getDate_Time());
            checkBox.setChecked(item.isChecked());

//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    checkboxListener.onCheckChanged(item, true);
//                }
//            });

            // Apply strikethrough if checked
            if (item.isChecked()) {
                itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                itemName.setPaintFlags(itemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                if (isChecked) {
                    itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    itemName.setPaintFlags(itemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                checkboxListener.onCheckChanged(item, isChecked);
            });

            //itemView.setOnClickListener(v -> listener.onItemClick(item));
            //buttonDelete.setOnClickListener(v -> deleteListener.onDeleteClick(item));



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDeleteClick(item);
                }
            });
        }


    }
}

class Item {
    private int id;
    private String name;
    private String date_Time;
    private boolean isChecked;

    public Item(int id, String name, String date_Time) {
        this.id = id;
        this.name = name;
        this.date_Time = date_Time;
        this.isChecked = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getDate_Time() {
        return date_Time;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}