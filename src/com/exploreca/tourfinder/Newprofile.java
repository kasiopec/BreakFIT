package com.exploreca.tourfinder;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.exploreca.tourfinder.db.DBAdapter;

public class Newprofile extends Activity {
	public void load() {
		db.open();
		Cursor curs = db.getAllEx(selectedItem);
		curs.moveToFirst();
		if (!curs.isAfterLast()) {
			do {
				ex.add(curs.getString(curs.getColumnIndex("name")));
			} while (curs.moveToNext());
		}
		db.close();
	}

	private String ItemToDelete;
	private final Context context = this;
	private ArrayAdapter<String> adapter;
	private String selectedItem;
	DBAdapter db = new DBAdapter(this);
	EntertainmentMenu mn = new EntertainmentMenu();
	ArrayList<String> ex = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profileset);
		selectedItem = getIntent().getExtras().getString("table_name")
				.toString();
		
		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);

		Button but = (Button) findViewById(R.id.button1);
		but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText exName = (EditText) findViewById(R.id.exName);
				EditText sets = (EditText) findViewById(R.id.sets);
				EditText setsTime = (EditText) findViewById(R.id.setsTime);
				db.open();
				if (exName.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"You did not enter a name", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				@SuppressWarnings("unused")
				long id = db.insertRecordToProfile(selectedItem, exName
						.getText().toString(), sets.getText().toString(),
						setsTime.getText().toString());
				ex.add(exName.getText().toString());
				adapter.notifyDataSetChanged();
				exName.setText("");
				sets.setText("");
				setsTime.setText("");
				db.close();

			}
		});
		load();
		ListView lv = (ListView) findViewById(R.id.listView1);
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, ex);
		lv.setAdapter(adapter);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				ItemToDelete = parent.getItemAtPosition(position).toString();

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Do you want delete: " + ItemToDelete + "?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								adapter.remove(ItemToDelete);
								adapter.notifyDataSetChanged();
								db.open();
								db.deleteRecord(selectedItem, ItemToDelete);
								db.close();
								ex.remove(ItemToDelete);
								// update();

								Toast.makeText(getApplicationContext(),
										ItemToDelete + " Deleted.",
										Toast.LENGTH_SHORT).show();
							}
						});

				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				builder.show();

				return true;

			}

		});
		lv.setAdapter(adapter);
	}

}
