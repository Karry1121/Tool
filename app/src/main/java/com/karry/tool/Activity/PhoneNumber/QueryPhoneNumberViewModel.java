package com.karry.tool.Activity.PhoneNumber;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.karry.tool.Bean.PhoneInfoBean;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QueryPhoneNumberViewModel extends AndroidViewModel {
	LoadingDialogHelper loadingDialogHelper;
	MutableLiveData<Boolean> isSuccessGet, isShowDialog;
	String[] strings;
	String[] nameList = {"查询号码/号段", "归属地", "运营商", "区号", "邮编"};

	public QueryPhoneNumberViewModel(Application application) {
		super(application);
		isShowDialog = new MutableLiveData<>();
		isSuccessGet = new MutableLiveData<>();
	}

	public void getPhoneInfo(String phoneNum) {
		isShowDialog.postValue(true);
		OkHttpClient okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
		Request request = new Request.Builder().url("https://apis.juhe.cn/mobile/get?phone="
				+ phoneNum + "&key=" + SettingsBean.API_KEY_QUERY_PHONE_LOCATION)
				.method("GET", null).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				isSuccessGet.postValue(false);
				isShowDialog.postValue(false);
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				String data = Objects.requireNonNull(response.body()).string();
				PhoneInfoBean phoneInfoBean = new Gson().fromJson(data, PhoneInfoBean.class);
				if (phoneInfoBean.getResult().getProvince().equals(phoneInfoBean.getResult().getCity())) {
					strings = new String[]{phoneNum, phoneInfoBean.getResult().getProvince(),
							"中国" + phoneInfoBean.getResult().getCompany(),
							phoneInfoBean.getResult().getAreacode(),
							phoneInfoBean.getResult().getZip()};
				} else {
					strings = new String[]{phoneNum,
							phoneInfoBean.getResult().getProvince() + phoneInfoBean.getResult().getCity(),
							"中国" + phoneInfoBean.getResult().getCompany(),
							phoneInfoBean.getResult().getAreacode(),
							phoneInfoBean.getResult().getZip()};
				}
				isShowDialog.postValue(false);
				isSuccessGet.postValue(true);
			}
		});
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		loadingDialogHelper = null;
		isSuccessGet = null;
		isShowDialog = null;
		strings = null;
	}
}
