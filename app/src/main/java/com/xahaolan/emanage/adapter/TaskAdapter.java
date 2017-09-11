package com.xahaolan.emanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xahaolan.emanage.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/9.
 */

public class TaskAdapter extends BaseAdapter {
    private static final String TAG = TaskAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, null);
            holder.head_text = (TextView) convertView.findViewById(R.id.item_task_head);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_task_name);
            holder.content_text = (TextView) convertView.findViewById(R.id.item_task_content);
            holder.cutdown_time_text = (TextView) convertView.findViewById(R.id.item_task_cutdown_time);
            holder.time_text = (TextView) convertView.findViewById(R.id.item_task_time);
            holder.state_text = (TextView) convertView.findViewById(R.id.item_task_state);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> data = list.get(position);
        if (data != null) {

        }
        return convertView;
    }

    private class ViewHolder {
        private TextView head_text;
        private TextView name_text;
        private TextView content_text;
        private TextView cutdown_time_text;
        private TextView time_text;
        private TextView state_text;
    }

    public void resetList(List<Map<String, Object>> list) {
        if (this.list != null) this.list.clear();
        else this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public void appendList(List<Map<String, Object>> list) {
        if (this.list == null) this.list = new ArrayList<>();
        this.list.addAll(list);
    }
}
