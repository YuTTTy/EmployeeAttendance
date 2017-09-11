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

public class EmployeeAdapter extends BaseAdapter {
    private static final String TAG = EmployeeAdapter.class.getSimpleName();
    private Context context;
    private List<Map<String, Object>> list;

    public EmployeeAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_employee, null);
            holder.head_image = (ImageView) convertView.findViewById(R.id.item_employee_head);
            holder.name_text = (TextView) convertView.findViewById(R.id.item_employee_name);
            holder.department_text = (TextView) convertView.findViewById(R.id.item_employee_department);
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
    }
    public int getPositionForSelection(int selection) {
        List<Map<String, Object>> contactsList = new ArrayList<>();
        for (int i = 0; i < contactsList.size(); i++) {
            String Fpinyin = (String) contactsList.get(i).get("FirstPinYin");
            char first = Fpinyin.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;
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
