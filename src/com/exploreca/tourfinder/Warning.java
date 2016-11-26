package com.exploreca.tourfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class Warning extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warning);
		
		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);
	}

}
