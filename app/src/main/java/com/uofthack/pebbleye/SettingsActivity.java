package com.uofthack.pebbleye;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_settings);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        setThings();
    }

    public void doSettingsChanged(View view) {
        if (view.getId() == R.id.settings_submit_button) {
            String message = ((EditText) findViewById(R.id.emergency_message)).getText().toString();
            RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
            int id = rg.getCheckedRadioButtonId();

            final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key_settings), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("message", message);
            editor.putInt("disability", id);
            editor.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.submit_dialog_title);
            builder.setMessage(R.string.submit_dialog_message);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    final SharedPreferences.Editor editor = sharedPref.edit();
                    setThings();
                    EditText current_message = (EditText) findViewById(R.id.emergency_message);
                    current_message.setText("");
                }

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void setThings() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key_settings), Context.MODE_PRIVATE);TextView current_message = (TextView) findViewById(R.id.current_message);
        String message = sharedPref.getString("message", "");
        current_message.setText(message.equals("") ? "" : message);

        int id = sharedPref.getInt("disability", -1);
        if (id != -1) {
            RadioButton rb = (RadioButton) findViewById(id);
            rb.setChecked(true);
        }
    }

}