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
 * Created by helinjie on 2017/9/3.
 */

public class CheckApplyAdapter extends BaseAdapter {
    private static final String TAG = CheckApplyAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public CheckApplyAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_check_apply, null);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_check_apply_name_text);
            holder.time_text = (TextView) convertView.findViewById(R.id.item_check_apply_time_text);
            holder.reason_text = (TextView) convertView.findViewById(R.id.item_check_apply_reason_text);
            holder.type_text = (TextView) convertView.findViewById(R.id.item_check_apply_type_text);
            holder.state_text = (TextView) convertView.findViewById(R.id.item_check_apply_state_text);
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
        private TextView name_text;
        private TextView time_text;
        private TextView reason_text;
        private TextView type_text;
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
