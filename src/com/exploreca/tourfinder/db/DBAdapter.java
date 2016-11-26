package com.exploreca.tourfinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	public static final String KEY_ROWID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_SETS = "sets";
	public static final String KEY_TIME = "time";
	public static final String KEY_BREAK = "break";
	
	private static final String DATABASE_NAME = "MyDB";
	private static final String DATABASE_TABLE = "Profiles";
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " 
	+ DATABASE_TABLE 
	+ "(" + KEY_ROWID 
		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		+ KEY_NAME 
		+ " TEXT NOT NULL);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdapter(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
			}
	public DBAdapter open() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	public  void createTable(String s) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + s 
				+ "(" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT NOT NULL, " + KEY_SETS + " TEXT NOT NULL, " + KEY_TIME + " TEXT NOT NULL);");
	}
	public void dropTable(String s){
		db.execSQL("DROP TABLE IF EXISTS " + s);
	}
	public void close(){
		DBHelper.close();	
	}
	public long insertRecordToProfile(String s, String name, String sets, String time){
		ContentValues initValues = new ContentValues();
		initValues.put(KEY_NAME, name);
		initValues.put(KEY_SETS, sets);
		initValues.put(KEY_TIME, time);
		return db.insert(s,null,initValues);
	}
	public long insertRecord(String name){
		ContentValues initValues = new ContentValues();
		initValues.put(KEY_NAME, name);
		
		return db.insert(DATABASE_TABLE, null, initValues);
		
	}
	public Cursor getAllEx(String s){
		return db.query(s, new String[]{KEY_ROWID,KEY_NAME},null,null, null, null, null);
		
	}
	public int deleteRecord (String s, String d){
		return db.delete(s, KEY_NAME + "= '" + d + "'", null);
		
	}
	
	public Cursor getAllRecords(){
		return db.query(DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME},null,null, null, null, null);
		
	}
	
}
