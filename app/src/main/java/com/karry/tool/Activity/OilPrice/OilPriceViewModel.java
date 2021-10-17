package com.karry.tool.Activity.OilPrice;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.karry.tool.Bean.OilBean;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OilPriceViewModel extends AndroidViewModel {
	public final int MSG_LOCATION = 1, MSG_DIALOG = 2;
	LoadingDialogHelper loadingDialogHelper;
	MutableLiveData<Boolean> isShowDialog, isGetPrice, isGetLocation;
	String[] stringsPrice;
	OilBean oilBean;
	CharSequence[] charSequences;
	String province;
	Handler handler = new Handler(message -> {
		if (message.what == MSG_LOCATION) {
			province = (String) message.obj;
			province = province.substring(0, province.indexOf("省"));
			for (int i = 0; i < oilBean.getResult().length; i++) {
				if (province.equals(oilBean.getResult()[i].getCity())) {
					stringsPrice = new String[]{oilBean.getResult()[i].getP_92(), oilBean.getResult()[i].getP_95(),
							oilBean.getResult()[i].getP_98(), oilBean.getResult()[i].getP_0()};
					isGetLocation.postValue(true);
					break;
				}
			}
			isShowDialog.postValue(false);
		} else if (message.what == MSG_DIALOG) {
			MaterialAlertDialogBuilder materialAlertDialogBuilder = (MaterialAlertDialogBuilder) message.obj;
			materialAlertDialogBuilder.setTitle("请选择要查询的省");
			materialAlertDialogBuilder.setNeutralButton("取消", null);
			materialAlertDialogBuilder.setPositiveButton("确定", (dialogInterface, i) -> isGetLocation.postValue(true));
			materialAlertDialogBuilder.setSingleChoiceItems(charSequences, -1, (dialogInterface, i) -> {
				for (int k = 0; k < oilBean.getResult().length; k++) {
					if (charSequences[i].equals(oilBean.getResult()[k].getCity())) {
						province = oilBean.getResult()[k].getCity();
						stringsPrice = new String[]{oilBean.getResult()[k].getP_92(), oilBean.getResult()[k].getP_95(),
								oilBean.getResult()[k].getP_98(), oilBean.getResult()[k].getP_0()};
						break;
					}
				}
			});
			materialAlertDialogBuilder.show();
		}
		return false;
	});

	public OilPriceViewModel(@NonNull Application application) {
		super(application);
		isShowDialog = new MutableLiveData<>();
		isGetPrice = new MutableLiveData<>();
		isGetLocation = new MutableLiveData<>();
	}

	@SuppressLint("MissingPermission")
	public void getLocation() {
		isShowDialog.postValue(true);
		LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		//定位精准度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		//海拔信息是否关注
		criteria.setAltitudeRequired(false);
		//对周围的事情是否关心
		criteria.setBearingRequired(false);
		//是否支持收费查询
		criteria.setCostAllowed(false);
		//是否耗电
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		//对速度是否关注
		criteria.setSpeedRequired(false);
		//注册监听
		locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true),
				60000, 0, location -> {
					String temp = getProvinceName(location.getLongitude(), location.getLatitude());
					if (temp != null) {
						Message message = handler.obtainMessage();
						message.what = MSG_LOCATION;
						message.obj = temp;
						handler.sendMessage(message);
					} else {
						isGetLocation.postValue(false);
						isShowDialog.postValue(false);
					}
				});
	}

	@Nullable
	private String getProvinceName(double lnt, double lat) {
		Geocoder geocoder = new Geocoder(getApplication());
		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lnt, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				return address.getAdminArea(); //获取省份信息
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void checkOilPrice() {
		SharedPreferences sharedPreferences = getApplication()
				.getSharedPreferences(SettingsBean.SP_NAME_OIL_PRICE, Context.MODE_PRIVATE);
		isShowDialog.postValue(true);
		if (!sharedPreferences.getString("date", "").equals(OtherHelper.getCurrentDate("M/d"))) {
			if (!NetworkHelper.isNetworkConnected(getApplication())) {
				ToastHelper.showToast(getApplication(), "当前网络不可用，请检查网络设置");
				isShowDialog.postValue(false);
				return;
			}
			OkHttpClient okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
			Request request = new Request.Builder().url("https://apis.juhe.cn/gnyj/query?key=" + SettingsBean.API_OIL_PRICE)
					.method("GET", null).build();
			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(@NonNull Call call, @NonNull IOException e) {
					isShowDialog.postValue(false);
					isGetPrice.postValue(false);
				}

				@Override
				public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
					String data = Objects.requireNonNull(response.body()).string();
					//保存当天获取的数据
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("date", OtherHelper.getCurrentDate("M/d"));
					editor.putString("price", data);
					editor.apply();
					oilBean = new Gson().fromJson(data, OilBean.class);
					//获取省份名称，形成一个数组
					charSequences = new CharSequence[oilBean.getResult().length];
					for (int i = 0; i < oilBean.getResult().length; i++) {
						charSequences[i] = oilBean.getResult()[i].getCity();
					}
					isGetPrice.postValue(true);
				}
			});
		} else {
			if (!NetworkHelper.isNetworkConnected(getApplication())) {
				ToastHelper.showToast(getApplication(), "当前网络不可用，已加载本地数据");
			}
			try {
				oilBean = new Gson().fromJson(sharedPreferences.getString("price", ""), OilBean.class);
				//获取省份名称，形成一个数组
				charSequences = new CharSequence[oilBean.getResult().length];
				for (int i = 0; i < oilBean.getResult().length; i++) {
					charSequences[i] = oilBean.getResult()[i].getCity();
				}
				isGetPrice.postValue(true);
			} catch (Exception e) {
				e.printStackTrace();
				isShowDialog.postValue(false);
				ToastHelper.showToast(getApplication(), "数据异常");
			}
		}
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		isGetPrice = null;
		isShowDialog = null;
		loadingDialogHelper = null;
		province = null;
		oilBean = null;
		stringsPrice = null;
		isGetLocation = null;
		charSequences = null;
	}
}
