package com.exploreca.tourfinder;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);
	}
}
