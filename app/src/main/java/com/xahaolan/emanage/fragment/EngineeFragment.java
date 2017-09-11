package com.xahaolan.emanage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.adapter.MainItemAdapter;
import com.xahaolan.emanage.ui.checkwork.CheckWorkActivity;
import com.xahaolan.emanage.ui.daily.DailyListActivity;
import com.xahaolan.emanage.ui.more.MoreActivity;
import com.xahaolan.emanage.ui.task.TaskActivity;
import com.xahaolan.emanage.ui.trail.WorkTrailActivity;
import com.xahaolan.emanage.ui.visit.VisitActivity;
import com.xahaolan.emanage.utils.mine.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xahaolan.emanage.base.BaseActivity.setSwipRefresh;

/**
 * Created by helinjie on 2017/9/2.  工程
 */

public class EngineeFragment extends Fragment {
    private static final String TAG = EngineeFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Activity activity;
    private GridView grid_view;
    private MainItemAdapter adapter;

    private List<Map<String,Object>> list = new ArrayList<>();
    private Integer[] imageArr = {R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
    private String[] nameArr = {"考勤","日报","工作轨迹","任务","客户拜访","更多"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enginee, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    public void initView() {
        swipeLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        grid_view = (GridView) getActivity().findViewById(R.id.enginee_gridview);
        setItemClick();
    }
    public void setItemClick(){
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    //考勤
                    case 0:
                        MyUtils.jump(getActivity(),CheckWorkActivity.class,new Bundle(),false,null);
                        break;
                    //日报
                    case 1:
                        MyUtils.jump(getActivity(),DailyListActivity.class,new Bundle(),false,null);
                        break;
                    //工作轨迹
                    case 2:
                        MyUtils.jump(getActivity(),WorkTrailActivity.class,new Bundle(),false,null);
                        break;
                    //任务
                    case 3:
                        MyUtils.jump(getActivity(),TaskActivity.class,new Bundle(),false,null);
                        break;
                    //客户拜访
                    case 4:
                        MyUtils.jump(getActivity(),VisitActivity.class,new Bundle(),false,null);
                        break;
                    //更多
                    case 5:
                        MyUtils.jump(getActivity(),MoreActivity.class,new Bundle(),false,null);
                        break;
                }
            }
        });
    }

    public void initData() {
        adapter = new MainItemAdapter(getActivity());
        grid_view.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterData();
    }

    public void setAdapterData(){
        for (int i = 0;i < 6;i++){
            Map<String,Object> data = new HashMap<>();
            data.put("image",imageArr[i]);
            data.put("name",nameArr[i]);
            list.add(data);
        }
        adapter.resetList(list);
        adapter.notifyDataSetChanged();
    }
}
