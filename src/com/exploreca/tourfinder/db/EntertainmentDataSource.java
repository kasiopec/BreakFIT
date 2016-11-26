package com.exploreca.tourfinder.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.exploreca.tourfinder.model.Entertainment;

public class EntertainmentDataSource {

	public static final String LOGTAG = "EXPLORECA";

	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;

	private static final String[] allColumns = {
			EntertainmentDBOpenHelper.COLUMN_ID,
			EntertainmentDBOpenHelper.COLUMN_TITLE,
			EntertainmentDBOpenHelper.COLUMN_DESC,
			EntertainmentDBOpenHelper.COLUMN_PRICE,
			EntertainmentDBOpenHelper.COLUMN_IMAGE, };

	public EntertainmentDataSource(Context context) {
		dbhelper = new EntertainmentDBOpenHelper(context);
	}

	public void open() {
		Log.i(LOGTAG, "Database opened");
		database = dbhelper.getWritableDatabase();
	}

	public void close() {
		Log.i(LOGTAG, "Database closed");
		dbhelper.close();
	}

	public Entertainment create(Entertainment tour) {
		ContentValues values = new ContentValues();
		values.put(EntertainmentDBOpenHelper.COLUMN_TITLE, tour.getTitle());
		values.put(EntertainmentDBOpenHelper.COLUMN_DESC, tour.getDescription());
		values.put(EntertainmentDBOpenHelper.COLUMN_PRICE, tour.getPrice());
		values.put(EntertainmentDBOpenHelper.COLUMN_IMAGE, tour.getImage());
		long insertid = database.insert(EntertainmentDBOpenHelper.TABLE_TOURS,
				null, values);
		tour.setId(insertid);
		return tour;
	}

	public List<Entertainment> findAll() {

		Cursor cursor = database.query(EntertainmentDBOpenHelper.TABLE_TOURS,
				allColumns, null, null, null, null, null);

		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		List<Entertainment> tours = cursorToList(cursor);
		return tours;
	}

	public List<Entertainment> findFiltered(String selection, String orderBy) {

		Cursor cursor = database.query(EntertainmentDBOpenHelper.TABLE_TOURS,
				allColumns, selection, null, null, null, orderBy);

		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
		List<Entertainment> tours = cursorToList(cursor);
		return tours;
	}

	private List<Entertainment> cursorToList(Cursor cursor) {
		List<Entertainment> tours = new ArrayList<Entertainment>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Entertainment tour = new Entertainment();
				tour.setId(cursor.getLong(cursor
						.getColumnIndex(EntertainmentDBOpenHelper.COLUMN_ID)));
				tour.setTitle(cursor.getString(cursor
						.getColumnIndex(EntertainmentDBOpenHelper.COLUMN_TITLE)));
				tour.setDescription(cursor.getString(cursor
						.getColumnIndex(EntertainmentDBOpenHelper.COLUMN_DESC)));
				tour.setImage(cursor.getString(cursor
						.getColumnIndex(EntertainmentDBOpenHelper.COLUMN_IMAGE)));
				tour.setPrice(cursor.getString(cursor
						.getColumnIndex(EntertainmentDBOpenHelper.COLUMN_PRICE)));
				tours.add(tour);
			}
		}
		return tours;
	}

	public boolean addToMyTours(Entertainment tour) {
		ContentValues values = new ContentValues();
		values.put(EntertainmentDBOpenHelper.COLUMN_ID, tour.getId());
		long result = database.insert(EntertainmentDBOpenHelper.TABLE_MYTOURS,
				null, values);
		return (result != -1);
	}

	public boolean removeFromMyTours(Entertainment tour) {
		String where = EntertainmentDBOpenHelper.COLUMN_ID + "=" + tour.getId();
		int result = database.delete(EntertainmentDBOpenHelper.TABLE_MYTOURS,
				where, null);
		return (result == 1);
	}

	public List<Entertainment> findMyTours() {

		String query = "SELECT tours.* FROM " + "tours JOIN mytours ON "
				+ "tours.tourId = mytours.tourId";
		Cursor cursor = database.rawQuery(query, null);

		Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");

		List<Entertainment> tours = cursorToList(cursor);
		return tours;
	}

}
