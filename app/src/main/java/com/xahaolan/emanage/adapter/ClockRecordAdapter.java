package com.xahaolan.emanage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xahaolan.emanage.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/3.
 */

public class ClockRecordAdapter extends BaseAdapter {
    private static final String TAG = ClockRecordAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public ClockRecordAdapter(Context context) {
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
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_clock_record_time, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.item_clock_record_view, null);
            holder = new ViewHolder();
            holder.head_image = (ImageView) convertView.findViewById(R.id.item_clock_record_view_head);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_clock_record_view_name);
            holder.time_text = (TextView) convertView.findViewById(R.id.item_clock_record_view_time);
            holder.in_time_text = (TextView) convertView.findViewById(R.id.item_clock_record_view_arrive_time);
            holder.out_time_text = (TextView) convertView.findViewById(R.id.item_clock_record_view_out_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> data = list.get(position);
        if (data != null) {
            if (data.get("createTime") != null) {
                String createTime = (String) data.get("createTime");
                holder.time_text.setText(createTime.substring(0,11));
                holder.in_time_text.setText(createTime.substring(11,16));
            }
            if (data.get("createdate") != null) {
                String createData = (String) data.get("createdate");
                holder.out_time_text.setText(createData.substring(11,16));
            }
        }
        return convertView;
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

    private class ViewHolder {
        private ImageView head_image;
        private TextView name_text;
        private TextView time_text;
        private TextView in_time_text;
        private TextView out_time_text;
    }
}
