package com.karry.tool.Activity.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.karry.tool.Activity.GenerateQRCode.GenerateQRCodeActivity;
import com.karry.tool.Activity.HistoryToday.HistoryTodayActivity;
import com.karry.tool.Activity.OilPrice.OilPriceActivity;
import com.karry.tool.Activity.PhoneNumber.QueryPhoneNumberActivity;
import com.karry.tool.Activity.TodayPoetry.TodayPoetryActivity;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment {
	public static final int REQUEST_CODE_SCAN = 0x01;
	ActivityResultLauncher<String[]> launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
			result -> {
				if (result.get(Manifest.permission.CAMERA) != null
						&& result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
					if (Objects.requireNonNull(result.get(Manifest.permission.CAMERA)).equals(true)
							&& Objects.requireNonNull(result.get(Manifest.permission.READ_EXTERNAL_STORAGE)).equals(true)) {
						ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN,
								new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
					} else {
						ToastHelper.showToast(requireActivity(), "扫码相关权限没有授予，请检查");
					}
				}
			});
	private FragmentHomeBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final MaterialButton btn_goTodayPoetry = binding.btnGoTodayPoetry,
				btn_goHistoryDay = binding.btnGoHistoryDay,
				btn_goQueryPhoneNumber = binding.btnGoQueryPhoneNumber,
				btn_goScanQRCode = binding.btnGoScanQRCode,
				btn_goQueryOilPrice = binding.btnGoQueryOilPrice,
				btn_GenerateQRCode = binding.btnGenerateQRCode;

		btn_goTodayPoetry.setOnClickListener(view -> startActivity(new Intent(getActivity(), TodayPoetryActivity.class)));
		btn_goHistoryDay.setOnClickListener(view -> startActivity(new Intent(getActivity(), HistoryTodayActivity.class)));
		btn_goQueryOilPrice.setOnClickListener(view -> startActivity(new Intent(getActivity(), OilPriceActivity.class)));
		btn_goQueryPhoneNumber.setOnClickListener(view -> startActivity(new Intent(getActivity(), QueryPhoneNumberActivity.class)));
		btn_GenerateQRCode.setOnClickListener(view -> startActivity(new Intent(getActivity(), GenerateQRCodeActivity.class)));
		btn_goScanQRCode.setOnClickListener(view -> launcher.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}));
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}