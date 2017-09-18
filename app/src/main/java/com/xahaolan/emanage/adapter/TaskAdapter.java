package com.xahaolan.emanage.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.utils.mine.MyUtils;

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, null);
            holder.head_text = (TextView) convertView.findViewById(R.id.item_task_head);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_task_name);
            holder.content_text = (TextView) convertView.findViewById(R.id.item_task_content);
            holder.end_time_text = (TextView) convertView.findViewById(R.id.item_task_cutdown_time);
            holder.start_time_text = (TextView) convertView.findViewById(R.id.item_task_time);
            holder.state_text = (TextView) convertView.findViewById(R.id.item_task_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> data = list.get(position);
        if (data != null) {
            if (data.get("createName") != null) {
                String name = (String) data.get("createName");
                holder.head_text.setText(name.substring(0));
                holder.head_text.setBackground(MyUtils.getShape(MyConstant.COLOR_GRAY_BG, 20f, 1, MyConstant.COLOR_GRAY));
                holder.name_text.setText(name);
            }
            if (data.get("content") != null) {
                holder.content_text.setText(data.get("content") + "");
            }
            if (data.get("createDate") != null) {
                holder.start_time_text.setText(data.get("createDate") + "");
            }
            if (data.get("endDate") != null) {
                holder.end_time_text.setText(data.get("endDate") + "");
            }
            if (data.get("state") != null) {
                int state = new Double((Double)data.get("state")).intValue();
                if (state == 0){
                    holder.end_time_text.setText("未完成");
                }else if (state == 1){
                    holder.end_time_text.setText("已完成");
                }
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView head_text;
        private TextView name_text;
        private TextView content_text;
        private TextView start_time_text;
        private TextView state_text;
        private TextView end_time_text;
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
