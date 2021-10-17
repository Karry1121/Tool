package com.karry.tool.Activity.GenerateQRCode;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.karry.tool.Bean.QRCodeBean;
import com.karry.tool.Bean.SettingsBean;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.NetworkHelper;
import com.karry.tool.Helper.OtherHelper;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QRCodeViewModel extends AndroidViewModel {
	private static final int PIC_RESULT = 1;
	Bitmap bitmap_qrcode;
	MutableLiveData<Boolean> isSuccessGetPic, isShowDialog;
	LoadingDialogHelper loadingDialogHelper;
	OkHttpClient okHttpClient;
	Handler handler = new Handler(message -> {
		if (message.what == PIC_RESULT) {
			bitmap_qrcode = BitmapFactory.decodeByteArray(
					OtherHelper.decryptBase64((String) message.obj), 0,
					((OtherHelper.decryptBase64((String) message.obj)).length));
			isSuccessGetPic.postValue(true);
		}
		return false;
	});

	public QRCodeViewModel(Application application) {
		super(application);
		isSuccessGetPic = new MutableLiveData<>();
		isShowDialog = new MutableLiveData<>();
	}

	public void getPicture(String content) {
		bitmap_qrcode = null;
		isShowDialog.postValue(true);
		if (okHttpClient == null) {
			okHttpClient = NetworkHelper.getOkHttpClient(getApplication());
		}
		Request request = new Request.Builder().url("https://apis.juhe.cn/qrcode/api?key="
				+ SettingsBean.API_KEY_QRCODE
				+ "&w=" + SettingsBean.QRCodeSize
				+ "&text=" + content).method("GET", null).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(@NonNull Call call, @NonNull IOException e) {
				isSuccessGetPic.postValue(false);
				isShowDialog.postValue(false);
			}

			@Override
			public void onResponse(@NonNull Call call, @NonNull Response response) {
				isShowDialog.postValue(false);
				Message message = handler.obtainMessage();
				if (response.isSuccessful()) {
					message.what = PIC_RESULT;
					try {
						message.obj = Objects.requireNonNull(response.body()).string();
						QRCodeBean qrCodeBean = new Gson().fromJson((String) message.obj, QRCodeBean.class);
						message.obj = qrCodeBean.getResult().getBase64_image();
						handler.sendMessage(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					handler.sendEmptyMessage(0);
				}
			}
		});
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		handler = null;
		bitmap_qrcode = null;
		isSuccessGetPic = null;
		isShowDialog = null;
		okHttpClient = null;
		loadingDialogHelper = null;
	}
}
