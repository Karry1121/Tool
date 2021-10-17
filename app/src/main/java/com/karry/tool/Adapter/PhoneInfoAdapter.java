package com.karry.tool.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

public class PhoneInfoAdapter extends RecyclerView.Adapter<PhoneInfoAdapter.ViewHolder> {
	private final String[] detail_1, detail_2;

	public PhoneInfoAdapter(String[] data1, String[] data2) {
		detail_1 = data1;
		detail_2 = data2;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_detail_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.getTv_detail_1().setText(detail_1[position]);
		holder.getTv_detail_2().setText(detail_2[position]);
	}

	@Override
	public int getItemCount() {
		return detail_1.length;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final MaterialTextView tv_detail_1, tv_detail_2;

		public ViewHolder(View view) {
			super(view);
			tv_detail_1 = view.findViewById(R.id.tv_detail_1);
			tv_detail_2 = view.findViewById(R.id.tv_detail_2);
		}

		public MaterialTextView getTv_detail_1() {
			return tv_detail_1;
		}

		public MaterialTextView getTv_detail_2() {
			return tv_detail_2;
		}
	}
}
