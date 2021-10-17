package com.karry.tool.Helper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;
import java.util.Objects;

public class StorageHelper {
	public static String getTotalCacheSize(Context context) {
		long size = getFolderSize(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			size += getFolderSize(context.getExternalCacheDir());
		}
		return getFormatSize(size);
	}

	/**
	 * 清除所有缓存
	 */
	public static boolean clearAllCache(Context context) {
		boolean result, result1 = false;
		result = deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			result1 = deleteDir(context.getExternalCacheDir());
		}
		return result && result1;
	}

	/**
	 * 删除某个文件
	 */
	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			return dir.delete();
		}
		if (dir != null) {
			return dir.delete();
		} else {
			return false;
		}
	}

	/**
	 * 获取文件
	 */
	private static long getFolderSize(File file) {
		long size = 0;
		if (file != null) {
			File[] fileList = file.listFiles();
			if (fileList != null && fileList.length > 0) {
				for (File value : fileList) {
					// 如果下面还有文件
					if (value.isDirectory()) {
						size = size + getFolderSize(value);
					} else {
						size = size + value.length();
					}
				}
			}
		}
		return size;
	}

	/**
	 * 格式化单位
	 */
	public static String getFormatSize(long size) {
		double kiloByte = (double) size / 1024;
		if (kiloByte < 1) {
			return size / 1024 + "K";
		}
		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
		}
		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
		}
		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
	}
}
