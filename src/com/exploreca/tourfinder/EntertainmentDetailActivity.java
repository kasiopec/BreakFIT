package com.exploreca.tourfinder;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exploreca.tourfinder.db.EntertainmentDataSource;
import com.exploreca.tourfinder.model.Entertainment;

public class EntertainmentDetailActivity extends Activity {

	private static final String LOGTAG = "EXPLORECA";

	List<Entertainment> entertainments;
	Entertainment entertainment;
	EntertainmentDataSource datasource;
	Context context;
	boolean isMyTours;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_detail);

		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);

		Bundle b = getIntent().getExtras();
		entertainment = b.getParcelable(".model.Tour");
		isMyTours = b.getBoolean("isMyTours");

		refreshDisplay();

		datasource = new EntertainmentDataSource(this);
	}

	private void refreshDisplay() {

		// Title
		TextView tv = (TextView) findViewById(R.id.titleText);
		tv.setText(entertainment.getTitle());

		// ImageTitle
		ImageView iv_title_image = (ImageView) findViewById(R.id.imageView1);
		int imageResource = getResources().getIdentifier(
				entertainment.getImage(), "drawable", getPackageName());
		if (imageResource != 0) {
			iv_title_image.setImageResource(imageResource);
		}
		// Description
		tv = (TextView) findViewById(R.id.descText);
		tv.setText(entertainment.getDescription());

		tv = (TextView) findViewById(R.id.textView_main_muscle);
		tv.setText(entertainment.getPrice());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tour_detail, menu);

		// Show delete menu item if we came from My Tours
		menu.findItem(R.id.menu_delete).setVisible(isMyTours);

		// Show add menu+-------------- item if we didn't come from My Tours
		menu.findItem(R.id.menu_add).setVisible(!isMyTours);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Toast.makeText(this, "Added to your Favorits.", Toast.LENGTH_SHORT)
					.show();
			if (datasource.addToMyTours(entertainment)) {
				Log.i(LOGTAG, "Tour added");
				finish();
			} else {
				Log.i(LOGTAG, "Tour not added");
			}
			break;
		case R.id.menu_delete:
			Toast.makeText(this, "Deleted from your Favorits.",
					Toast.LENGTH_SHORT).show();
			if (datasource.removeFromMyTours(entertainment)) {
				setResult(-1);
				finish();
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}

}
