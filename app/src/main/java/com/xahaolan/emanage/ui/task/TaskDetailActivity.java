package com.xahaolan.emanage.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.services.TaskService;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by               task detail
 */

public class TaskDetailActivity extends BaseActivity {
    private static final String TAG = TaskDetailActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;

    private TextView send_text;
    private TextView execute_text;
    private TextView time_text;
    private TextView content_text;
    private LinearLayout items_layout;
    private TextView btn_text;

    private int taskId;
    private Map<String,Object> detailData;
    private String urlStr = "";
    private String[] urlArr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_task_detail);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "任务详情", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        send_text = (TextView) findViewById(R.id.task_detail_send_name);
        execute_text = (TextView) findViewById(R.id.task_detail_execute_name);
        time_text = (TextView) findViewById(R.id.task_detail_time_name);
        content_text = (TextView) findViewById(R.id.task_detail_content);
        items_layout = (LinearLayout) findViewById(R.id.task_detail_item_layout);
        btn_text = (TextView) findViewById(R.id.task_detail_btn);

        setClick();

    }

    public void setClick() {
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFinishTask();
            }
        });
    }

    @Override
    public void initData() {
        intent = getIntent();
        taskId = intent.getIntExtra("taskId",0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTaskDetail();
    }

    /**
     *      task detail
     */
    public void requestTaskDetail() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).taskDetailService(taskId, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    detailData = (Map<String, Object>) msg.obj;
                    if (detailData != null){
                        setViewData();
                    }

                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }

    /**
     *       finish task
     */
    public void requestFinishTask() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        new TaskService(context).finishTaskService(taskId, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
                }
                if (msg.what == MyConstant.REQUEST_SUCCESS) {
                    finish();
                } else if (msg.what == MyConstant.REQUEST_FIELD) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });
    }
    public void setViewData(){
        if (detailData.get("createName") != null){
            send_text.setText(detailData.get("createName")+"");
        }
        if (detailData.get("executorName") != null){
            execute_text.setText(detailData.get("executorName")+"");
        }
        if (detailData.get("endDate") != null){
            time_text.setText(detailData.get("endDate")+"");
        }
        if (detailData.get("content") != null){
            content_text.setText(detailData.get("content")+"");
        }
        if(detailData.get("urls") != null){
            urlStr = (String) detailData.get("urls");
            setImageViews();
        }
    }
    /**
     *
     */
    public void setImageViews(){
        int numUrl = 0;
        urlArr = new String[appearNumber(urlStr,",")+1];
        while (urlStr != null && !urlStr.equals("")) {
            if (urlStr.contains(",")){
                String subUrl = StringUtils.substringBefore(urlStr,","); //截取第一个路径
                urlStr = urlStr.substring(subUrl.length()+1,urlStr.length());//删除第一个路径
                urlArr[numUrl] = subUrl; //路径添加
                numUrl++;
            }else {
                urlArr[numUrl] = urlStr; //路径添加
                urlStr = "";
            }
        }
        LogUtils.e(TAG,"图片加载路径 ：" + urlArr.toString());

        getItemsData();
    }
    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     *
     */
    public void getItemsData() {
        for (int i = 0; i < urlArr.length; i++) {
            items_layout.addView(getPhotoItemView(urlArr[i]));
        }
    }

    /**
     *
     * @param imageUrl
     * @return
     */
    public View getPhotoItemView(String imageUrl) {
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image, null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        Glide.with(context).load(MyConstant.BASE_URL+imageUrl).into(photo_image);
        return photo_view;
    }
}
