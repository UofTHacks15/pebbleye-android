package com.uofthack.pebbleye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MainActivity extends Activity {

    ContactsAdapter adapter = null;
    ListView listview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        setListView();
    }

    public void doSettings(View view) {
        if (view.getId() == R.id.settings_button) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    public void doAddContact(View view) {
        if (view.getId() == R.id.add_button) {
            LayoutInflater inflater = this.getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title);
            final View dialogView = inflater.inflate(R.layout.add_contact_dialog, null);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = ((EditText) dialogView.findViewById(R.id.contact_name)).getText().toString();
                    String number = ((EditText) dialogView.findViewById(R.id.contact_number)).getText().toString();
                    if (!name.equals("") && !number.equals("")) {
                        //SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        int count = sharedPref.getAll().size() + 1;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(count + "%" + name, number);
                        editor.apply();
                        setListView();
                    } else {
                        Log.v("ERROR", "Name or number field empty.");
                    }
                }

            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void setListView() {
        listview = (ListView) findViewById(R.id.list);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPref.getAll();
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            list.add(entry.getKey() + "%" + entry.getValue().toString());
        }
        Collections.sort(list);
        String[] values = new String[list.size()];
        values = list.toArray(values);

        adapter = new ContactsAdapter(MainActivity.this, values);
        listview.setAdapter(adapter);
    }

    public class ContactsAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public ContactsAdapter(Context context, String[] values) {
            super(context, R.layout.row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row, parent, false);
            TextView name = (TextView) rowView.findViewById(R.id.contact_name);
            TextView number = (TextView) rowView.findViewById(R.id.contact_number);

            Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/paratype_pt-sans_narrow.ttf");
            name.setTypeface(custom_font);
            number.setTypeface(custom_font);

            ImageView deleteButton = (ImageView) rowView.findViewById(R.id.delete_button);

            name.setText(values[position].split("%")[1]);               // the 0th element is count
            number.setText(values[position].split("%")[2]);

            final int pos = position;
            final SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.delete_dialog_title);
                    builder.setMessage(R.string.delete_dialog_message);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(values[pos].split("%")[0] + "%" + values[pos].split("%")[1]);
                            editor.apply();
                            setListView();
                        }

                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            return rowView;
        }
    }

}