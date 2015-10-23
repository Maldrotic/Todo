package com.maldrotic.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoListAdapter extends ArrayAdapter<TodoItem> {

    public TodoListAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, R.layout.todo_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.todo_list_item, null);
        }

        TodoItem item = getItem(position);

        if (item != null) {
            TextView title = (TextView) v.findViewById(R.id.itemTitle);
            if (title != null) {
                title.setText(item.getTitle());
            }
            TextView time = (TextView) v.findViewById(R.id.itemTime);
            if (time != null) {
                time.setText(item.getTime());
            }
            TextView date = (TextView) v.findViewById(R.id.itemDate);
            if (date != null) {
                date.setText(item.getDate());
            }
        }

        return v;
    }



}
