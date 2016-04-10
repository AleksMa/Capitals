package ru.mamaevalexey.capitals;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class CheckBoxActivity extends Activity {

boolean check[] = new boolean[5];	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_box);
		
	}
	
	public void buttonClicked(View view){
		
		for(int i=0;i<5;i++){
			CheckBox ChB = (CheckBox) findViewById(R.id.checkBox1 + i);
			check[i] = ChB.isChecked();
		}
		if((check[0] && check[1] && check[2] && check[3] && check[4]) ||
				(!check[0] && !check[1] && !check[2] && !check[3] && !check[4])){
			Intent i = new Intent(this, MainActivity.class);
            i.putExtra("CH_ALL", true);
            startActivity(i);
		}
		else{
			Intent i = new Intent(this, MainActivity.class);
            i.putExtra("CH_ALL", false);
            for(int j=0;j<5;j++){
            	i.putExtra("CH_"+(j+1), check[j]);
    		}
            startActivity(i);
		}
		
		
	}
}
