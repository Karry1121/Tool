package com.karry.tool.Activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.Activity.ScanHistoryActivity;
import com.karry.tool.Activity.SettingsActivity;
import com.karry.tool.Helper.LoadingDialogHelper;
import com.karry.tool.Helper.StorageHelper;
import com.karry.tool.Helper.ToastHelper;
import com.karry.tool.R;
import com.karry.tool.databinding.FragmentMineBinding;

public class MineFragment extends Fragment {
	private FragmentMineBinding binding;
	private MaterialTextView tv_show_cache_size;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentMineBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		tv_show_cache_size = binding.tvShowCacheSize;
		tv_show_cache_size.setText(StorageHelper.getTotalCacheSize(requireActivity()));
		final MaterialButton btn_clearCache = binding.btnClearCache;
		final MaterialButton btn_go_scan_history = binding.btnGoScanHistory;
		btn_clearCache.setOnClickListener(view -> clearCache());
		btn_go_scan_history.setOnClickListener(view ->
				startActivity(new Intent(requireActivity(), ScanHistoryActivity.class)));

		final MaterialToolbar toolbar = binding.toolbarMine;
		toolbar.setOnMenuItemClickListener(item -> {
			if (item.getItemId() == R.id.menu_settings) {
				startActivity(new Intent(requireActivity(), SettingsActivity.class));
			}
			return true;
		});
		return root;
	}

	private void clearCache() {
		LoadingDialogHelper loadingDialogHelper = new LoadingDialogHelper(getActivity(), "正在清理…");
		loadingDialogHelper.show();
		if (StorageHelper.clearAllCache(requireActivity())) {
			ToastHelper.showToast(requireActivity(), "清理成功");
		} else {
			ToastHelper.showToast(requireActivity(), "清理失败");
		}
		loadingDialogHelper.dismiss();
		tv_show_cache_size.setText(StorageHelper.getTotalCacheSize(requireActivity()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}