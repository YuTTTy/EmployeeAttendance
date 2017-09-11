package com.xahaolan.emanage.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xahaolan.emanage.R;
import com.xahaolan.emanage.utils.common.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/10/18.   版本更新
 */
public class UpdateManager {
    private static final String TAG = UpdateManager.class.getSimpleName();

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private Context context;
    private Dialog downloadDialog; // 下载对话框
    private ProgressBar mProgress; // 进度条
    private TextView mProgressText; // 显示下载数字
    private int progress; // 进度值
    private Thread downloadThread; // 下载线程
    private boolean interceptFlag; // 终止标记
    private String apkUrl = "";// 更新apk的地址
    private String savePath = ""; // 下载包保存路径
    private String apkFilePath = ""; // apk完整路径
    private String tmpFilePath = ""; // 临时下载文件路径
    private String apkFileSize = ""; // //下载文件大小
    private String tmpFileSize = ""; // 已下载大小

    /**
     *
     * @param context
     * @param apkUrl
     */
    public UpdateManager(Context context, String apkUrl) {
        this.context = context;
        this.apkUrl = apkUrl;
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize + "/" + apkFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    Toast.makeText(context,"无法下载安装文件，请检查是否有SD卡",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // 显示下载对话框
    public void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("正在下载新版本");

        downloadDialog = builder.create();
        downloadDialog.show();
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);

        Window window = downloadDialog.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.update_progress);
        mProgress = (ProgressBar) window.findViewById(R.id.update_progress);
        mProgressText = (TextView) window.findViewById(R.id.tv_Progresstext);

        // 下载文件
        downloadApk();
    }

    private void downloadApk() {
        downloadThread = new Thread(mdownApkRunnable);
        downloadThread.start(); // 开始下载
    }

    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            try {

                String apkName = "yq" + ".apk";
                String tmpApk = "yq" + ".tmp";
                // 判断是否挂载了sd卡
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/update/";
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    apkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                }

                // 没有挂载sd卡，无法下载
                if (apkFilePath == null || apkFilePath == "") {
                    LogUtils.e(TAG,"没有挂载sd卡，无法下载");
                    mhandler.sendEmptyMessage(DOWN_NOSDCARD);
                    return;
                }

                File ApkFile = new File(apkFilePath);

                // 输出临时文件
                File tempFile = new File(tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tempFile);
                URL url = new URL(apkUrl);
                // 定义url链接对象
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect(); // 建立链接

                int length = conn.getContentLength(); // 得到链接内容的
                InputStream is = conn.getInputStream();

                DecimalFormat df = new DecimalFormat("0.00");
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
                int count = 0;
                byte buf[] = new byte[1024];
                // 循环下载
                do {
                    int numread = is.read(buf);
                    count += numread;
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    progress = (int) (((float) count / length) * 100);
                    mhandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        if (tempFile.renameTo(ApkFile)) {
                            // 下载完成
                            mhandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);
                fos.close();
                is.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    // 安装apk
    private void installApk() {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        // 如果存在，启动安装流程
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
        context.startActivity(i);
    }
}
