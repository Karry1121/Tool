package com.karry.tool.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class NetworkHelper {
	public static String poetryToken = null;
	private static OkHttpClient okHttpClient;
	private static Cache cache;

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断字符串是否为URL
	 *
	 * @param urls 需要判断的String类型url
	 * @return true:是URL；false:不是URL
	 */
	public static boolean isHttpUrl(String urls) {
		//设置正则表达式
		String regex =
				"(((https|http)?://)?([a-z0-9]+[.])|(www.))"
						+ "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
		Pattern pat = Pattern.compile(regex.trim());  //对比
		Matcher mat = pat.matcher(urls.trim());
		return mat.matches();  //判断是否匹配
	}

	public static OkHttpClient getOkHttpClient(Context context) {
		if (cache == null) {
			cache = new Cache(context.getCacheDir(), 10240 * 1024);
			if (okHttpClient == null) {
				okHttpClient = new OkHttpClient.Builder()
						.cache(cache)
						.connectTimeout(15, TimeUnit.SECONDS)
						.build();
			}
		}
		return okHttpClient;
	}
}
