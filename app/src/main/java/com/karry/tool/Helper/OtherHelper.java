package com.karry.tool.Helper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherHelper {
	public static boolean isPhoneNumber(String num) {
		String regex = "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}";
		Pattern pat = Pattern.compile(regex.trim());  //对比
		Matcher mat = pat.matcher(num.trim());
		return mat.matches();  //判断是否匹配
	}

	public static void showMyDialog(Context context, String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("复制", (dialogInterface, i) -> {
			ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setPrimaryClip(ClipData.newPlainText(null, message));
			ToastHelper.showToast(context, "复制成功");
		});
		builder.setNegativeButton("取消", null);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public static String getCurrentDate(String pattern) {
		return new SimpleDateFormat(pattern, Locale.SIMPLIFIED_CHINESE).format(Calendar.getInstance().getTime());
	}

	/**
	 * BASE64解密
	 */
	public static byte[] decryptBase64(String data) {
		return android.util.Base64.decode(data, android.util.Base64.DEFAULT);
	}
}
