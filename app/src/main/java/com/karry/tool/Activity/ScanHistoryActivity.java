package com.karry.tool.Activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Adapter.ScanHistoryAdapter;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class ScanHistoryActivity extends AppCompatActivity {
	MaterialToolbar toolbar;
	RecyclerView recyclerView;
	SharedPreferences sharedPreferences;
	ScanHistoryAdapter scanHistoryAdapter;
	MaterialTextView tv_scan_history_toast;
	SwitchMaterial switchMaterial;
	Map<String, ?> map;
	ListPopupWindow listPopupWindow_toolbar;
	String[] strings_clear_all = {"清空历史记录"}, content;
	ArrayList<String> list = new ArrayList<>();
	Object[] temp_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_history);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		sharedPreferences = getSharedPreferences(SettingsBean.SP_NAME_SCAN_HISTORY, MODE_PRIVATE);

		//历史记录数据读取
		map = sharedPreferences.getAll();
		temp_date = map.keySet().toArray();
		if (temp_date.length >= 2) {
			Arrays.sort(temp_date, Collections.reverseOrder());
		}
		content = new String[temp_date.length];
		for (int i = 0; i < temp_date.length; i++) {
			list.add(String.valueOf(temp_date[i]));
			content[i] = String.valueOf(map.get(String.valueOf(temp_date[i])));
		}
		scanHistoryAdapter = new ScanHistoryAdapter(list);

		initView();
		//显示数据列表
		showHistoryItem();
	}

	@SuppressLint("NotifyDataSetChanged")
	private void initView() {
		toolbar = findViewById(R.id.toolbar_scan_history);
		toolbar.setNavigationOnClickListener(view -> this.finish());
		toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_settings_more) {
				showListPopupWindow();
			}
			return true;
		});
		tv_scan_history_toast = findViewById(R.id.tv_scan_history_toast);

		SharedPreferences preferences = getSharedPreferences(SettingsBean.SP_NAME_SETTINGS, MODE_PRIVATE);
		switchMaterial = findViewById(R.id.sw_open_scan_history);
		switchMaterial.setChecked(SettingsBean.isOpenScanHistory);
		switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
			if (b) {
				SettingsBean.isOpenScanHistory = true;
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("isOpenScanHistory", SettingsBean.isOpenScanHistory);
				editor.apply();
			} else {
				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
				builder.setTitle("确定要关闭扫码历史记录功能？");
				builder.setMessage("关闭后新的扫码结果将不会保存");
				builder.setCancelable(false);
				builder.setNegativeButton("取消", (dialogInterface, i) ->
						switchMaterial.setChecked(SettingsBean.isOpenScanHistory));
				builder.setPositiveButton("确定", (dialogInterface, i) -> {
					SettingsBean.isOpenScanHistory = false;
					SharedPreferences.Editor editor = preferences.edit();
					editor.putBoolean("isOpenScanHistory", SettingsBean.isOpenScanHistory);
					editor.apply();
				});
				builder.show();
			}
		});
		recyclerView = findViewById(R.id.rv_scan_history);
	}

	private void showListPopupWindow() {
		listPopupWindow_toolbar = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);
		listPopupWindow_toolbar.setWidth(500);
		listPopupWindow_toolbar.setDropDownGravity(Gravity.END);
		listPopupWindow_toolbar.setAnchorView(toolbar);
		ArrayAdapter<String> stringArrayAdapter =
				new ArrayAdapter<>(this, R.layout.list_popup_window_item, strings_clear_all);
		listPopupWindow_toolbar.setAdapter(stringArrayAdapter);
		listPopupWindow_toolbar.setOnItemClickListener((adapterView, view, i, l) -> {
			if (sharedPreferences.getAll().size() == 0) {
				ToastHelper.showToast(this, "已经没有记录啦！");
				listPopupWindow_toolbar.dismiss();
				return;
			}
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setTitle("确定要清空扫码历史？");
			builder.setNegativeButton("取消", null);
			builder.setCancelable(false);
			builder.setPositiveButton("确定", (dialogInterface, i1) -> clearScanHistory());
			builder.show();
			listPopupWindow_toolbar.dismiss();
		});
		listPopupWindow_toolbar.show();
	}

	@SuppressLint("NotifyDataSetChanged")
	private void clearScanHistory() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		list.clear();
		scanHistoryAdapter.notifyDataSetChanged();
		tv_scan_history_toast.setText("暂无扫码历史记录");
	}

	private void showHistoryItem() {
		LoadingDialogHelper loadingDialogHelper = new LoadingDialogHelper(this, "加载中……");
		loadingDialogHelper.show();
		tv_scan_history_toast.setText("");

		scanHistoryAdapter.setOnItemClickListener(position -> {
			MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
			builder.setMessage(content[position]);
			builder.setTitle(String.valueOf(temp_date[position]));
			if (NetworkHelper.isHttpUrl(content[position])) {
				builder.setPositiveButton("打开浏览器", (dialogInterface, i) ->
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(content[position]))));
				builder.setNegativeButton("取消", null);
				builder.setNeutralButton("复制", (dialogInterface, i) -> {
					ClipboardManager clipboardManager =
							(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content[position]));
					ToastHelper.showToast(this, "复制成功");
				});
			} else {
				builder.setPositiveButton("复制", (dialogInterface, i) -> {
					ClipboardManager clipboardManager =
							(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content[position]));
					ToastHelper.showToast(this, "复制成功");
				});
				builder.setNegativeButton("取消", null);
			}
			builder.show();
		});
		//加载适配器
		recyclerView.setAdapter(scanHistoryAdapter);
		//显示分割线
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(dividerItemDecoration);
		if (map.size() == 0) {
			tv_scan_history_toast.setText("暂无扫码历史记录");
		}
		loadingDialogHelper.dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
