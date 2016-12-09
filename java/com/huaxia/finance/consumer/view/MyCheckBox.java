package com.huaxia.finance.consumer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.huaxia.finance.consumer.R;

public class MyCheckBox extends ImageButton{

	public MyCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyCheckBox(Context context) {
		super(context);
		init();
	}

	public boolean toogle = false;
	
	private void init() {
		MyCheckBox.this.setBackgroundResource(R.drawable.agree_default);
		this.post(new Runnable() {
			
			@Override
			public void run() {
				MyCheckBox.this.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (toogle) {
							MyCheckBox.this.setBackgroundResource(R.drawable.agree_default);
							toogle = false;
						}else {
							MyCheckBox.this.setBackgroundResource(R.drawable.agree_icon);
							toogle = true;
						}
					}
				});
			}
		});
		
	}

}
