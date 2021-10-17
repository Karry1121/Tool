package com.karry.tool.Activity.HistoryToday;

import static android.content.Context.MODE_PRIVATE;
import static com.karry.tool.Bean.SettingsBean.API_KEY_HISTORY;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.karry.tool.Bean.HistoryBean;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryTodayViewModel extends AndroidViewModel {
	String[] stringsDate, stringsContent;
	MutableLiveData<Boolean> isSuccessGet, isShowDialog;
	LoadingDialogHelper loadingDialogHelper;

	public HistoryTodayViewModel(Application application) {
		super(application);
		isSuccessGet = new MutableLiveData<>();
		isShowDialog = new MutableLiveData<>();
	}

	public void getHistoryTodayInfo() {
		//获取存储的数据，日期不一致的需要重新请求获取
		SharedPreferences sharedPreferences = getApplication()
				.getSharedPreferences(SettingsBean.SP_NAME_HISTORY_TODAY, MODE_PRIVATE);
		isShowDialog.postValue(true);

		if (!sharedPreferences.getString("date", "").equals(OtherHelper.getCurrentDate("M/d"))) {
			if (!NetworkHelper.isNetworkConnected(getApplication())) {
				ToastHelper.showToast(getApplication(), "当前网络不可用，请检查网络设置");
				isShowDialog.postValue(false);
				return;
			}
			OkHttpClient okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
			Request request = new Request.Builder()
					.url("https://v.juhe.cn/todayOnhistory/queryEvent.php?key="
							+ API_KEY_HISTORY + "&date=" + OtherHelper.getCurrentDate("M/d"))
					.method("GET", null).build();
			Call call = okHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(@NonNull Call call, @NonNull IOException e) {
					e.printStackTrace();
					isSuccessGet.postValue(false);
					isShowDialog.postValue(false);
				}

				@Override
				public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
					String data = Objects.requireNonNull(response.body()).string();
					isShowDialog.postValue(false);
					try {
						HistoryBean historyBean = new Gson().fromJson(data, HistoryBean.class);
						stringsContent = new String[historyBean.getResult().length];
						stringsDate = new String[historyBean.getResult().length];
						for (int i = 0; i < historyBean.getResult().length; i++) {
							stringsDate[i] = historyBean.getResult()[i].getDate()
									.substring(0, historyBean.getResult()[i].getDate().indexOf("年")) + "年";
							stringsContent[i] = historyBean.getResult()[i].getTitle();
						}
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("content_date", Arrays.toString(stringsDate));
						editor.putString("content", Arrays.toString(stringsContent));
						editor.putString("date", OtherHelper.getCurrentDate("M/d"));
						editor.apply();
						isSuccessGet.postValue(true);
					} catch (Exception e) {
						e.printStackTrace();
						isSuccessGet.postValue(false);
					}
				}
			});
		} else {
			if (!NetworkHelper.isNetworkConnected(getApplication())) {
				ToastHelper.showToast(getApplication(), "当前网络不可用，已加载本地数据");
			}
			String list = sharedPreferences.getString("content", "");
			String list_date = sharedPreferences.getString("content_date", "");
			stringsContent = list.substring(1, list.length() - 1).split(",");
			stringsDate = list_date.substring(1, list_date.length() - 1).split(",");
			isSuccessGet.postValue(true);
			isShowDialog.postValue(false);
		}
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		stringsDate = null;
		stringsContent = null;
		loadingDialogHelper = null;
		isSuccessGet = null;
		isShowDialog = null;
	}
}
