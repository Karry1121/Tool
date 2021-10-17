package com.karry.tool.Activity.TodayPoetry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.karry.tool.Bean.PoetryBean;
import com.karry.tool.Bean.TokenBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PoetryViewModel extends AndroidViewModel {
	private static final int GET_TOKEN = 2;
	MutableLiveData<Boolean> isShowDialog, isGetPoetry;
	Boolean isShowFullContent;
	LoadingDialogHelper loadingDialogHelper;
	PoetryBean poetryBean;
	OkHttpClient okHttpClient;
	Handler handler = new Handler(message -> {
		if (message.what == GET_TOKEN) {
			getPoetry();
		}
		return false;
	});

	public PoetryViewModel(Application application) {
		super(application);
		isShowDialog = new MutableLiveData<>();
		isGetPoetry = new MutableLiveData<>();
	}

	public void getToken() {
		isShowFullContent = false;
		if (okHttpClient == null) {
			okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
		}
		Request request = new Request.Builder().url("https://v2.jinrishici.com/token")
				.method("GET", null).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				isGetPoetry.postValue(false);
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				String data = Objects.requireNonNull(response.body()).string();
				TokenBean tokenBean = new Gson().fromJson(data, TokenBean.class);
				if (tokenBean.getStatus().equals("success")) {
					NetworkHelper.poetryToken = tokenBean.getData();
					SharedPreferences sharedPreferences = getApplication().
							getSharedPreferences("poetryToken", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("token", NetworkHelper.poetryToken);
					editor.apply();
					Message message = handler.obtainMessage();
					message.what = GET_TOKEN;
					handler.sendMessage(message);
				}
			}
		});
	}

	public void getPoetry() {
		isShowDialog.postValue(true);
		isShowFullContent = false;
		if (okHttpClient == null) {
			okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
		}
		Request request = new Request.Builder().url("https://v2.jinrishici.com/sentence")
				.addHeader("X-User-Token", NetworkHelper.poetryToken)
				.method("GET", null).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				isShowDialog.postValue(false);
				isGetPoetry.postValue(false);
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
				isShowDialog.postValue(false);
				String data = Objects.requireNonNull(response.body()).string();
				try {
					poetryBean = new Gson().fromJson(data, PoetryBean.class);
					isGetPoetry.postValue(true);
				} catch (Exception e) {
					e.printStackTrace();
					isGetPoetry.postValue(false);
				}
			}
		});
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		handler = null;
		isShowDialog = null;
		isGetPoetry = null;
		okHttpClient = null;
		loadingDialogHelper = null;
		poetryBean = null;
	}
}
