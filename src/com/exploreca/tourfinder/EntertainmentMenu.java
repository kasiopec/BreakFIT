package com.exploreca.tourfinder;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.exploreca.tourfinder.db.DBAdapter;
import com.exploreca.tourfinder.db.EntertainmentDataSource;
import com.exploreca.tourfinder.model.Entertainment;
import com.exploreca.tourfinder.xml.EntertainmentPullParser;

public class EntertainmentMenu extends ListActivity implements
		OnGestureListener, OnClickListener {

	public static final String LOGTAG = "EXPLORECA";
	public static final String USERNAME = "pref_username";
	public static final String VIEWIMAGE = "pref_viewimages";
	private static final int ENTERTAINMENT_DETAIL_ACTIVITY = 1001;

	// For the viewFlipper
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private GestureDetector detector;
	private ViewFlipper view;

	private final String SWIPE_UP_DOWN = "Swipe up or down for exercises.";
	private final String SWIPE_LEFT_RIGHT = "Swipe left or right for gym motivations.";

	// Local countdown variables
	private CountDownTimer countDownTimer;
	private boolean timerHasStarted = false;
	public TextView textCounter;
	private final long startTime = 60 * 1000;
	private final long interval = 1 * 1000;
	DBAdapter db = new DBAdapter(this);

	// VIbrator
	private Vibrator vib;
	// Wake up dialog
	private PowerManager.WakeLock w1;

	private static final int SWIPE_MIN_DISTANCE = 120;
	// private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 120;

	private SharedPreferences settings;
	private OnSharedPreferenceChangeListener listener;
	boolean isMyTours;
	private final Context context = this;
	// List views
	private List<Entertainment> entertainments;

	EntertainmentDataSource datasource;

	// Aleart dialog for Help-menu
	public AlertDialog alertDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Help");
		alert.setMessage(SWIPE_UP_DOWN + "\n" + "\n" + SWIPE_LEFT_RIGHT);
		alert.setNegativeButton("Got it",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		return alert.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entertainment);

		alertDialog();

		// Calling the gesturedetector to add gestures for viewFlipper
		detector = new GestureDetector(this, this);
		view = (ViewFlipper) findViewById(R.id.flipper);
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils
				.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils
				.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.slide_right_out);

		// Coloriing the actionbar blue
		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);

		Button btnStart = (Button) findViewById(R.id.startButton);
		btnStart.setOnClickListener(this);
		countDownTimer = new MyCountDownTimer(startTime, interval);
		textCounter = (TextView) findViewById(R.id.textView3);
		textCounter.setText(textCounter.getText()
				+ String.valueOf(startTime / 1000));

		// Vibration
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// Waking the phone up when vibration starts
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		w1 = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "my tag");

		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		listener = new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				EntertainmentMenu.this.refreshDisplay();
			}
		};
		settings.registerOnSharedPreferenceChangeListener(listener);

		datasource = new EntertainmentDataSource(this);
		datasource.open();

		entertainments = datasource.findAll();
		if (entertainments.size() == 0) {
			createData();
			entertainments = datasource.findAll();
		}
		isMyTours = false;

		refreshDisplay();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entertainment_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;

		case R.id.menu_help:
			alertDialog();
			break;
		//
		// case R.id.menu_mytours:
		// entertainments = datasource.findMyTours();
		// refreshDisplay();
		// isMyTours = true;
		// break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void refreshDisplay() {

		// boolean viewImages = settings.getBoolean(VIEWIMAGE, true);

		ArrayAdapter<Entertainment> adapter = new EntertainmentListAdapter(
				this, entertainments);
		setListAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		if (!timerHasStarted) {
			countDownTimer.start();
			timerHasStarted = true;
			textCounter.setText("STOP");

		} else {
			countDownTimer.cancel();
			timerHasStarted = false;
			textCounter.setText("RESET");

			Toast.makeText(getApplicationContext(), "Reset Break timer",
					Toast.LENGTH_SHORT).show();
		}
	}

	public AlertDialog alertDialogVibrationOff() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Break time is over");
		alert.setMessage("Your break has ended." + "\n"
				+ "Continue your workout!");
		alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				vib.cancel();
			}
		});
		return alert.show();
	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		public void onFinish() {
			textCounter.setText("Time's up!");

			long[] pattern = { 0, 200, 200 };
			vib.vibrate(pattern, 0);
			alertDialogVibrationOff();

		}

		public void onTick(long millisUntilFinished) {
			textCounter.setText("" + millisUntilFinished / 1000);
			// textCounter.setTextColor(Color.rgb(200, 0, 0));

		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		datasource.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
		if (w1.isHeld())
			w1.release();

	}

	protected void onStop() {
		super.onStop();

	}

	private void createData() {
		EntertainmentPullParser parser = new EntertainmentPullParser();
		List<Entertainment> tours = parser.parseXML(this);

		for (Entertainment tour : tours) {
			datasource.create(tour);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Entertainment tour = entertainments.get(position);

		Intent intent = new Intent(this, EntertainmentDetailActivity.class);

		intent.putExtra(".model.Tour", tour);
		intent.putExtra("isMyTours", isMyTours);

		startActivityForResult(intent, ENTERTAINMENT_DETAIL_ACTIVITY);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ENTERTAINMENT_DETAIL_ACTIVITY && resultCode == -1) {
			datasource.open();
			entertainments = datasource.findMyTours();
			refreshDisplay();
			isMyTours = true;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (Math.abs(e1.getY() - e2.getY()) > 250)
			return false;

		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			view.setInAnimation(slideLeftIn);
			view.setOutAnimation(slideLeftOut);
			view.showNext();

			return false;

		}

		else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			view.setInAnimation(slideRightIn);
			view.setOutAnimation(slideRightOut);
			view.showPrevious();
		}
		return false;

	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		return detector.onTouchEvent(event);
	}

}
