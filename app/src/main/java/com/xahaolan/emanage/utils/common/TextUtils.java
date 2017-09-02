package com.xahaolan.emanage.utils.common;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;

/**
 * Created by aiodiy on 2017/8/23.
 */

public class TextUtils {
    /**
     * 设置字体不同color、size
     *
     * @param context
     * @param textStr
     * @param frontColor
     * @param backColor
     * @param startSize
     * @param middleSize
     * @param endSize
     * @return
     */
    public static SpannableString setDifferentSizeColor(Context context, String textStr, int frontColor, int backColor, int startSize, int middleSize, int endSize) {
        SpannableString spannable = new SpannableString(textStr);
        spannable.setSpan(new TextAppearanceSpan(context, frontColor), startSize, middleSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextAppearanceSpan(context, backColor), middleSize, endSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
    /**
     * 设置字体下划线
     *
     * @param context
     * @param textStr
     * @return
     */
    public static SpannableString setUnderLine(Context context, String textStr, int textColor) {
        SpannableString spannable = new SpannableString(textStr);
        spannable.setSpan(new TextAppearanceSpan(context, textColor), 0, textStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), 0, textStr.length(), 0);
        return spannable;
    }
    /**
     * 设置字体不同color、size、下划线
     *
     * @param context
     * @param textStr
     * @param frontColor
     * @param backColor
     * @param startSize
     * @param middleSize
     * @param endSize
     * @return
     */
    public static SpannableString setDiffSizeColorUnderLine(Context context, String textStr, int frontColor, int backColor, int startSize, int middleSize, int endSize) {
        SpannableString spannable = new SpannableString(textStr);
        spannable.setSpan(new TextAppearanceSpan(context, frontColor), startSize, middleSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new TextAppearanceSpan(context, backColor), middleSize, endSize, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), middleSize, endSize, 0);
        return spannable;
    }
}
