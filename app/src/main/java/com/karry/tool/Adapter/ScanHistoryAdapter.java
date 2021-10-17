package com.karry.tool.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

import java.util.ArrayList;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {
	private final ArrayList<String> arrayList;
	private OnItemClickListener listener;

	public ScanHistoryAdapter(ArrayList<String> dataset) {
		arrayList = dataset;
	}

	@NonNull
	@Override
	public ScanHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_history_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ScanHistoryAdapter.ViewHolder holder, int position) {
		holder.getTextView().setText(arrayList.get(position));
		holder.textView.setOnClickListener(view -> {
			if (listener != null) {
				listener.onItemClick(holder.getAdapterPosition());
			}
		});
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public int getItemCount() {
		return arrayList.size();
	}

	public interface OnItemClickListener {
		void onItemClick(int position);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final MaterialTextView textView;

		public ViewHolder(View view) {
			super(view);
			textView = view.findViewById(R.id.tv_scan_history_item);
		}

		public MaterialTextView getTextView() {
			return textView;
		}
	}
}
