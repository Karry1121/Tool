package com.karry.tool.Activity.GenerateQRCode;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.OtherHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class GenerateQRCodeActivity extends AppCompatActivity {
	String NAME_INPUT = "input_data";
	ShapeableImageView iv_qrcode;
	MaterialButton btn_save_qrcode, btn_generate, btn_clear_qrcode;
	TextInputEditText et_qrcode_content;
	QRCodeViewModel qrCodeViewModel;
	MaterialToolbar toolbar;
	ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
			result -> {
				if (result.equals(true)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						savePicture_10();
					} else {
						savePicture();
					}
				} else {
					ToastHelper.showToast(GenerateQRCodeActivity.this, "缺少保存的权限，请检查");
				}
			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_qrcode);

		//设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明。
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//状态栏根据主题颜色变色，浅色模式下不会看不清
		getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		initView();

		qrCodeViewModel = new ViewModelProvider(this,
				(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(QRCodeViewModel.class);
		if (qrCodeViewModel.loadingDialogHelper == null) {
			qrCodeViewModel.loadingDialogHelper = new LoadingDialogHelper(this, "正在生成……");
		}
		qrCodeViewModel.isSuccessGetPic.observe(this, b -> {
			if (b && qrCodeViewModel.bitmap_qrcode != null) {
				iv_qrcode.setImageBitmap(qrCodeViewModel.bitmap_qrcode);
			} else if (!b) {
				ToastHelper.showToast(this, "获取失败，稍后再试试");
			}
		});
		qrCodeViewModel.isShowDialog.observe(this, b -> {
			if (b) {
				qrCodeViewModel.loadingDialogHelper.show();
			} else {
				qrCodeViewModel.loadingDialogHelper.dismiss();
			}
		});

		//输入框数据恢复
		if (savedInstanceState != null) {
			et_qrcode_content.setText(savedInstanceState.getString(NAME_INPUT));
		}

		btn_generate.setOnClickListener(view -> {
			if (Objects.requireNonNull(et_qrcode_content.getText()).toString().equals("")) {
				ToastHelper.showToast(GenerateQRCodeActivity.this, "输入内容不能为空哦~");
				return;
			}
			qrCodeViewModel.getPicture(et_qrcode_content.getText().toString());
		});
		btn_save_qrcode.setOnClickListener(view -> {
			if (iv_qrcode.getDrawable() == null || qrCodeViewModel.bitmap_qrcode == null) {
				ToastHelper.showToast(GenerateQRCodeActivity.this, "没有图片能保存");
				return;
			}
			launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
		});
		btn_clear_qrcode.setOnClickListener(view -> {
			if (iv_qrcode.getDrawable() == null || qrCodeViewModel.bitmap_qrcode == null) {
				ToastHelper.showToast(GenerateQRCodeActivity.this, "无需清除图片~");
				return;
			}
			iv_qrcode.setImageDrawable(null);
		});
	}

	private void initView() {
		et_qrcode_content = findViewById(R.id.et_qrcode_content);
		btn_generate = findViewById(R.id.btn_generate);
		btn_save_qrcode = findViewById(R.id.btn_save_qrcode);
		btn_clear_qrcode = findViewById(R.id.btn_clear_qrcode);
		iv_qrcode = findViewById(R.id.iv_qrcode);
		toolbar = findViewById(R.id.toolbar_qrcode);
		toolbar.setNavigationOnClickListener(view -> GenerateQRCodeActivity.this.finish());
	}

	private void savePicture() {
		//将ImageView中的图片转换成Bitmap
		iv_qrcode.buildDrawingCache();
		//将bitmap转换成二进制，写入本地
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		qrCodeViewModel.bitmap_qrcode.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bytes = stream.toByteArray();
		File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tool");
		if (!folder.exists()) {
			folder.mkdir();
		}

		File file = new File(folder, OtherHelper.getCurrentDate("yyyyMMddHHmmss") + ".jpg");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bytes, 0, bytes.length);
			fileOutputStream.flush();
			//用广播通知相册进行更新相册
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			sendBroadcast(intent);
			ToastHelper.showToast(GenerateQRCodeActivity.this, "保存成功~");
		} catch (IOException e) {
			ToastHelper.showToast(GenerateQRCodeActivity.this, "保存失败~");
			e.printStackTrace();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	private void savePicture_10() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Tool");
		contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,
				"Tool_" + OtherHelper.getCurrentDate("yyyyMMddHHmmss") + ".jpg");
		contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
		contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
		contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000);
		contentValues.put(MediaStore.MediaColumns.DATE_EXPIRES, (System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS) / 1000);
		contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);

		ContentResolver contentResolver = getContentResolver();
		Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		try {
			try (OutputStream outputStream = contentResolver.openOutputStream(uri)) {
				if (!qrCodeViewModel.bitmap_qrcode.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
					throw new IOException();
				}
			} catch (IOException e) {
				ToastHelper.showToast(GenerateQRCodeActivity.this, "保存失败");
				e.printStackTrace();
			}
			contentValues.clear();
			contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
			contentValues.putNull(MediaStore.MediaColumns.DATE_EXPIRES);
			contentResolver.update(uri, contentValues, null, null);
			ToastHelper.showToast(GenerateQRCodeActivity.this, "保存成功");
		} catch (Exception e) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				contentResolver.delete(uri, null);
			}
			ToastHelper.showToast(GenerateQRCodeActivity.this, "保存失败");
			e.printStackTrace();
		}
	}

	/**
	 * 销毁前进行数据保存
	 *
	 * @param outState 数据
	 */
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putString(NAME_INPUT, Objects.requireNonNull(et_qrcode_content.getText()).toString());
		super.onSaveInstanceState(outState);
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