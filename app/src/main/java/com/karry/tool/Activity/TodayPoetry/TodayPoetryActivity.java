package com.karry.tool.Activity.TodayPoetry;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

public class TodayPoetryActivity extends AppCompatActivity {
	MaterialTextView tv_author, tv_poetry, tv_full_poetry;
	MaterialButton btn_change, btn_full_poetry;
	MaterialToolbar toolbar;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_today_poetry);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();

		PoetryViewModel poetryViewModel = new ViewModelProvider(this,
				(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(PoetryViewModel.class);

		//有数据，则恢复显示；没有数据，则重新获取
		if (poetryViewModel.poetryBean != null) {
			tv_poetry.setText(poetryViewModel.poetryBean.getData().getContent());
			tv_author.setText("出自 " + poetryViewModel.poetryBean.getData().getOrigin().getAuthor()
					+ "《" + poetryViewModel.poetryBean.getData().getOrigin().getTitle() + "》");
			if (poetryViewModel.isShowFullContent) {
				String d = poetryViewModel.poetryBean.getData().getOrigin().getDynasty().replace("代", "");
				tv_full_poetry.setText(poetryViewModel.poetryBean.getData().getOrigin().getTitle() + "\n"
						+ d + " · " + poetryViewModel.poetryBean.getData().getOrigin().getAuthor() + "\n");
				String[] content = poetryViewModel.poetryBean.getData().getOrigin().getContent();
				for (int i = 0; i < content.length; i++) {
					if (i != content.length - 1) {
						tv_full_poetry.append(content[i] + "\n");
					} else {
						tv_full_poetry.append(content[i]);
					}
				}
			}
		} else {
			if (!NetworkHelper.isNetworkConnected(this)) {
				ToastHelper.showToast(this, "当前网络不可用，请检查网络设置");
			} else {
				if (!NetworkHelper.poetryToken.equals("")) {
					poetryViewModel.getPoetry();
				} else {
					poetryViewModel.getToken();
				}
			}
		}

		//初始化对话框
		if (poetryViewModel.loadingDialogHelper == null) {
			poetryViewModel.loadingDialogHelper = new LoadingDialogHelper(this);
		}
		//监控对话框标志，有变化进行相应的操作
		poetryViewModel.isShowDialog.observe(this, aBoolean -> {
			if (aBoolean) {
				poetryViewModel.loadingDialogHelper.show();
			} else {
				poetryViewModel.loadingDialogHelper.dismiss();
			}
		});

		//监控诗词获取结果标志
		poetryViewModel.isGetPoetry.observe(this, aBoolean -> {
			if (aBoolean) {
				tv_poetry.setText(poetryViewModel.poetryBean.getData().getContent());
				tv_author.setText("出自 " + poetryViewModel.poetryBean.getData().getOrigin().getAuthor()
						+ "《" + poetryViewModel.poetryBean.getData().getOrigin().getTitle() + "》");
			} else {
				ToastHelper.showToast(this, "获取失败，稍后试试");
			}
		});

		btn_change.setOnClickListener(view -> {
			tv_poetry.setText("");
			tv_author.setText("");
			tv_full_poetry.setText("");
			if (!NetworkHelper.isNetworkConnected(this)) {
				ToastHelper.showToast(this, "当前网络不可用，请检查网络设置");
				return;
			}
			if (!NetworkHelper.poetryToken.equals("")) {
				poetryViewModel.getPoetry();
			} else {
				poetryViewModel.getToken();
			}
		});
		btn_full_poetry.setOnClickListener(view -> {
			if (poetryViewModel.poetryBean == null) {
				ToastHelper.showToast(TodayPoetryActivity.this, "请先获取诗词");
			} else {
				poetryViewModel.isShowFullContent = true;
				String d = poetryViewModel.poetryBean.getData().getOrigin().getDynasty().replace("代", "");
				tv_full_poetry.setText(poetryViewModel.poetryBean.getData().getOrigin().getTitle() + "\n"
						+ d + " · " + poetryViewModel.poetryBean.getData().getOrigin().getAuthor() + "\n");
				String[] content = poetryViewModel.poetryBean.getData().getOrigin().getContent();
				for (int i = 0; i < content.length; i++) {
					if (i != content.length - 1) {
						tv_full_poetry.append(content[i] + "\n");
					} else {
						tv_full_poetry.append(content[i]);
					}
				}
			}
		});
		tv_author.setOnLongClickListener(view -> {
			ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setPrimaryClip(ClipData.newPlainText(null,
					poetryViewModel.poetryBean.getData().getOrigin().getTitle()));
			ToastHelper.showToast(this, "诗题复制成功，快去搜索吧~");
			if (SettingsBean.isPoetryAutoOpenBrowser) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com")));
			}
			return false;
		});
	}

	private void initView() {
		tv_author = findViewById(R.id.tv_author);
		tv_poetry = findViewById(R.id.tv_poetry);
		tv_full_poetry = findViewById(R.id.tv_full_poetry);
		btn_change = findViewById(R.id.btn_change_poetry);
		btn_full_poetry = findViewById(R.id.btn_full_poetry);
		toolbar = findViewById(R.id.toolbar_poetry);
		toolbar.setNavigationOnClickListener(view -> TodayPoetryActivity.this.finish());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}