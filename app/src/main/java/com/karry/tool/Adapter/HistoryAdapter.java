package com.karry.tool.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
	private final String[] localDataSet;
	private final String[] localdateSet;

	public HistoryAdapter(String[] dateSet, String[] dataSet) {
		localDataSet = dataSet;
		localdateSet = dateSet;
	}

	@NonNull
	@Override
	//返回一个新的 ViewHolder
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_today_item, parent, false);
		return new ViewHolder(view);
	}

	@SuppressLint("SetTextI18n")
	@Override
	//显示一个指定位置的数据
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.getTv_date().setText(localdateSet[position]);
		holder.getTv_item().setText(localDataSet[position]);
	}

	@Override
	//返回数据列表的长度
	public int getItemCount() {
		return localDataSet.length;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final MaterialTextView tv_item, tv_date;

		public ViewHolder(View view) {
			super(view);
			tv_item = view.findViewById(R.id.tv_history_item);
			tv_date = view.findViewById(R.id.tv_history_year);
		}

		public MaterialTextView getTv_item() {
			return tv_item;
		}

		public MaterialTextView getTv_date() {
			return tv_date;
		}
	}
}
