package com.karry.tool.Activity.PhoneNumber;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.karry.tool.Adapter.PhoneInfoAdapter;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

import java.util.Objects;

public class QueryPhoneNumberActivity extends AppCompatActivity {
	private TextInputEditText et_phone_number;
	private MaterialButton btn_query_phone_location;
	private RecyclerView rv_phone_detail;
	private PhoneInfoAdapter phoneInfoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_phone_number);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();

		if (savedInstanceState != null) {
			et_phone_number.setText(savedInstanceState.getString("etContent", ""));
		}

		QueryPhoneNumberViewModel queryPhoneNumberViewModel = new ViewModelProvider(this,
				ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(QueryPhoneNumberViewModel.class);
		if (queryPhoneNumberViewModel.loadingDialogHelper == null) {
			queryPhoneNumberViewModel.loadingDialogHelper = new LoadingDialogHelper(this, "查询中，请稍后……");
		}
		//屏幕旋转，恢复原来显示的数据
		if (queryPhoneNumberViewModel.strings != null) {
			phoneInfoAdapter = new PhoneInfoAdapter(
					queryPhoneNumberViewModel.nameList, queryPhoneNumberViewModel.strings);
			rv_phone_detail.setAdapter(phoneInfoAdapter);
			rv_phone_detail.setItemAnimator(new DefaultItemAnimator());
		}
		queryPhoneNumberViewModel.isShowDialog.observe(this, aBoolean -> {
			if (aBoolean) {
				queryPhoneNumberViewModel.loadingDialogHelper.show();
			} else {
				queryPhoneNumberViewModel.loadingDialogHelper.dismiss();
			}
		});
		queryPhoneNumberViewModel.isSuccessGet.observe(this, aBoolean -> {
			if (aBoolean) {
				phoneInfoAdapter = new PhoneInfoAdapter(
						queryPhoneNumberViewModel.nameList, queryPhoneNumberViewModel.strings);
				rv_phone_detail.setAdapter(phoneInfoAdapter);
				rv_phone_detail.setItemAnimator(new DefaultItemAnimator());
			} else {
				ToastHelper.showToast(this, "查询失败，稍后再试试");
			}
		});
		btn_query_phone_location.setOnClickListener(view -> {
			String phone;
			//输入数字在7-11位之间，用0补齐11位
			if (Objects.requireNonNull(et_phone_number.getText()).length() >= 7 &&
					Objects.requireNonNull(et_phone_number.getText()).length() < 11) {
				phone = String.format("%-11s", et_phone_number.getText().toString()).replace(" ", "0");
			} else {
				ToastHelper.showToast(QueryPhoneNumberActivity.this, "请填写正确的手机号~");
				return;
			}
			//补齐之后判断是不是手机号的形式
			if (!OtherHelper.isPhoneNumber(phone)) {
				ToastHelper.showToast(QueryPhoneNumberActivity.this, "请填写正确的手机号~");
				return;
			}
			queryPhoneNumberViewModel.getPhoneInfo(et_phone_number.getText().toString());
		});
	}

	private void initView() {
		MaterialToolbar toolbar = findViewById(R.id.toolbar_query_phone_number);
		toolbar.setNavigationOnClickListener(view -> QueryPhoneNumberActivity.this.finish());
		et_phone_number = findViewById(R.id.et_phone_number);

		btn_query_phone_location = findViewById(R.id.btn_query_phone_location);
		rv_phone_detail = findViewById(R.id.rv_phone_detail);

		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
		gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
		rv_phone_detail.setLayoutManager(gridLayoutManager);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("etContent", Objects.requireNonNull(et_phone_number.getText()).toString());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isShouldHideInput(view, ev)) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMethodManager != null) {
					assert view != null;
					inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
					view.clearFocus();
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if ((v instanceof TextInputEditText)) {
			int[] leftTop = {0, 0};
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}