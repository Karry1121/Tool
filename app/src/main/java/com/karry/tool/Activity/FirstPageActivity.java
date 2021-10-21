package com.karry.tool.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.karry.tool.Activity.GenerateQRCode.GenerateQRCodeActivity;
import com.karry.tool.Activity.OilPrice.OilPriceActivity;
import com.karry.tool.Activity.PhoneNumber.QueryPhoneNumberActivity;
import com.karry.tool.Activity.home.HomeFragment;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Bean.TokenBean;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;
import com.karry.tool.databinding.ActivityFirstPageBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirstPageActivity extends AppCompatActivity {
	private static long exitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//加载启动画面，一定要写在setContentView之前
		SplashScreen.installSplashScreen(this);

		ActivityFirstPageBinding binding = ActivityFirstPageBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_first_page);
		NavigationUI.setupWithNavController(binding.navView, navController);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		setDynamicShortcuts();

		SharedPreferences sharedPreferences = getSharedPreferences(SettingsBean.SP_NAME_SETTINGS, MODE_PRIVATE);
		SettingsBean.QRCodeSize = sharedPreferences.getInt("QRCodeSize", 300);
		SettingsBean.isPoetryAutoOpenBrowser = sharedPreferences.getBoolean("isPoetryAutoOpenBrowser", true);
		SettingsBean.isOpenScanHistory = sharedPreferences.getBoolean("isOpenScanHistory", true);

		if (!NetworkHelper.isNetworkConnected(this)) {
			ToastHelper.showToast(this, "当前网络不可用，部分功能可能受限");
			return;
		}
		getToken();
	}

	private void getToken() {
		SharedPreferences sharedPreferences = getSharedPreferences("poetryToken", MODE_PRIVATE);
		if (sharedPreferences.getString("token", "").equals("")) {
			OkHttpClient okHttpClient = NetworkHelper.getOkHttpClient(this);
			Request request = new Request.Builder().url("https://v2.jinrishici.com/token")
					.method("GET", null).build();
			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(@NonNull Call call, @NonNull IOException e) {
				}

				@Override
				public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
					String data = Objects.requireNonNull(response.body()).string();
					TokenBean tokenBean = new Gson().fromJson(data, TokenBean.class);
					if (tokenBean.getStatus().equals("success")) {
						NetworkHelper.poetryToken = tokenBean.getData();
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("token", NetworkHelper.poetryToken);
						editor.apply();
					}
				}
			});
		} else {
			NetworkHelper.poetryToken = sharedPreferences.getString("token", "");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if (requestCode == HomeFragment.REQUEST_CODE_SCAN) {
			Object obj = data.getParcelableExtra(ScanUtil.RESULT);
			if (obj instanceof HmsScan) {
				if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
					String originalScanResult = ((HmsScan) obj).getOriginalValue();
					//保存扫码历史
					if (SettingsBean.isOpenScanHistory) {
						SharedPreferences sharedPreferences = getSharedPreferences(SettingsBean.SP_NAME_SCAN_HISTORY, MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString(OtherHelper.getCurrentDate("yyyy-MM-dd HH:mm:ss"), originalScanResult);
						editor.apply();
					}
					//解析扫码内容
					if (NetworkHelper.isHttpUrl(originalScanResult)) {
						if (NetworkHelper.isNetworkConnected(FirstPageActivity.this)) {
							//如果是网页打开手机默认浏览器
							MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
							builder.setTitle("识别到这是一个网址");
							builder.setMessage(originalScanResult);
							builder.setPositiveButton("打开浏览器", (dialogInterface, i) ->
									startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(originalScanResult))));
							builder.setNegativeButton("取消", null);
							builder.setNeutralButton("复制", (dialogInterface, i) -> {
								ClipboardManager clipboardManager =
										(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
								clipboardManager.setPrimaryClip(ClipData.newPlainText(null, originalScanResult));
								ToastHelper.showToast(this, "复制成功");
							});
							builder.setCancelable(false);
							builder.show();
						} else {
							ToastHelper.showToast(FirstPageActivity.this, "当前网络不可用，请检查后再试");
						}
					} else {
						//不是网址需要分情况讨论
						if (OtherHelper.isPhoneNumber(originalScanResult)) {
							OtherHelper.showMyDialog(FirstPageActivity.this, "这可能是一个手机号", originalScanResult);
						} else {
							OtherHelper.showMyDialog(FirstPageActivity.this, "扫码结果", originalScanResult);
						}
					}
				}
			}
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N_MR1)
	private ShortcutInfo createShortcutInfo1() {
		Intent[] intents = new Intent[]{
				new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, FirstPageActivity.class),
				//加该FLAG的目的是让SplashActivity作为根activity
				new Intent(Intent.ACTION_VIEW).setClassName(getPackageName(),
						OilPriceActivity.class.getName())
		};
		return new ShortcutInfo.Builder(this, "id_oilPrice")
				.setLongLabel(getString(R.string.btn_goQueryOilPrice))
				.setShortLabel(getString(R.string.btn_goQueryOilPrice))
				.setIcon(Icon.createWithResource(this, R.drawable.ic_car_oil))
				.setIntents(intents).build();
	}

	@RequiresApi(api = Build.VERSION_CODES.N_MR1)
	private ShortcutInfo createShortcutInfo2() {
		Intent[] intents = new Intent[]{
				new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, FirstPageActivity.class),
				//加该FLAG的目的是让SplashActivity作为根activity
				new Intent(Intent.ACTION_VIEW).setClassName(getPackageName(),
						GenerateQRCodeActivity.class.getName())
		};
		return new ShortcutInfo.Builder(this, "id_qrcode")
				.setLongLabel(getString(R.string.btn_GenerateQRCode))
				.setShortLabel(getString(R.string.btn_GenerateQRCode))
				.setIcon(Icon.createWithResource(this, R.drawable.ic_qrcode))
				.setIntents(intents).build();
	}

	@RequiresApi(api = Build.VERSION_CODES.N_MR1)
	private ShortcutInfo createShortcutInfo3() {
		Intent[] intents = new Intent[]{
				new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, FirstPageActivity.class),
				//加该FLAG的目的是让SplashActivity作为根activity
				new Intent(Intent.ACTION_VIEW).setClassName(getPackageName(),
						QueryPhoneNumberActivity.class.getName())
		};
		return new ShortcutInfo.Builder(this, "id_phone")
				.setLongLabel(getString(R.string.btn_goQueryPhoneNumber))
				.setShortLabel(getString(R.string.btn_goQueryPhoneNumber))
				.setIcon(Icon.createWithResource(this, R.drawable.ic_phone))
				.setIntents(intents).build();
	}

	//动态注册桌面图标快捷菜单
	private void setDynamicShortcuts() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
			ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
			List<ShortcutInfo> shortcutInfos = new ArrayList<>();
			shortcutInfos.add(createShortcutInfo1());
			shortcutInfos.add(createShortcutInfo2());
			shortcutInfos.add(createShortcutInfo3());
			if (shortcutManager != null) {
				shortcutManager.setDynamicShortcuts(shortcutInfos);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				ToastHelper.showToast(FirstPageActivity.this, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
				return true;
			} else {
				this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}