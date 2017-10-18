package com.xahaolan.emanage.ui.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.BaseActivity;
import com.xahaolan.emanage.base.MyConstant;
import com.xahaolan.emanage.http.FormRequest;
import com.xahaolan.emanage.http.services.CheckWorkServices;
import com.xahaolan.emanage.http.services.TaskService;
import com.xahaolan.emanage.manager.PhotoCamerManager;
import com.xahaolan.emanage.ui.trail.SltDepartmentActivity;
import com.xahaolan.emanage.utils.common.BitmapUtils;
import com.xahaolan.emanage.utils.common.LogUtils;
import com.xahaolan.emanage.utils.common.ToastUtils;
import com.xahaolan.emanage.utils.mine.AppUtils;
import com.xahaolan.emanage.utils.mine.MyUtils;
import com.xahaolan.emanage.view.wheel.dialog.ChangeBirthDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helinjie on 2017/9/9.
 */

public class CreateTaskActivity extends BaseActivity {
    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeLayout;
    private PhotoCamerManager photoCamerUtil;
    private Intent intent;

    private EditText content_et;
    private LinearLayout execute_layout;
    private TextView execute_text;
    private LinearLayout time_layout;
    private TextView time_text;
    private ImageView photo_icon;
    private LinearLayout photos_layout;
    private TextView btn_text;

