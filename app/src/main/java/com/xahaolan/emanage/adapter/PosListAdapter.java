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
 * Created by helinjie on 2017/9/10.
 */

public class PosListAdapter extends BaseAdapter {
    private static final String TAG = PosListAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public PosListAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pos_list, null);
            holder.head_image = (ImageView) convertView.findViewById(R.id.item_pos_list_head);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_pos_list_name);
            holder.department_text = (TextView) convertView.findViewById(R.id.item_pos_list_department);
            holder.position_text = (TextView) convertView.findViewById(R.id.item_pos_list_position);
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
        private ImageView head_image;
        private TextView name_text;
        private TextView department_text;
        private TextView position_text;
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
