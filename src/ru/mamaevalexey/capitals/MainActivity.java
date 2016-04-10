package ru.mamaevalexey.capitals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import ru.mamaevalexey.capitals.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity{

	static DBManager dbManager;
	//public int K = 20;
	//private String [] ArrOfCounries/* = new String[K]*/;
	//public String [] ArrOfCapitals/* = new String[K]*/;
	//public Country [] MainArray = new Country [K]; 
	public ArrayList<Country> MainCountryArray = new ArrayList<Country>(); 
	private String [] Variants = new String[4];
	public Country MainCountry;
	private boolean right, trans = false, light = false;
	private String ClickedCapital, PreviousCapital="";
	private int score=0;
    private String GameName = "Новый Игрок";
    boolean check[] = new boolean[5];
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences shared = this.getPreferences(0);
        dbManager = DBManager.getInstance(this);
        
        RelativeLayout layout =(RelativeLayout)findViewById(R.id.BasicGameBackground);
        layout.setBackgroundResource(R.drawable.original);
        
        
        Resources res = getResources();
        String[][][] ARR = new String[2][5][];
		//ARR[1] = new String[2][5];
        ARR[0][0] = res.getStringArray(R.array.EUcountries);
        ARR[0][1] = res.getStringArray(R.array.ASIcountries);
        ARR[0][2] = res.getStringArray(R.array.AFRcountries);
        ARR[0][3] = res.getStringArray(R.array.AMEcountries);
        ARR[0][4] = res.getStringArray(R.array.OCEcountries);
        ARR[1][0] = res.getStringArray(R.array.EUcapitals);
        ARR[1][1] = res.getStringArray(R.array.ASIcapitals);
        ARR[1][2] = res.getStringArray(R.array.AFRcapitals);
        ARR[1][3] = res.getStringArray(R.array.AMEcapitals);
        ARR[1][4] = res.getStringArray(R.array.OCEcapitals);
        
        
        if(getIntent().getExtras().getBoolean("CH_ALL")){
        	for(int i=0; i<5;i++){
        		for(int j=0; j<ARR[1][i].length;j++){
        			MainCountryArray.add(new Country(ARR[0][i][j], ARR[1][i][j]));	
        		}
        	}
        }
        else{
        	for(int i=0;i<5;i++){
        		check[i]=getIntent().getExtras().getBoolean("CH_"+(i+1));
        	}
        	/*if(check[0])Toast.makeText(this, "Европа выбрана", Toast.LENGTH_LONG).show();
        	else Toast.makeText(this, "Европа не выбрана", Toast.LENGTH_LONG).show();*/
        	
            	for(int i=0; i<5;i++){
            		if(check[i]){
            			for(int j=0; j<ARR[1][i].length;j++){
            			
            				MainCountryArray.add(new Country(ARR[0][i][j], ARR[1][i][j]));
            			
            		}
            	}
            }
        }
        
        LoadCountries();
        if(savedInstanceState!=null)
        {
        	 Variants = savedInstanceState.getStringArray("var");
        	 score = savedInstanceState.getInt("scr");
        	 PreviousCapital = savedInstanceState.getString("pr");
        	 MainCountry = new Country(savedInstanceState.getString("mcou"), savedInstanceState.getString("mcap"));
        	 Update();
        }
        else if(shared!=null){
        	trans = shared.getBoolean("trans", false);
        	if(trans){
        		try{
        			GameName = getIntent().getExtras().getString("name");
                	trans = false;
        		}
            	catch(Exception e){
            		trans = false;
            	}
            }
        	ChooseCountry();
            MakeVariants();	
        	Update();
        	
        }
        else{
        	ChooseCountry();
            MakeVariants();	
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*public void onSettingsMenuClick(MenuItem item) {
    	switchSettingsActivity(null);
    }
    
    public void switchSettingsActivity(View v){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }*/
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt(i+" "+j, puzzle[i][j]);
        //outState.putInt("x", 1);
        outState.putStringArray("var", Variants);
        outState.putInt("scr", score);
        outState.putString("pr", PreviousCapital);
        outState.putString("mcou", MainCountry.country);
        outState.putString("mcap", MainCountry.capital);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
    	switch (id) {
        case R.id.action_settings:
        	trans = true;
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences shared = this.getPreferences(0);
        SharedPreferences.Editor editor = shared.edit();
        //editor.putInt("scr", score);
        editor.putBoolean("trans", trans);
        editor.commit();
        if(score!=0)dbManager.addResult(GameName, score);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    public void LoadCountries(){
    	
    	/*Resources res = getResources();
    	
		ArrOfCounries = res.getStringArray(R.array.countries_array);
		ArrOfCapitals = res.getStringArray(R.array.capitals_array);*/
    	
    	/*try {
			File CouFile = new File("Countries.txt");
			File CapFile = new File("Capitals.txt");
			Scanner CouScanner = new Scanner(CouFile);
			Scanner CapScanner = new Scanner(CapFile);
			for(int i=0; i<K; i++){
				ArrOfCounries[i]=CouScanner.next();
				ArrOfCapitals[i]=CapScanner.next();
	    	}
			CapScanner.close();
			CouScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
    	
    	/*for(int i=0; i<K; i++){
    		MainArray[i] = new Country(ArrOfCounries[i], ArrOfCapitals[i]);
    	}*/
    		
    }
    
    public void buttonClicked(View view) {
    	int buttonId = view.getId();
    	final Button btn = (Button) view.findViewById(buttonId);
    	//Toast.makeText(this, "Ответ: "+btn.getText().toString(), Toast.LENGTH_LONG).show();
    	if(buttonId==R.id.NewGameButton){
    		if(score!=0)dbManager.addResult(GameName, score);
    		ChooseCountry();
            MakeVariants();
    		score=0;
    		Update();
    	}
    	else if(buttonId==R.id.HoFbutton){
    		startActivity(new Intent(this, HoFActivity.class));
    	}
    	else{
    		Knopochki(btn);
    	}
    }
    
    public void Knopochki(final Button btn){
    	Drawable d = getResources().getDrawable(R.drawable.button_check);
		btn.setBackgroundDrawable(d);
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	ClickedCapital = btn.getText().toString();
            	Result();
            	if(right)btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_right));
            	else btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_false));
            	new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background));
                    	ContinueGame();
                    }
                }, 1000);
            }
        }, 1000);
    }
    
    public void Result(){
    	if(ClickedCapital.equals(MainCountry.capital)){
    		//Toast.makeText(this, "Правильный ответ :)", Toast.LENGTH_SHORT).show();
    		right = true;
    		PreviousCapital=ClickedCapital;
    	}
    	else right = false;
    }
    
    public void ContinueGame(){
    	if(right){
    	score++;
    	ChooseCountry();
        MakeVariants();
    	}	
    	else {
    		if(score!=0)dbManager.addResult(GameName, score);
    		MakeDialog();
    		/*ChooseCountry();
            MakeVariants();
    		score=0;
    		Update();*/
    	}
    }
    
    void MakeDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setTitle("Вы проиграли, "+GameName+" :(")
    			.setMessage("Правильный ответ - " + MainCountry.capital + "\n" + "Сыграем еще?")
    			.setCancelable(false)
    			.setNegativeButton("Конечно!",
    					new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog, int id) {
    							dialog.cancel();
    							ChooseCountry();
    				            MakeVariants();
    				    		score=0;
    				    		Update();
    						}
    					});
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    public void MakeVariants(){
    	
    	List<String> VarList = new LinkedList<String>();
    	boolean exist = false; //Наличие искомой столицы
    	for(int i=0; i<MainCountryArray.size(); i++){
    		VarList.add(MainCountryArray.get(i).capital);
    	}
    	Random r = new Random();
    	for (int i = 0; i < 4; i++) {
    		int rand = r.nextInt(VarList.size());
    		Variants[i]=VarList.get(rand);
    		VarList.remove(rand);
    		if(Variants[i].equals(MainCountry.capital)) exist = true;
    	}
    	if(!exist){
    		Variants[r.nextInt(4)]=MainCountry.capital;
    	}
    	Update();
    }
    
    public void Update(){
    	for (int i = 0; i < 4; i++) {
    		Button btn;
    		btn = (Button) findViewById(R.id.button20 + i);
    		btn.setText("" + Variants[i]);
    	}
    	TextView txt = (TextView) findViewById(R.id.CountryText);
		txt.setText(MainCountry.country);
    	TextView scr = (TextView) findViewById(R.id.ScoreText);
    	scr.setText("Счет: "+score);
    }
    
    public void ChooseCountry(){
    	Random r = new Random();
    	int rand = r.nextInt(MainCountryArray.size());
    	MainCountry = MainCountryArray.get(rand);
    	if(MainCountry.capital.equals(PreviousCapital)){
    		ChooseCountry();
    	}
    }

    
    
}
