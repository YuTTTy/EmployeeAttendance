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
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> data = list.get(position);
        if (data != null) {
            if (data.get("personName") != null){
                holder.name_text.setText(data.get("personName")+"");
            }
            if (data.get("reason") != null) {
                holder.reason_text.setText(data.get("reason") + "");
            }
            if (data.get("type") != null) {
                //0(请假单)，1（外出登记），2（出差申请），3（加班）
                int type = new Double((Double) data.get("type")).intValue();
                if (type == 0) {
                    holder.type_text.setText("请假");
                } else if (type == 1) {
                    holder.type_text.setText("外出");
                } else if (type == 2) {
                    holder.type_text.setText("出差");
                } else if (type == 3) {
                    holder.type_text.setText("加班");
                }
            }
            if (data.get("state") != null) {
                //0(待审批),1(已审批)，2(已拒绝)
                int state = new Double((Double) data.get("state")).intValue();
                if (state == 0) {
                    holder.state_text.setText("待审批");
                    if (data.get("startDate") != null){
                        holder.time_text.setText(data.get("startDate")+"");
                    }
                } else if (state == 1) {
                    holder.state_text.setText("已审批");
                    if (data.get("endDate") != null){
                        holder.time_text.setText(data.get("endDate")+"");
                    }
                } else if (state == 2) {
                    holder.state_text.setText("已拒绝");
                    if (data.get("endDate") != null){
                        holder.time_text.setText(data.get("endDate")+"");
                    }
                }
            }
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
