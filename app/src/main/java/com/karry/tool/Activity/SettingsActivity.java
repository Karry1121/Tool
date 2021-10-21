package com.karry.tool.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

public class SettingsActivity extends AppCompatActivity {
	MaterialButton btn_about_app, btn_poetry_open_browser;
	MaterialToolbar toolbar;
	MaterialTextView tv_settings_poetry_result, tv_settings_show_qrcode_size;
	Slider slide_qrcode_size;
	String[] strings_poetry = {"复制并打开", "仅复制"};
	String[] strings_recovery = {"恢复默认设置"};
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	ListPopupWindow listPopupWindow_toolbar, listPopupWindow_btn_poetry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，浅色模式下不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		sharedPreferences = getSharedPreferences(SettingsBean.SP_NAME_SETTINGS, MODE_PRIVATE);
		editor = sharedPreferences.edit();
		listPopupWindow_btn_poetry = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);
		listPopupWindow_toolbar = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);

		initView();
		setupListPopupWindow();
	}

	@SuppressLint("SetTextI18n")
	private void initView() {
		toolbar = findViewById(R.id.toolbar_settings);
		toolbar.setNavigationOnClickListener(view -> SettingsActivity.this.finish());
		toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_settings_more) {
				//下拉框宽度
				listPopupWindow_toolbar.setWidth(500);
				listPopupWindow_toolbar.setDropDownGravity(Gravity.END);
				//下拉框显示在这个按钮下面（必要参数，没有会闪退）
				listPopupWindow_toolbar.setAnchorView(toolbar);
				//指定下拉框显示的内容
				ArrayAdapter<String> stringArrayAdapter =
						new ArrayAdapter<>(this, R.layout.list_popup_window_item, strings_recovery);
				listPopupWindow_toolbar.setAdapter(stringArrayAdapter);
				//每一个条目的按键事件
				listPopupWindow_toolbar.setOnItemClickListener((adapterView, view, i, l) -> {
					MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
					builder.setTitle("确定要加载默认参数？");
					builder.setNeutralButton("取消", null);
					builder.setCancelable(false);
					builder.setPositiveButton("确定", (dialogInterface, i1) -> {
						SettingsBean.loadDefault();
						//保存参数
						editor.putBoolean("isPoetryAutoOpenBrowser", SettingsBean.isPoetryAutoOpenBrowser);
						editor.putInt("QRCodeSize", SettingsBean.QRCodeSize);
						editor.apply();
						//在界面更新数据
						if (SettingsBean.isPoetryAutoOpenBrowser) {
							tv_settings_poetry_result.setText(strings_poetry[0]);
						} else {
							tv_settings_poetry_result.setText(strings_poetry[1]);
						}
						tv_settings_show_qrcode_size.setText("当前大小：" + SettingsBean.QRCodeSize);
						slide_qrcode_size.setValue(SettingsBean.QRCodeSize);
					});
					builder.show();
					listPopupWindow_toolbar.dismiss();
				});
				listPopupWindow_toolbar.show();
			}
			return true;
		});

		btn_about_app = findViewById(R.id.btn_about_app);
		btn_about_app.setOnClickListener(view -> startActivity(
				new Intent(SettingsActivity.this, APPInfoActivity.class)));

		tv_settings_poetry_result = findViewById(R.id.tv_settings_poetry_result);
		if (SettingsBean.isPoetryAutoOpenBrowser) {
			tv_settings_poetry_result.setText(strings_poetry[0]);
		} else {
			tv_settings_poetry_result.setText(strings_poetry[1]);
		}

		btn_poetry_open_browser = findViewById(R.id.btn_poetry_open_browser);

		tv_settings_show_qrcode_size = findViewById(R.id.tv_settings_show_qrcode_size);
		tv_settings_show_qrcode_size.setText("当前大小：" + SettingsBean.QRCodeSize);

		slide_qrcode_size = findViewById(R.id.slide_qrcode_size);
		slide_qrcode_size.setValue(SettingsBean.QRCodeSize);
		slide_qrcode_size.addOnChangeListener((slider, value, fromUser) ->
				tv_settings_show_qrcode_size.setText("当前大小：" + (int) value));
		slide_qrcode_size.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
			@Override
			public void onStartTrackingTouch(@NonNull Slider slider) {
			}

			@Override
			public void onStopTrackingTouch(@NonNull Slider slider) {
				if (slider.getValue() >= 800) {
					ToastHelper.showToast(SettingsActivity.this, "请注意，图片过大可能会影响显示效果");
				}
				SettingsBean.QRCodeSize = (int) slider.getValue();
				editor.putInt("QRCodeSize", SettingsBean.QRCodeSize);
				editor.apply();
			}
		});
	}

	private void setupListPopupWindow() {
		//下拉框宽度
		listPopupWindow_btn_poetry.setWidth(500);
		listPopupWindow_btn_poetry.setDropDownGravity(Gravity.END);
		//下拉框显示在这个按钮下面（必要参数，没有会闪退）
		listPopupWindow_btn_poetry.setAnchorView(btn_poetry_open_browser);
		//指定下拉框显示的内容
		ArrayAdapter<String> stringArrayAdapter =
				new ArrayAdapter<>(this, R.layout.list_popup_window_item, strings_poetry);
		listPopupWindow_btn_poetry.setAdapter(stringArrayAdapter);
		//每一个条目的按键事件
		listPopupWindow_btn_poetry.setOnItemClickListener((adapterView, view, i, l) -> {
			SettingsBean.isPoetryAutoOpenBrowser = i == 0;
			editor.putBoolean("isPoetryAutoOpenBrowser", SettingsBean.isPoetryAutoOpenBrowser);
			editor.apply();
			if (SettingsBean.isPoetryAutoOpenBrowser) {
				tv_settings_poetry_result.setText(strings_poetry[0]);
			} else {
				tv_settings_poetry_result.setText(strings_poetry[1]);
			}
			listPopupWindow_btn_poetry.dismiss();
		});
		btn_poetry_open_browser.setOnClickListener(view -> listPopupWindow_btn_poetry.show());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
