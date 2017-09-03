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

public class MainItemAdapter extends BaseAdapter {
    private static final String TAG = MainItemAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public MainItemAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_item, null);
            holder.image_view = (ImageView) convertView.findViewById(R.id.item_main_item_image);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_main_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> data = list.get(position);
        if (data != null) {
            if (data.get("image") != null) {
                int imageRes = (int) data.get("image");
                holder.image_view.setImageResource(imageRes);
            }
            if (data.get("name") != null) {
                holder.name_text.setText(data.get("name") + "");
            }
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView image_view;
        private TextView name_text;
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