    private int createId;//   发布人id
     private String createName;//  发布人姓名
     private int executorId = 0;//  执行人id
     private String content;//     任务内容
     private String endDate;//     截止日期
    private String[] sourceFile;
    private List<String> sourceList;
    private Map<String, Object> paramsMap; //非资源文件params
    private List<Map<String,Object>> fileList;//资源文件params
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setcontentLayout(R.layout.activity_create_task);
    }

    @Override
    public void setTitleAttribute() {
        setTitle(0, R.color.titleBg, R.drawable.ico_left_white, "", R.color.baseTextMain, "创建任务", R.color.baseTextMain, "", R.color.baseTextMain, 0);
    }

    @Override
    public void initView() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setEnabled(false); //禁止下拉刷新
        setSwipRefresh(swipeLayout, null);
        content_et = (EditText) findViewById(R.id.create_task_content);
        execute_layout = (LinearLayout) findViewById(R.id.create_task_execute_layout);
        execute_text = (TextView) findViewById(R.id.create_task_execute_name);
        time_layout = (LinearLayout) findViewById(R.id.create_task_time_layout);
        time_text = (TextView) findViewById(R.id.create_task_time_text);
        photo_icon = (ImageView) findViewById(R.id.create_task_photo);
        photos_layout = (LinearLayout) findViewById(R.id.create_task_photos_layout);
        btn_text = (TextView) findViewById(R.id.create_task_btn);
        setClick();

    }

    public void setClick() {
        execute_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("sltType",2);
                MyUtils.jump(context, SltDepartmentActivity.class, bundle, false, null);
            }
        });
        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeBirthDialog timeDialog = new ChangeBirthDialog(context);
                Calendar calendar = Calendar.getInstance();
                timeDialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                timeDialog.show();
                timeDialog.setBirthdayListener(new ChangeBirthDialog.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day) {
                        String sltTimeStr = year + "-" + MyUtils.suppleSingular(Integer.parseInt(month)) + "-" + day;
                        time_text.setText(sltTimeStr);
                    }
                });
            }
        });
        photo_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoCamerUtil.takePhotoCamer(1, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (msg.what == MyConstant.HANDLER_SUCCESS) {
                            String imagePath = (String) msg.obj;
                            LogUtils.e(TAG,"拍照图片路径 ："+imagePath);
                            try {
                                sourceList.add(imagePath);
                                photos_layout.removeAllViews();
                                for (int i = 0; i < sourceList.size(); i++) {
                                    photos_layout.addView(addPhotoItemView(sourceList.get(i)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCreateTask();
            }
        });
    }

    @Override
    public void initData() {
        photoCamerUtil = new PhotoCamerManager((Activity) context, context);
        sourceList = new ArrayList<>();
        paramsMap = new HashMap<>();
        fileList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        executorId = intent.getIntExtra("employeeId", 0);
        String executorName = intent.getStringExtra("employeeName");
        if (executorName != null && !executorName.equals("")){
            execute_text.setText(executorName);
        }
    }

    public void requestCreateTask() {
        getParams();
        if (content == null || content.equals("")){
            ToastUtils.showShort(context,"请输入任务内容");
            return;
        }
        if (endDate == null || endDate.equals("")){
            ToastUtils.showShort(context,"请设置截止时间");
            return;
        }

        paramsMap.put("createId", createId);
        paramsMap.put("createName", createName);
        paramsMap.put("executorId", executorId);
        paramsMap.put("content", content);
        paramsMap.put("content", content);
        paramsMap.put("endDate", endDate);
        Map<String,Object> fileMap;
        if (sourceList != null && sourceList.size() > 0){
            for (String sourcePath : sourceList){
                fileMap = new HashMap<>();
                fileMap.put("sourceFile",sourcePath);
                fileList.add(fileMap);
            }
        }
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        LogUtils.e(TAG,"---------------- 创建任务request ----------------");
        LogUtils.e(TAG,"创建任务 request url : "+MyConstant.BASE_URL + "/app/task!add.action");
        new FormRequest(context,MyConstant.BASE_URL + "/app/task!add.action",paramsMap,fileList,new Handler(){
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
                    if (errMsg.equals("session过期")) {
                        BaseActivity.loginOut(context);
                    }
                } else if (msg.what == MyConstant.REQUEST_ERROR) {
                    String errMsg = (String) msg.obj;
                    ToastUtils.showShort(context, errMsg);
                }
            }
        });

//        preRequest();
//        if (swipeLayout != null) {
//            swipeLayout.setRefreshing(true);
//        }
//        new TaskService(context).addTaskAddService( createId, createName, executorId,
//         content, endDate, new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (swipeLayout.isRefreshing()) {  //3.检查是否处于刷新状态
//                    swipeLayout.setRefreshing(false);  //4.显示或隐藏刷新进度条
//                }
//                if (msg.what == MyConstant.REQUEST_SUCCESS) {
//                    String response = (String) msg.obj;
//                    finish();
//                } else if (msg.what == MyConstant.REQUEST_FIELD) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                    if (errMsg.equals("session过期")){
//                        BaseActivity.loginOut(context);
//                    }
//                } else if (msg.what == MyConstant.REQUEST_ERROR) {
//                    String errMsg = (String) msg.obj;
//                    ToastUtils.showShort(context, errMsg);
//                }
//            }
//        });
    }
    public void getParams(){
        createId = AppUtils.getPersonId(context);
        createName = AppUtils.getPersonName(context);
        content = content_et.getText().toString();
        endDate = time_text.getText().toString();
    }
//    /**
//     *  资源文件
//     */
//    public void preRequest() {
//        //        if (sourceList == null || sourceList.size() <=0){
////            ToastUtils.showShort(context,"请上传图片");
////            return;
////        }
//        sourceFile = new String[sourceList.size()];
//        for (int i = 0; i < sourceList.size(); i++) {
//            try {
//                String imageStr = BitmapUtils.readStream(sourceList.get(i));
//                LogUtils.e(TAG, "imageStr : " + imageStr);
//                sourceFile[i] = imageStr;
//                LogUtils.e(TAG, "sourceFile : " + sourceFile.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    /**
     *             添加照片
     *
     * @param imageUrl
     * @return
     */
    public View addPhotoItemView(String imageUrl) {
        View photo_view = LayoutInflater.from(context).inflate(R.layout.item_view_image, null);
        ImageView photo_image = (ImageView) photo_view.findViewById(R.id.item_view_photo_image);
        photo_image.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(imageUrl).into(photo_image);
        return photo_view;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyUtils.hideKeyboard((Activity) context);
        photoCamerUtil.activityResult(requestCode, resultCode, data);
    }
}
