package com.xahaolan.emanage.utils.mine;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ListView;

import com.xahaolan.emanage.base.MyConstant;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by aiodiy on 2016/12/15.
 */
public class MyUtils {
    private static final String TAG = MyUtils.class.getSimpleName();
    private Context context;

    /**
     * 页面跳转
     *
     * @param context
     * @param cls
     * @param bundle
     * @param isCallBack
     * @param requestCode
     */
    public static void jump(Context context, Class<?> cls, Bundle bundle, boolean isCallBack, Integer requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(bundle);
        if (isCallBack) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 倒角相同
     *
     * @param colorStr    背景颜色
     * @param cornerFlot  倒角值
     * @param strokeWidth 边线宽度
     * @param strokeColor 边线颜色
     * @return
     */
    public static GradientDrawable getShape(String colorStr, float cornerFlot, int strokeWidth, String strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(colorStr));
        drawable.setCornerRadius(cornerFlot);
        drawable.setStroke(strokeWidth, Color.parseColor(strokeColor));
        return drawable;
    }

    /**
     * 倒角不同
     *
     * @param colorStr          背景色
     * @param cornerLeftTop     左上角
     * @param cornerLeftBottom  左下角
     * @param cornerRightTop    右上
     * @param cornerRightBottom 右下
     * @param strokeWidth       边线宽度
     * @param strokeColor       变现颜色
     * @return
     */
    public static GradientDrawable getShape(String colorStr, float cornerLeftTop, float cornerRightTop, float cornerRightBottom, float cornerLeftBottom, int strokeWidth, String strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(colorStr));
        float[] corners = {cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop, cornerRightBottom, cornerRightBottom, cornerLeftBottom, cornerLeftBottom,};
        drawable.setCornerRadii(corners);
        drawable.setStroke(strokeWidth, Color.parseColor(strokeColor));
        return drawable;
    }

    /**
     * 延时自动弹出软键盘
     *
     * @param context
     */
    public static void autoKeyboardTime(final Context context) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //method one
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//        //method two
//        inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.showSoftInput(editText, 0);
            }
        }, 1000);
    }

    /**
     * 自动弹出软键盘
     *
     * @param context
     */
    public static void autoKeyboard(final Context context) {
        //method one
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//        //method two
//        inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        View inputView = activity.getWindow().peekDecorView();
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputView != null) {
            inputManager.hideSoftInputFromWindow(inputView.getWindowToken(), 0);
        }
    }

    /**
     * 关闭栈内所有Activity
     *
     * @param context
     */
    public static void closeAllActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        manager.restartPackage(context.getPackageName());
    }
    /**
     * 自动补全单数
     *
     * @return
     */
    public static String suppleSingular(int number) {
        String suppleStr = "";
        if (number <= 9) {
            suppleStr = "0" + number;
        } else {
            suppleStr = number + "";
        }
        return suppleStr;
    }
    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNo
     */
    public static void takePhone(Context context, String phoneNo) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(phoneIntent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phones
     */
    public static void takeMsg(Context context, List<String> phones) {
        StringBuffer buffer = new StringBuffer("smsto:");
        for (String phone : phones) {
            buffer.append(phone);
            buffer.append(";");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(buffer.toString()));
        context.startActivity(intent);
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][3458]\\d{9}";//"[1]"代表第1位为数字1，"[3458]"代表第二位可以为3、4、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 验证密码格式
     * <p>
     * 1，不能全部是数字
     * 2，不能全部是字母
     * 3，必须是数字或字母
     * 4. 长度要在8-16位之间
     */
    public static boolean isPassword(String password) {
    /*
    ^ 匹配一行的开头位置
    (?![0-9]+$) 预测该位置后面不全是数字
    (?![a-zA-Z]+$) 预测该位置后面不全是字母
    [0-9A-Za-z] {8,16} 由8-16位数字或这字母组成
    $ 匹配行结尾位置
    注：(?!xxxx) 是正则表达式的负向零宽断言一种形式，标识预该位置后不是xxxx字符。
    */
        String num = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        if (TextUtils.isEmpty(password)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return password.matches(num);
        }
    }

    /**
     * 通讯录手机号码处理
     *
     * @param phoneNo
     * @return
     */
    public static String dealPhoneNumber(String phoneNo) {
        phoneNo = phoneNo.replace(" ", "");
        if (phoneNo.substring(0, 3).equals("+86")) {
            phoneNo = phoneNo.substring(3, 14);
        }
        return phoneNo;
    }


    /**
     * 动态设置list view的高度
     *
     * @param listView
     */
    public static void getListViewHeight(ListView listView, Adapter adapter) {
        if (listView == null) {
            return;
        }
        adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View itemView = adapter.getView(i, null, listView);
            listView.measure(0, 0);
            totalHeight += itemView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (adapter.getCount() - 1);
        listView.setLayoutParams(params);
    }

    /**
     * 获取栈顶的activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * 代码生成选择器
     *
     * @param context   当前上下文
     * @param idNormal  默认图片id
     * @param idPressed 触摸时图片id
     * @param idFocused 获得焦点时图片id
     * @param idUnable  没有选中时图片id
     * @return
     */
    public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused, int idUnable) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }


    /**
     * 控件选择器
     *
     * @param context   当前上下文
     * @param idNormal  默认图片id
     * @param idPressed 按压时图片id
     * @return
     */
    public static StateListDrawable setSelector(Context context, int idNormal, int idPressed) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        bg.addState(new int[]{}, normal);
        return bg;
    }

}
