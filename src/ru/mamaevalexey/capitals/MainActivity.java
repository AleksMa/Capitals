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
	public ArrayList<Country> MainCountryArray = new ArrayList<Country>(); 
	private String [] Variants = new String[4];
	public Country MainCountry;
	public int IDs[][] = new int [5][]; 
	private boolean right, trans = false;
	private String ClickedCapital, PreviousCapital="";
	private int score=0;
    private String GameName = "Новый Игрок";
    boolean check[] = new boolean[5];
    boolean tap = false;
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        SharedPreferences shared = this.getPreferences(0);
        dbManager = DBManager.getInstance(this);
        MakeId();
        String[][][] ARR = new String[2][5][];
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
        	
            	for(int i=0; i<5;i++){
            		if(check[i]){
            			for(int j=0; j<ARR[1][i].length;j++){
            			
            				MainCountryArray.add(new Country(ARR[0][i][j], ARR[1][i][j], IDs[i][j]));
            		}
            	}
            }
        }
        GameName = getIntent().getExtras().getString("Name");
        //LoadCountries();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        editor.putBoolean("trans", trans);
        editor.commit();
        //if(score!=0)dbManager.addResult(GameName, score);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    /*public void LoadCountries(){
    	
    }*/
    
    public void buttonClicked(View view) {
    	int buttonId = view.getId();
    	final Button btn = (Button) view.findViewById(buttonId);
    	if(buttonId==R.id.NewGameButton){
    		if(score!=0)dbManager.addResult(GameName, score);
    		ChooseCountry();
            MakeVariants();
    		score=0;
    		Update();
    	}
    	else{
    		if(!tap){
    		Buttons(btn);
    		}
    	}
    }
    
   
    
    public void Buttons(final Button btn){
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
                    	tap = false;
                    	ContinueGame();
                    }
                }, 1000);
            }
        }, 1000);
		
    }
    
    public void Result(){
    	if(ClickedCapital.equals(MainCountry.capital)){
    		//Toast.makeText(this, "Правильный ответ", Toast.LENGTH_SHORT).show();
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
        tap = false;
    	}	
    	else {
    		if(score!=0)dbManager.addResult(GameName, score);
    		MakeDialog();
    		tap = false;
    		/*ChooseCountry();
            MakeVariants();
    		score=0;
    		Update();*/
    	}
    }
    
    void MakeDialog(){
    	/*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    	String button1String = "Конечно!";*/
    	
    	AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle("Вы проиграли, "+GameName+" :(");
        ad.setMessage("Правильный ответ - " + MainCountry.capital + "\n" + "Сыграем еще?");
        ad.setPositiveButton("Конечно!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	dialog.cancel();
				ChooseCountry();
	            MakeVariants();
	    		score=0;
	    		Update();
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	dialog.cancel();
            	ToMainMenu();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
        AlertDialog alert = ad.create();
    	alert.show();
    }
    
    public void ToMainMenu(){
    	Intent i = new Intent(this, MenuActivity.class);
		startActivity(i);
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
		txt.setText("Назовите столицу государства \n" + MainCountry.country);
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
    	
    	int [] ASid = {R.drawable.az, R.drawable.am, R.drawable.af, R.drawable.bd, R.drawable.bh, R.drawable.bn, R.drawable.bt, R.drawable.tl,
    			R.drawable.vn, R.drawable.ge, R.drawable.il, R.drawable.in, R.drawable.id, R.drawable.jo, R.drawable.iq, R.drawable.ir,
    			R.drawable.ye, R.drawable.kz, R.drawable.kh, R.drawable.qa, R.drawable.kg, R.drawable.cn, R.drawable.kw, R.drawable.la,
    			R.drawable.my, R.drawable.mv, R.drawable.mn, R.drawable.mm, R.drawable.np, R.drawable.ae, R.drawable.om, R.drawable.pk,
    			R.drawable.sa, R.drawable.kp, R.drawable.sg, R.drawable.sy, R.drawable.tj, R.drawable.th, R.drawable.tm, R.drawable.tr, R.drawable.uz,
    			R.drawable.ph, R.drawable.lk, R.drawable.kr, R.drawable.jp };
    	
    	int [] AFid = {R.drawable.dz, R.drawable.ao, R.drawable.bj, R.drawable.bw, R.drawable.bf, R.drawable.bi, R.drawable.ga, R.drawable.gm,
    			R.drawable.gh, R.drawable.gn, R.drawable.gw, R.drawable.dj, R.drawable.eg, R.drawable.zm, R.drawable.zw, R.drawable.cv,
    			R.drawable.cm, R.drawable.ke, R.drawable.km, R.drawable.cd, R.drawable.cg, R.drawable.ci, R.drawable.ls, R.drawable.lr,
    			R.drawable.lb, R.drawable.ly, R.drawable.mu, R.drawable.mr, R.drawable.mg, R.drawable.mw, R.drawable.ml, R.drawable.ma,
    			R.drawable.mz, R.drawable.na, R.drawable.ne, R.drawable.ng, R.drawable.rw, R.drawable.st, R.drawable.sz, R.drawable.sc,
    			R.drawable.sn, R.drawable.so, R.drawable.sd, R.drawable.sl, R.drawable.tz, R.drawable.tg, R.drawable.tn, R.drawable.ug,
    			R.drawable.cf, R.drawable.td, R.drawable.gq, R.drawable.er, R.drawable.et, R.drawable.za};
    	
    	int [] AMid = {R.drawable.ag, R.drawable.ar, R.drawable.bs, R.drawable.bb, R.drawable.bz, R.drawable.bo, R.drawable.br,
    			R.drawable.ve, R.drawable.ht, R.drawable.gy, R.drawable.gt, R.drawable.hn, R.drawable.gd, R.drawable.dm,
    			R.drawable.dom, R.drawable.ca, R.drawable.co, R.drawable.cr, R.drawable.cu, R.drawable.mx, R.drawable.ni,
    			R.drawable.pa, R.drawable.py, R.drawable.pe, R.drawable.sv, R.drawable.vc, R.drawable.kn, R.drawable.lc, 
    			R.drawable.us, R.drawable.sr, R.drawable.tt, R.drawable.uy, R.drawable.cl, R.drawable.ec, R.drawable.jm };
    	
    	int [] AOid = {R.drawable.au, R.drawable.vu, R.drawable.ki, R.drawable.mh, R.drawable.nz, R.drawable.pw, R.drawable.pg,
    			R.drawable.ws, R.drawable.sb, R.drawable.to, R.drawable.tv, R.drawable.fm, R.drawable.fj};
    	
    	
    	
    		for(int j=0;j<IDs[0].length;j++){
        		IDs[0][j]=EUid[j];
        		IDs[1][j]=ASid[j];
        	}
    		for(int j=0;j<IDs[2].length;j++){
        		IDs[2][j]=AFid[j];
        	}
    		for(int j=0;j<IDs[3].length;j++){
    			IDs[3][j]=AMid[j];
        	}
    		for(int j=0;j<IDs[4].length;j++){
    			IDs[4][j]=AOid[j];
        	}
    	}
    
    }
    
    

