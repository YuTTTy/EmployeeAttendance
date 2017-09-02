package com.xahaolan.emanage.utils.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 正则工具类
 * @author ChenNan
 */
public class RegExpUtil {
	
	/**
	 * 验证手机号是否合法
	 * 
	 * @param num
	 *            手机号
	 * @return 返回true表示合法
	 */
	public static boolean isPhoneNumber(String num) {
		if (null != num) {
			Pattern p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
			Matcher m = p.matcher(num);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证用户名（数字字母混合）
	 * @param num用户名
	 * @return
	 */
	public static boolean isUsername(String num) {
		if (null != num) {
			Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{2,20}$"); // 用户名--不能全是数字，不能全是字母，
			Matcher m = p.matcher(num);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证密码（大小写字母数字组合6位）
	 * @param num 密码
	 * @return
	 */
	public static boolean isPassword(String num) {
		if (null != num) {
			Pattern p = Pattern.compile("^[a-zA-Z0-9_]{0,6}$");
			Matcher m = p.matcher(num);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 验证昵称
	 * @param num用户名
	 * @return
	 */
	public static boolean isNickname(String num) {
		if (null != num) {
			Pattern p = Pattern.compile("^[\u4e00-\u9fa50]{1,10}$|^[0-9a-zA-Z_]{1,20}$"); // 昵称不得超过10个汉字或20个英文字符, 支持英文、数字、下划线
			Matcher m = p.matcher(num);
			return m.matches();
		}
		return false;
	}

	public static boolean isIDumber(String num){
		if (null != num) {
			Pattern p = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
			Matcher m = p.matcher(num);
			return m.matches();
		}
		return false;
	}

}
