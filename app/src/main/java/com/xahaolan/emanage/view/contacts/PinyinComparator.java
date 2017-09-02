package com.xahaolan.emanage.view.contacts;

import java.util.Comparator;
import java.util.Map;

public class PinyinComparator implements Comparator<Map<String,Object>> {

	@Override
	public int compare(Map<String,Object> lhs, Map<String,Object> rhs) {
		// TODO Auto-generated method stub
		return sort(lhs, rhs);
	}

	private int sort(Map<String,Object> lhs, Map<String,Object> rhs) {
		// 获取ascii值
		int lhs_ascii = ((String)lhs.get("FirstPinYin")).toUpperCase().charAt(0);
		int rhs_ascii = ((String)lhs.get("FirstPinYin")).toUpperCase().charAt(0);
		// 判断若不是字母，则排在字母之后
		if (lhs_ascii < 65 || lhs_ascii > 90)
			return 1;
		else if (rhs_ascii < 65 || rhs_ascii > 90)
			return -1;
		else
			return ((String)lhs.get("PinYin")).compareTo(((String)lhs.get("PinYin")));
	}

}
