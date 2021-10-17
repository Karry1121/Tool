package com.karry.tool.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

public class OilPriceAdapter extends RecyclerView.Adapter<OilPriceAdapter.ViewHolder> {
	private final String[] strings_oil, strings_oil_price;

	public OilPriceAdapter(String[] data1, String[] data2) {
		strings_oil = data1;
		strings_oil_price = data2;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.oil_price_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull OilPriceAdapter.ViewHolder holder, int position) {
		holder.getTv_oil().setText(strings_oil[position]);
		holder.getTv_oil_price().setText(strings_oil_price[position]);
		holder.getTv_oil_price_unit().setText("元/L");
	}

	@Override
	public int getItemCount() {
		return strings_oil.length;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		MaterialTextView tv_oil, tv_oil_price, tv_oil_price_unit;

		public ViewHolder(View view) {
			super(view);
			tv_oil = view.findViewById(R.id.tv_oil);
			tv_oil_price = view.findViewById(R.id.tv_oil_price);
			tv_oil_price_unit = view.findViewById(R.id.tv_oil_price_unit);
		}

		public MaterialTextView getTv_oil() {
			return tv_oil;
		}

		public MaterialTextView getTv_oil_price() {
			return tv_oil_price;
		}

		public MaterialTextView getTv_oil_price_unit() {
			return tv_oil_price_unit;
		}
	}
}
