package com.sjourcecode.billshare;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sjourcecode.billshare.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	ArrayAdapter<String> adapter;
	List<PersonRecord> persons;
	ArrayList<String> names = new ArrayList<String>();
	PersonDatabase personDatabase = new PersonDatabase(MainActivity.this);

	@Override
	protected void onCreate(Bundle thisBundle) {
		super.onCreate(thisBundle);
		setContentView(R.layout.activity_main);
		
		persons = personDatabase.getAllPersons();
		for (int i = 0; i<persons.size(); i++)
		{
			String balance = String.format(Locale.US, "%3d.%02d", persons.get(i).getBalance()/100, persons.get(i).getBalance()%100);
			names.add(balance + "  " + persons.get(i).getName());
		}
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		personDatabase.deletePerson(persons.get(position));
		names.remove(position);
		persons.remove(position);
		
		adapter.notifyDataSetChanged(); // Updates adapter to new changes

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.add_person:
			doLaunchContactPicker();
			return true;

		case R.id.action_settings:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// / Called when the user clicks the Add button
	public void AddPerson(View view) {
		EditText editText = (EditText) findViewById(R.id.eTName);
		String name = editText.getText().toString();
		names.add(name);					// Add name to list used for the graphics
		personDatabase.createPerson(name);	// Add name to SQLite
		adapter.notifyDataSetChanged(); 	// Updates adapter to new changes

		// Remove software keyboard
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) { // check if no view has focus:
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private static final int CONTACT_PICKER_RESULT = 1001;

	public void doLaunchContactPicker() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    // Process result of Contact picker.
	    if(requestCode==CONTACT_PICKER_RESULT) {
           if(resultCode==ListActivity.RESULT_OK) {
               Uri contactData = data.getData();
               Cursor c = getContentResolver().query(contactData, null, null, null, null);
                // has result
                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    
//                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                Toast.makeText(MainActivity.this, name + " is added", Toast.LENGTH_LONG).show();
				names.add(name);
				personDatabase.createPerson(name);	// Add name to SQLite
				adapter.notifyDataSetChanged(); 	// Updates adapter to new changes

                }
            }
        }
	}
}
