package com.example.demo.util;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

/**
 * 主键生成器
 * 
 */
public class KeyGenerator {

	public static String[] chars = new String[] { 
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
			"a", "b", "c", "d","e", "f", "g", "h", "i", "j", "k", "l", "m", 
			"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", 
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	public static String single;// 序列生成标志，number表示生成为数字，其他表示使用uuid生成
	public static long point = 0;// 毫秒中的增量
	public static String node;// 机器节点标识
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
	private static Random random = new Random();

	/**
	 * 获取随机字符串用于微信配置
	 *
	 * @return
	 */
	public static String getSignature() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			sb.append(chars[random.nextInt(chars.length - 1)] + System.currentTimeMillis());
		}
		return sb.toString();
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}

	/**
	 * 生成16位uuid <功能详细描述>
	 * 
	 * @return
	 */
	public static String generateShortUuid(String single,String node) {
		StringBuffer shortBuffer = new StringBuffer();
		if (null == node || "".equals(node)) {
			System.err.println("机器节点错误");
		}
		if ("number".equals(single)) {// 生成数字格式
			shortBuffer.append(node).append(getSequenceNumber());
			return shortBuffer.toString();
		}
		String uuid = getUUID();
		for (int i = 0; i < 16; i++) {
			String str = uuid.substring(i * 2, i * 2 + 2);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
	}

	/**
	 * 采用同步方法,获取毫秒时间+增量值
	 * 
	 * @return
	 */
	public synchronized static String getSequenceNumber() {
		point++;
		if (point == 100) {
			point = 1;
		}
		return sdf.format(System.currentTimeMillis()) + String.valueOf(point);
	}



}
