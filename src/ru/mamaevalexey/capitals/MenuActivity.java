package ru.mamaevalexey.capitals;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {

	
	public String MenuName = "Новый игрок";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
	
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
	
	public void buttonClicked(View view){
		int buttonId = view.getId();
		if(buttonId == R.id.MenuNGButton){
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("CH_ALL", true);
            startActivity(i);
		}
		else if(buttonId == R.id.MenuSGButton){
			Intent i = new Intent(this, CheckBoxActivity.class);
            startActivity(i);
		}
		else if(buttonId == R.id.MenuSButton){
			Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
		}
		else if(buttonId == R.id.MenuHoFButton){
			Intent i = new Intent(this, HoFActivity.class);
            startActivity(i);
		}
		
	}
	
}
