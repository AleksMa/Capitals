package ru.mamaevalexey.capitals;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private String Name = "Новый Игрок";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences shared = this.getPreferences(0);
		setContentView(R.layout.activity_settings);
		Update();
	}

	@Override
    protected void onPause() {
        super.onPause();
        SharedPreferences shared = this.getPreferences(0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("name", Name);
        editor.commit();
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
    	switch (id) {
        case R.id.action_settings:
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("name", Name);
            startActivity(i);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
	public void ChangeName(View view){
		int buttonId = view.getId();
    	Button btn = (Button) view.findViewById(buttonId);
    	if(buttonId==R.id.buttonName){
    		EditText text = (EditText) findViewById(R.id.editText1);
			Name = text.getEditableText().toString();
    		Update();
    	}
    	else if(buttonId==R.id.IntoMain){
    		Intent i = new Intent(this, MenuActivity.class);
            i.putExtra("Name", Name);
            startActivity(i);
    	}
	}
	
	public void Update(){
		TextView name = (TextView) findViewById(R.id.NameTextView);
		name.setText("Ваше имя: "+ Name);
	}
	
	
}
