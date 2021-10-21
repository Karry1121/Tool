package com.karry.tool.Activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

public class APPInfoActivity extends AppCompatActivity {
	MaterialToolbar toolbar;
	MaterialTextView tv_appVersion_info;
	ShapeableImageView iv_app_logo;
	boolean isShowVersionCode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appinfo);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，浅色模式下不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();
	}

	private void initView() {
		toolbar = findViewById(R.id.toolbar_app_info);
		toolbar.setNavigationOnClickListener(view -> APPInfoActivity.this.finish());
		tv_appVersion_info = findViewById(R.id.tv_appVersion_info);
		iv_app_logo = findViewById(R.id.iv_app_logo);
		iv_app_logo.setOnClickListener(view -> {
			isShowVersionCode = !isShowVersionCode;
			showText();
		});

		showText();
		tv_appVersion_info.setOnClickListener(view -> {
			isShowVersionCode = !isShowVersionCode;
			showText();
		});
	}

	@SuppressLint("SetTextI18n")
	private void showText() {
		if (isShowVersionCode) {
			tv_appVersion_info.setText(getVersion(0) + "(" + getVersion(1) + ")");
		} else {
			tv_appVersion_info.setText(getVersion(0));
		}
	}

	private String getVersion(int code) {
		String version = "";
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
			if (code == 1) {
				return String.valueOf(packageInfo.versionCode);
			} else {
				version = packageInfo.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}