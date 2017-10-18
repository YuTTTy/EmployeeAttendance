package com.xahaolan.emanage.utils.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aiodiy on 2017/1/11.
 */

public class BitmapUtils {
    /**
     * decodeStream解码图片为bitmap
     * <p>
     * decodeStream最大的秘密在于其直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，
     * 从而节省了java层的空间
     *
     * @param context
     * @param resId      资源图片Id
     * @param ImgName    assest目录下的图片名
     * @param filePath   图片在文件夹下的路径
     * @param fileUri    图片Uri
     * @param clazz
     * @param sampleSize 压缩比例
     * @return
     */
    public static Bitmap decodeStreamToBitmap(Context context, int resId, String ImgName, String filePath, Uri fileUri, Activity clazz, int sampleSize) {
//        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTempStorage = new byte[100 * 1024]; //为位图设置100K的缓存
        /*
        * //ALPHA_8：每个像素占用1byte内存（8位）
//ARGB_4444:每个像素占用2byte内存（16位）
//ARGB_8888:每个像素占用4byte内存（32位）
//RGB_565:每个像素占用2byte内存（16位）
//Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。
也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes //=30M。
        * */
        options.inPreferredConfig = Bitmap.Config.RGB_565; //设置位图颜色显示优化方式
        options.inPurgeable = true; //设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        options.inSampleSize = sampleSize; //设置位图缩放比列
        options.inInputShareable = true; // 设置解码位图的尺寸信息(设置是否深拷贝，与inPurgeable结合使用，inPurgeable为false时，该参数无意义。)
        InputStream inStream = null;
        try {
            /*1.解码资源文件*/
            if (resId != 0) {
                inStream = context.getResources().openRawResource(resId);
            }
            /*2.加载assest目录下的图片*/
            if (ImgName != null && !ImgName.equals("")) {
                AssetManager assetMg = context.getAssets();
                inStream = assetMg.open(ImgName); //ImgName图片名称
            }
            /*3.加载位图、及SD卡目录下的图片*/
//            String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/device.phg";
            if (filePath != null && !filePath.equals("")) {
                inStream = new FileInputStream(filePath);
            }
            /*4.解码Uri*/
            if (fileUri != null) {
                inStream = clazz.getContentResolver().openInputStream(fileUri);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(inStream, null, options);
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image) {
        Log.e("----------", image.getRowBytes() * image.getHeight() + "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i("------------", w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        int height = image.getHeight();
        int width = image.getWidth();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm);// 把ByteArrayInputStream数据生成图片

        Log.e("----------", bitmap.getRowBytes() * bitmap.getHeight() + "");
        return bitmap;
    }

    /**
     * 获得所有图片的路径
     */
    private void getAllImagePath(Context context) {
        List<String> paths = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        //遍历相册
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            //将图片路径添加到集合
            paths.add(path);
        }
        cursor.close();
    }

    /**
     * Bitmap转化为drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable 转 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * view  to  bitmap
     *
     * @param view
     * @param bitmapWidth
     * @param bitmapHeight
     * @return
     */
    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));

        return bitmap;
    }

    /**
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 获取资源文件 r.drawable中的图片转换为drawable
     *
     * @param context
     * @param resInt
     * @return
     */
    public static Drawable res2Drawable(Context context, int resInt) {
        Resources resources = context.getResources();
        Drawable drawable = resources.getDrawable(resInt);
        return drawable;
    }

    /**
     * 获取资源文件 r.drawable中的图片转换为bitmap
     *
     * @param context
     * @param resInt
     * @return
     */
    public static Bitmap res2Bitmap(Context context, int resInt) {
//        Resources r = context.getResources();
//        InputStream inputStream = r.openRawResource(resInt);
//        BitmapDrawable  bmpDraw = new BitmapDrawable(inputStream);
//        Bitmap bitmap = bmpDraw.getBitmap();

//        Bitmap bitmap=BitmapFactory.decodeResource(r, resInt);
//        Bitmap bitmap = Bitmap.createBitmap( 300, 300, Bitmap.Config.ARGB_8888 );

        InputStream inputStream = context.getResources().openRawResource(resInt);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    /**
     * 照片转byte二进制
     *
     * @param imagepath 需要转byte的照片路径
     * @return 已经转成的byte
     * @throws Exception
     */
    public static String readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return byte2hex(outStream.toByteArray());
    }

    // 二进制转字符串
    public static String byte2hex(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        String tmp = "";
        for (int i = 0; i < b.length; i++) {
            tmp = Integer.toHexString(b[i] & 0XFF);
            if (tmp.length() == 1){
                sb.append("0" + tmp);
            }else{
                sb.append(tmp);
            }

        }
//        LogUtils.e(BitmapUtils.class.getSimpleName(), "照片转byte二进制字符串 : " + sb.toString());
        return sb.toString();
    }

    public static String getImageStr(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] datas = baos.toByteArray();
        StringBuffer buffer = new StringBuffer(String.valueOf(datas));
        LogUtils.e(BitmapUtils.class.getSimpleName(), "照片转byte二进制 : " + buffer.toString());
        return buffer.toString();
    }

}
