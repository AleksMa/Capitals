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
	public int IDs[][] = new int [5][]; 
	private boolean right, trans = false, light = false;
	private String ClickedCapital, PreviousCapital="";
	private int score=0;
    private String GameName = "����� �����";
    boolean check[] = new boolean[5];
    boolean tap = false;
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        SharedPreferences shared = this.getPreferences(0);
        dbManager = DBManager.getInstance(this);
        //---
        
        //layout.setBackgroundResource(0x7f020057);
        //---
        MakeId();
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
        			MainCountryArray.add(new Country(ARR[0][i][j], ARR[1][i][j], IDs[i][j]));
        		}
        	}
        }
        else{
        	for(int i=0;i<5;i++){
        		check[i]=getIntent().getExtras().getBoolean("CH_"+(i+1));
        	}
        	/*if(check[0])Toast.makeText(this, "������ �������", Toast.LENGTH_LONG).show();
        	else Toast.makeText(this, "������ �� �������", Toast.LENGTH_LONG).show();*/
        	
            	for(int i=0; i<5;i++){
            		if(check[i]){
            			for(int j=0; j<ARR[1][i].length;j++){
            			
            				MainCountryArray.add(new Country(ARR[0][i][j], ARR[1][i][j], IDs[i][j]));
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
        	 //MainCountry = new Country(savedInstanceState.getString("mcou"), savedInstanceState.getString("mcap"));
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
    	//Toast.makeText(this, "�����: "+btn.getText().toString(), Toast.LENGTH_LONG).show();
    	if(buttonId==R.id.NewGameButton){
    		if(score!=0)dbManager.addResult(GameName, score);
    		ChooseCountry();
            MakeVariants();
    		score=0;
    		Update();
    	}
    	/*else if(buttonId==R.id.HoFbutton){
    		startActivity(new Intent(this, HoFActivity.class));
    	}*/
    	else{
    		if(!tap){
    		Knopochki(btn);
    		}
    	}
    }
    
    public void Knopochki(final Button btn){
    	tap = true;
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
    		//Toast.makeText(this, "���������� ����� :)", Toast.LENGTH_SHORT).show();
    		right = true;
    		PreviousCapital=ClickedCapital;
    	}
    	else right = false;
    }
    
    public void ContinueGame(){
    	tap = false;
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
    	builder.setTitle("�� ���������, "+GameName+" :(")
    			.setMessage("���������� ����� - " + MainCountry.capital + "\n" + "������� ���?")
    			.setCancelable(false)
    			.setNegativeButton("�������!",
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
    	boolean exist = false; //������� ������� �������
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
		txt.setText("�������� ������� ����������� \n" + MainCountry.country);
		//txt.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background));
    	TextView scr = (TextView) findViewById(R.id.ScoreText);
    	scr.setText("����: "+score);
    }
    
    public void ChooseCountry(){
    	Random r = new Random();
    	int rand = r.nextInt(MainCountryArray.size());
    	MainCountry = MainCountryArray.get(rand);
    	if(MainCountry.capital.equals(PreviousCapital)){
    		ChooseCountry();
    	}
    	RelativeLayout layout =(RelativeLayout)findViewById(R.id.BasicGameBackground);
    	layout.setBackgroundResource(MainCountry.id);
    }

    public void MakeId(){
    	
    	IDs[0]=new int[45];
    	IDs[1]=new int[45];
    	IDs[2]=new int[54];
    	IDs[3]=new int[35];
    	IDs[4]=new int[13];
    	
    	int [] EUid = {R.drawable.at, R.drawable.al, R.drawable.ad, R.drawable.by , R.drawable.be , R.drawable.bg, R.drawable.ba, R.drawable.va,
    			R.drawable.gb, R.drawable.hu, R.drawable.de, R.drawable.gr, R.drawable.dk, R.drawable.ie, R.drawable.is, R.drawable.es, 
    			R.drawable.it, R.drawable.cy, R.drawable.lv, R.drawable.lt, R.drawable.li, R.drawable.lu, R.drawable.mk, R.drawable.mt,
    			R.drawable.md, R.drawable.mc, R.drawable.nl, R.drawable.no, R.drawable.pl, R.drawable.pt, R.drawable.ru, R.drawable.ro,
    			R.drawable.sm, R.drawable.rs, R.drawable.sk, R.drawable.si, R.drawable.ua, R.drawable.fi, R.drawable.fr, R.drawable.hr,
    			R.drawable.me, R.drawable.cz, R.drawable.ch, R.drawable.se, R.drawable.ee};
    	int [] AFid = {R.drawable.dz, R.drawable.ao, R.drawable.bj, R.drawable.bw, R.drawable.bf, R.drawable.bi, R.drawable.ga, R.drawable.gm,
    			R.drawable.gh, R.drawable.gn, R.drawable.gw, R.drawable.dj, R.drawable.eg, R.drawable.zm, R.drawable.zw, R.drawable.cv,
    			R.drawable.cm, R.drawable.ke, R.drawable.km, R.drawable.cd, R.drawable.cg, R.drawable.si, R.drawable.ls, R.drawable.lr,
    			R.drawable.lb, R.drawable.ly, R.drawable.mu, R.drawable.mr, R.drawable.mg, R.drawable.mw, R.drawable.ml, R.drawable.ma,
    			R.drawable.mz, R.drawable.na, R.drawable.ne, R.drawable.ng, R.drawable.rw, R.drawable.st, R.drawable.sz, R.drawable.sc,
    			R.drawable.sn, R.drawable.so, R.drawable.sd, R.drawable.sl, R.drawable.tz, R.drawable.tg, R.drawable.tn, R.drawable.ug,
    			R.drawable.cf, R.drawable.td, R.drawable.gq, R.drawable.er, R.drawable.et, R.drawable.za};
    	
    	
    	
    		for(int j=0;j<IDs[0].length;j++){
        		IDs[0][j]=EUid[j];
        		IDs[1][j]=R.drawable.original; 
        		//IDs[1][j]=ASid[j];
        	}
    		for(int j=0;j<IDs[2].length;j++){
        		IDs[2][j]=AFid[j];
        	}
    		for(int j=0;j<IDs[3].length;j++){
    			IDs[3][j]=R.drawable.original; 
    			//IDs[3][j]=AMid[j];
        	}
    		for(int j=0;j<IDs[4].length;j++){
    			IDs[4][j]=R.drawable.original; 
    			//IDs[4][j]=AOid[j];
        	}
    	}
    
    }
    
    

