package ru.mamaevalexey.capitals;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MenuActivity extends Activity {

	boolean first;
	public String MenuName = "Новый игрок";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		first = true;
		SharedPreferences shared = this.getPreferences(0);
		RelativeLayout layout =(RelativeLayout)findViewById(R.id.MenuLayout);
        layout.setBackgroundResource(R.drawable.original);
        if(shared!=null)first = shared.getBoolean("f", true);
        if(!first){
        	MenuName = getIntent().getExtras().getString("Name");
        	first = true;
        }
	}

	@Override
    protected void onPause() {
        super.onPause();
        SharedPreferences shared = this.getPreferences(0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("f", first);
        editor.commit();
    }
	
	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	

	/*@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences shared = this.getPreferences(0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean("f", true);
        editor.commit();
	}*/

	public void buttonClicked(View view){
		int buttonId = view.getId();
		if(buttonId != R.id.MenuSButton)first = true;
		if(buttonId == R.id.MenuNGButton){
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("CH_ALL", true);
			i.putExtra("Name", MenuName);
            startActivity(i);
		}
		else if(buttonId == R.id.MenuSGButton){
			Intent i = new Intent(this, CheckBoxActivity.class);
			i.putExtra("Name", MenuName);
            startActivity(i);
		}
		else if(buttonId == R.id.MenuSButton){
			Intent i = new Intent(this, SettingsActivity.class);
			i.putExtra("Name", MenuName);
            first = false;
			startActivity(i);
		}
		else if(buttonId == R.id.MenuHoFButton){
			Intent i = new Intent(this, HoFActivity.class);
            startActivity(i);
		}
		
	}
	
}
