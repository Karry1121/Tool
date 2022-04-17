package com.karry.tool.Activity.HistoryToday;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Adapter.HistoryAdapter;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

public class HistoryTodayActivity extends AppCompatActivity {
	MaterialTextView tv_history_title;
	HistoryAdapter historyAdapter;
	RecyclerView recyclerView;
	MaterialToolbar toolbar;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_today);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();

		HistoryTodayViewModel historyTodayViewModel = new ViewModelProvider(this,
				(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(HistoryTodayViewModel.class);
		if (historyTodayViewModel.loadingDialogHelper == null) {
			historyTodayViewModel.loadingDialogHelper = new LoadingDialogHelper(this);
		}
		//没有数据重新获取
		if (historyTodayViewModel.stringsContent == null || historyTodayViewModel.stringsDate == null) {
			historyTodayViewModel.getHistoryTodayInfo();
		}
		historyTodayViewModel.isShowDialog.observe(this, aBoolean -> {
			if (aBoolean) {
				historyTodayViewModel.loadingDialogHelper.show();
			} else {
				historyTodayViewModel.loadingDialogHelper.dismiss();
			}
		});
		historyTodayViewModel.isSuccessGet.observe(this, aBoolean -> {
			if (aBoolean) {
				historyAdapter = new HistoryAdapter(historyTodayViewModel.stringsDate, historyTodayViewModel.stringsContent);
				recyclerView.setAdapter(historyAdapter);
			} else {
				ToastHelper.showToast(this, "获取失败，稍后再试试吧");
			}
		});

		tv_history_title.setText("历史上的" + OtherHelper.getCurrentDate("M月d日"));
	}

	private void initView() {
		tv_history_title = findViewById(R.id.tv_history_title);
		recyclerView = findViewById(R.id.recycler_view);
		toolbar = findViewById(R.id.toolbar_history_today);
		toolbar.setNavigationOnClickListener(view -> HistoryTodayActivity.this.finish());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}