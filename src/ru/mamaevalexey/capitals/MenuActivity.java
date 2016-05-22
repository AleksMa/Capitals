package ru.mamaevalexey.capitals;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
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
		SetButtons();
		SharedPreferences shared = this.getPreferences(0);
		RelativeLayout layout =(RelativeLayout)findViewById(R.id.MenuLayout);
        layout.setBackgroundResource(R.drawable.original);
        if(shared!=null){
        	first = shared.getBoolean("f", true);
        	MenuName = shared.getString("Name", "Новый игрок");
        }
        if(!first){
        	if(getIntent().hasExtra("Name")){
        	MenuName = getIntent().getExtras().getString("Name");
        	first = true;
        	}
        }
	}

	@Override
    protected void onPause() {
        super.onPause();
        SharedPreferences shared = this.getPreferences(0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("Name", MenuName);
        editor.putBoolean("f", first);
        editor.commit();
    }
	

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
	
	private int GetWH(boolean width){
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metricsB = new DisplayMetrics();
		display.getMetrics(metricsB);
		if(width) return (int) (metricsB.widthPixels);
		else return (int) (metricsB.heightPixels);
	}
	
	private void SetButtons(){
		 /*Button btn = (Button)findViewById(R.id.MenuNGButton);
		 btn.setLayoutParams(new LayoutParams(300, 60));*/
	}
	
}
