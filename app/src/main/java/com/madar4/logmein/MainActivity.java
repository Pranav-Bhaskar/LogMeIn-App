package com.madar4.logmein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	
	SharedPreferences sharedPreferences;
	TextView username;
	TextView password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		username = (TextView) findViewById(R.id.username);
		password = (TextView) findViewById(R.id.password);
		sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
		updateView();
	}
	
	public void login(View view){
		String user = sharedPreferences.getString("Username", null);
		if(user == null) {
			Toast.makeText(this, "Username Empty", Toast.LENGTH_LONG).show();
			return;
		}
		String pass = sharedPreferences.getString("Password", null);
		if(pass == null) {
			Toast.makeText(this, "Password Empty", Toast.LENGTH_LONG).show();
			return;
		}
		Login login = new Login(this, user, pass);
		login.execute();
	}
	
	public void changeUsername(View view){
		final EditText editText = new EditText(this);
		editText.setHint(sharedPreferences.getString("Username", "---Set Username---"));
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Username");
		alertDialog.setMessage("Enter Username :: ");
		alertDialog.setView(editText);
		alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				String nUsername = editText.getText().toString();
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("Username", nUsername);
				editor.apply();
				if(editor.commit())
					Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
				updateView();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_LONG).show();
			}
		});
		alertDialog.show();
	}
	
	public void changePassword(View view){
		final EditText editText = new EditText(this);
		editText.setHint(sharedPreferences.getString("Password", "---Set Password---"));
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Password");
		alertDialog.setMessage("Enter Password :: ");
		alertDialog.setView(editText);
		alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				String nPassword = editText.getText().toString();
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("Password", nPassword);
				if(editor.commit())
					Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
				updateView();
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_LONG).show();
				updateView();
			}
		});
		alertDialog.show();
	}
	
	private void updateView(){
		username.setText(sharedPreferences.getString("Username", "---Set Username---"));
		password.setText(sharedPreferences.getString("Password", "---Set Password---"));
	}
}
