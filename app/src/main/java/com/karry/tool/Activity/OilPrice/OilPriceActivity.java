package com.karry.tool.Activity.OilPrice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Adapter.OilPriceAdapter;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

import java.util.Objects;

public class OilPriceActivity extends AppCompatActivity {
	RecyclerView recyclerView;
	MaterialTextView tv_oil_location;
	MaterialToolbar toolbar;
	OilPriceViewModel oilPriceViewModel;
	String[] oilTitle = new String[]{"92#", "95#", "98#", "0#"}, strings = new String[]{"查询其他省"};
	@SuppressLint("SetTextI18n")
	ActivityResultLauncher<String[]> launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
			result -> {
				if (result.get(Manifest.permission.ACCESS_COARSE_LOCATION) != null
						&& result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
					if (Objects.requireNonNull(result.get(Manifest.permission.ACCESS_COARSE_LOCATION)).equals(true)
							&& Objects.requireNonNull(result.get(Manifest.permission.ACCESS_FINE_LOCATION)).equals(true)) {
						oilPriceViewModel.getLocation();
					} else {
						ToastHelper.showToast(this, "缺少定位权限，将加载随机值");
						tv_oil_location.setText(oilPriceViewModel.oilBean.getResult()[0].getCity() + "今日油价");
						OilPriceAdapter oilPriceAdapter = new OilPriceAdapter(
								new String[]{"92#", "95#", "98#", "0#"},
								new String[]{oilPriceViewModel.oilBean.getResult()[0].getP_92(),
										oilPriceViewModel.oilBean.getResult()[0].getP_95(),
										oilPriceViewModel.oilBean.getResult()[0].getP_98(),
										oilPriceViewModel.oilBean.getResult()[0].getP_0()});
						recyclerView.setAdapter(oilPriceAdapter);
						recyclerView.setItemAnimator(new DefaultItemAnimator());
						try {
							oilPriceViewModel.loadingDialogHelper.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oil_price);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，浅色模式下不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();

		oilPriceViewModel = new ViewModelProvider(this,
				(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(OilPriceViewModel.class);
		if (oilPriceViewModel.loadingDialogHelper == null) {
			oilPriceViewModel.loadingDialogHelper = new LoadingDialogHelper(this, "加载中……");
		}

		oilPriceViewModel.isShowDialog.observe(this, aBoolean -> {
			if (aBoolean) {
				oilPriceViewModel.loadingDialogHelper.show();
			} else {
				oilPriceViewModel.loadingDialogHelper.dismiss();
			}
		});

		oilPriceViewModel.isGetPrice.observe(this, aBoolean -> {
			if (aBoolean) {
				if (oilPriceViewModel.province == null) {
					launcher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
				}
			} else {
				ToastHelper.showToast(this, "加载失败，稍后再试试");
			}
		});

		oilPriceViewModel.isGetLocation.observe(this, aBoolean -> {
			if (aBoolean) {
				tv_oil_location.setText(oilPriceViewModel.province + "今日油价");
				OilPriceAdapter oilPriceAdapter = new OilPriceAdapter(oilTitle, oilPriceViewModel.stringsPrice);
				recyclerView.setAdapter(oilPriceAdapter);
			} else {
				tv_oil_location.setText(oilPriceViewModel.oilBean.getResult()[0].getCity() + "今日油价");
				OilPriceAdapter oilPriceAdapter = new OilPriceAdapter(
						oilTitle, new String[]{oilPriceViewModel.oilBean.getResult()[0].getP_92(),
						oilPriceViewModel.oilBean.getResult()[0].getP_95(),
						oilPriceViewModel.oilBean.getResult()[0].getP_98(),
						oilPriceViewModel.oilBean.getResult()[0].getP_0()});
				recyclerView.setAdapter(oilPriceAdapter);
			}
			recyclerView.setItemAnimator(new DefaultItemAnimator());
		});

		if (oilPriceViewModel.oilBean == null) {
			oilPriceViewModel.checkOilPrice();
		} else if (oilPriceViewModel.province == null) {
			launcher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
		} else {
			tv_oil_location.setText(oilPriceViewModel.province + "今日油价");
			OilPriceAdapter oilPriceAdapter = new OilPriceAdapter(oilTitle, oilPriceViewModel.stringsPrice);
			recyclerView.setAdapter(oilPriceAdapter);
			recyclerView.setItemAnimator(new DefaultItemAnimator());
		}
	}

	@SuppressLint("SetTextI18n")
	private void initView() {
		recyclerView = findViewById(R.id.rv_oil_price);
		tv_oil_location = findViewById(R.id.tv_oil_location);
		toolbar = findViewById(R.id.toolbar_oil_price);
		toolbar.setNavigationOnClickListener(view -> OilPriceActivity.this.finish());
		toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_settings_more) {
				ListPopupWindow listPopupWindow = new ListPopupWindow(this, null, R.attr.listPopupWindowStyle);
				listPopupWindow.setWidth(500);
				listPopupWindow.setAnchorView(toolbar);
				listPopupWindow.setDropDownGravity(Gravity.END);
				ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.list_popup_window_item, strings);
				listPopupWindow.setAdapter(stringArrayAdapter);
				listPopupWindow.setOnItemClickListener((adapterView, view, i, l) -> {
					if (oilPriceViewModel.oilBean != null) {
						Message message = oilPriceViewModel.handler.obtainMessage();
						message.what = oilPriceViewModel.MSG_DIALOG;
						message.obj = new MaterialAlertDialogBuilder(this);
						oilPriceViewModel.handler.sendMessage(message);
					} else {
						ToastHelper.showToast(this, "还没有获取到油价信息，请重新获取");
					}
					listPopupWindow.dismiss();
				});
				listPopupWindow.show();
			}
			return true;
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
