package com.exploreca.tourfinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.exploreca.tourfinder.db.DBAdapter;

public class MainActivity extends Activity {
	DBAdapter db = new DBAdapter(this);
	ArrayList<String> prof = new ArrayList<String>();
	public String selectedItem;
	public String selectedItem2;
	private final Context context = this;
	private ArrayAdapter<String> adapter;
	private ListView list;
	
	private final String TITLE_PROFILE = "Edit your profile?";
	private final String MGS_PROCEED = "Select 'edit' to edit your profile." + "\n" + "Or 'next' to proceed.";
//	private final String MGS_PROCEED = "Proceed to the next menu, or edit your profile?";

	// private List<Entertainment> entertainments;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar bar = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#15317E"));
		bar.setBackgroundDrawable(colorDrawable);

		try {
			String destPath = "/data/data" + getPackageName()
					+ "/database/MyDB";
			File f = new File(destPath);
			if (!f.exists()) {
				CopyDB(getBaseContext().getAssets().open("mydb"),
						new FileOutputStream(destPath));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		db.open();
		Cursor curs = db.getAllRecords();
		curs.moveToFirst();
		if (!curs.isAfterLast()) {
			do {
				prof.add(curs.getString(curs.getColumnIndex("name")));
			} while (curs.moveToNext());
		}
		db.close();

		list = (ListView) findViewById(android.R.id.list);
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, prof);

		list.setOnItemClickListener(new OnItemClickListener() {

			// Long click on the an item in listView for choosing the profile -
			// editing
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				AlertDialog.Builder profileDialog = new AlertDialog.Builder(
						context);
				selectedItem = parent.getItemAtPosition(position).toString();
				db.open();
				db.createTable(selectedItem);
				db.close();
				profileDialog.setTitle(TITLE_PROFILE);
				profileDialog.setMessage(MGS_PROCEED);

				profileDialog.setPositiveButton("Next",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent(MainActivity.this,
										EntertainmentMenu.class);
								intent.putExtra("table_name", selectedItem);
								startActivity(intent);
							}
						});
				// DOESN'T WORK
				profileDialog.setNeutralButton("Edit",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Intent i = new Intent(MainActivity.this,
										Newprofile.class);
								i.putExtra("table_name", selectedItem);
								startActivity(i);
							}

						});
				profileDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
				profileDialog.show();
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {

				selectedItem = parent.getItemAtPosition(position).toString();
				db.open();
				db.createTable(selectedItem);
				db.close();
				Intent i = new Intent(MainActivity.this, Newprofile.class);
				i.putExtra("table_name", selectedItem);
				startActivity(i);
				return true;

			}
		});

		list.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.profile_delete:
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Delete your profile?");
			alert.setMessage("Enter your profile name");
			final EditText input = new EditText(context);
			alert.setView(input);
			alert.setPositiveButton("Delete",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Editable value = input.getText();
							db.open();
							if (value.toString().matches("")) {
								Toast.makeText(getApplicationContext(),
										"You did not enter a name",
										Toast.LENGTH_SHORT).show();
								return;
							}
							db.deleteRecord("Profiles", value.toString());
							db.dropTable(value.toString());
							db.close();
							prof.remove(value.toString());
							adapter.notifyDataSetChanged();

							Toast.makeText(getApplicationContext(),
									value.toString() + " Deleted.",
									Toast.LENGTH_SHORT).show();
						}
					});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert.show();

			return true;
		case R.id.profile_create_new:
			AlertDialog.Builder alert2 = new AlertDialog.Builder(context);
			alert2.setTitle("Your new profile");
			alert2.setMessage("Enter your profile name");
			final EditText input2 = new EditText(context);
			alert2.setView(input2);
			alert2.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Editable value = input2.getText();
							db.open();
							if (value.toString().matches("")) {
								Toast.makeText(getApplicationContext(),
										"You did not enter a name",
										Toast.LENGTH_SHORT).show();
								return;
							}
							// long id = db.insertRecord(value.toString());
							db.close();
							prof.add(value.toString());
							adapter.notifyDataSetChanged();
						}
					});
			alert2.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});
			alert2.show();

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void CopyDB(InputStream inputStream, OutputStream outputStream)
			throws IOException {
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_create_new, menu);
		return true;
	}

}
