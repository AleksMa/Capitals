package ru.mamaevalexey.capitals;
import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private Context context;
	private String DB_NAME = "game.db";

	private SQLiteDatabase db;

	private static DBManager dbManager;

	public static DBManager getInstance(Context context) {
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	private DBManager(Context context) {
		this.context = context;
		db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		createTablesIfNeedBe(); 
	}

	void addResult(String username, int score) {
		db.execSQL("INSERT INTO RESULTS VALUES ('" + username + "', " + score
				+ ");");
	}
	
	void deleteALL() {
		db.execSQL("DELETE FROM RESULTS;");
	}
	
	void deleteForName(String username) {
		db.execSQL("DELETE FROM RESULTS VALUES ('"+username+");");
	}

	ArrayList<Result> getAllResults() {

		ArrayList<Result> data = new ArrayList<Result>();
		Cursor cursor = db.rawQuery("SELECT * FROM RESULTS ORDER BY SCORE DESC;", null);
		boolean hasMoreData = cursor.moveToFirst();

		while (hasMoreData) {
			String name = cursor.getString(cursor.getColumnIndex("USERNAME"));
			int score = Integer.parseInt(cursor.getString(cursor
					.getColumnIndex("SCORE")));
			data.add(new Result(name, score));
			hasMoreData = cursor.moveToNext();
		}

		return data;
	}

	private void createTablesIfNeedBe() {
		db.execSQL("CREATE TABLE IF NOT EXISTS RESULTS (USERNAME TEXT, SCORE INTEGER);");
	}

	private boolean dbExist() {
		File dbFile = context.getDatabasePath(DB_NAME);
		return dbFile.exists();
	}

}