package ru.mamaevalexey.capitals;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HoFActivity extends Activity {

	private DBManager dbManager;
	ArrayList<Result> results;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ho_f);
		dbManager = DBManager.getInstance(this);
		
		TextView restv = (TextView)this.findViewById(R.id.results);
		results = dbManager.getAllResults();
		String resStr = "";
		for (Result res : results)
		{
			resStr += res.name + ": " + res.score + "\n";
		}	
		restv.setText(resStr);
	}
	
	public void buttonClicked(View view){
		int buttonId = view.getId();
    	Button btn = (Button) view.findViewById(buttonId);
    	//ArrayList<Result> results = dbManager.getAllResults();
    	TextView restv = (TextView)this.findViewById(R.id.results);
		String resStr = "";
    	if(buttonId==R.id.DeleteAllButton){
    		deleteAll(view);
    		restv.setText("");
    	}
    	/*else if(buttonId==R.id.DeleteForName){
    		EditText text = (EditText) findViewById(R.id.DeleteForName);
			String Name = text.getEditableText().toString();
    		deleteForName(view, Name);
    		for (Result res : results)
    		{
    			resStr += res.name + ": " + res.score + "\n";
    		}	
    		restv.setText(resStr);
    	}*/
	}
	
	public void deleteAll (View v)
	{
		dbManager.deleteALL();
	}
	
	public void deleteForName (View v, String name)
	{
		dbManager.deleteForName(name);
	}
	
}
