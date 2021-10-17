package com.karry.tool.Helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.karry.tool.R;

public class LoadingDialogHelper extends Dialog {
	MaterialTextView tv_content;
	ShapeableImageView iv_icon;

	public LoadingDialogHelper(Context context) {
		this(context, R.style.loading_dialog, "努力加载中...");
	}

	public LoadingDialogHelper(Context context, String s) {
		this(context, R.style.loading_dialog, s);
	}

	protected LoadingDialogHelper(Context context, int theme, String s) {
		super(context, theme);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.loading_dialog_layout);
		tv_content = findViewById(R.id.tv_loading_dialog);
		tv_content.setText(s);
		iv_icon = findViewById(R.id.iv_loading_dialog);
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		iv_icon.startAnimation(animation);

		getWindow().getAttributes().gravity = Gravity.CENTER;
		getWindow().getAttributes().dimAmount = 0.5f;
	}
}
