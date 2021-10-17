package com.karry.tool.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karry.tool.R;

public class ToastHelper {
	/**
	 * 显示Toast
	 *
	 * @param context 需要显示的Activity
	 * @param message 显示的字符串
	 */
	public static void showToast(Context context, String message) {
		@SuppressLint("InflateParams")
		View toastView = LayoutInflater.from(context).inflate(R.layout.toast_layout_text, null);
		TextView textView = toastView.findViewById(R.id.tv_message);
		textView.setText(message);
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(toastView);
		toast.show();
	}
}
