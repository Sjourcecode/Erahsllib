package com.sjourcecode.billshare;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PersonDatabase {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_BALANCE };

	public PersonDatabase(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public PersonRecord createPerson(String person) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, person);
		long insertId = database.insert(MySQLiteHelper.TABLE_PERSONS, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		PersonRecord newPerson = cursorToPersonRecord(cursor);
		cursor.close();
		return newPerson;
	}

	public void deletePerson(PersonRecord person) {
		long id = person.getId();
		System.out.println("Person deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_PERSONS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<PersonRecord> getAllPersons() {
		List<PersonRecord> persons = new ArrayList<PersonRecord>();

		open();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PERSONS, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PersonRecord person = cursorToPersonRecord(cursor);
			persons.add(person);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return persons;
	}

	private PersonRecord cursorToPersonRecord(Cursor cursor) {
		PersonRecord person = new PersonRecord();
		person.setId(cursor.getLong(0));
		person.setPerson(cursor.getString(1));
		return person;
	}
}
