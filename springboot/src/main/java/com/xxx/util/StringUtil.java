package com.xxx.util;

import java.util.UUID;

public class StringUtil {
	
	/**
	 * 返回对象到字符串形式
	 * */
	public static String toString(Object obj){
		if(obj==null){
			return "";
		} else {
			return obj.toString();
		}
	}
	
	/**
	 * 返回数组到字符串形式
	 * */
	public static String toString(String[] arr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
			if (i < arr.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 返回32随机UUID码
	 * */
	public static String uuid(){
		return UUID.randomUUID().toString().replace("-", "");
	}

}
