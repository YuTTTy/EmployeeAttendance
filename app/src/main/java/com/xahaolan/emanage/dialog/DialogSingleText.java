package com.xahaolan.emanage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.base.MyConstant;

import java.util.List;

/**
 * Created by helinjie on 2017/9/3.
 */

public class DialogSingleText extends Dialog {
    private static final String TAG = DialogSingleText.class.getSimpleName();
    private Context context;
    private Handler handler;
    private LinearLayout items_layout;
    private String[] strArr;

    public DialogSingleText(Context context,String[] strArr, Handler handler) {
        super(context);
        this.context = context;
        this.strArr = strArr;
        this.handler = handler;
        setContentView(R.layout.dialog_single_text);

        Window window = getWindow();
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = window.getAttributes();
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        initView();
        initData();
    }

    public void initView() {
        items_layout = (LinearLayout) findViewById(R.id.dialog_single_text_layout);
    }

    public void initData() {
        for (int i = 0; i < strArr.length; i++) {
            items_layout.addView(addItemView(strArr[i],i));
        }
    }

    public View addItemView(String nameStr, final int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_single_text, null);
        TextView name_text = (TextView) itemView.findViewById(R.id.item_single_text_name);
        name_text.setText(nameStr);
        name_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.obj = position;
                message.what = MyConstant.HANDLER_SUCCESS;
                handler.sendMessage(message);
                dismiss();
            }
        });
        return itemView;
    }
}
