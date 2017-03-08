package com.example.growupsnake;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;
public class GameActivity extends Activity {
	private String str="Õ£÷π“Ù¿÷≤•∑≈";
	private boolean StopMusic=false;
	private MediaPlayer mp;
	private GameView gv;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.game_controller);
		 mp=MediaPlayer.create(GameActivity.this,R.raw.game);
	     mp.setLooping(true);
		 Button set=(Button)findViewById(R.id.button1);
		 set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				gv.stop=true;
				gv.tap=true;
				Toast.makeText(GameActivity.this, "”Œœ∑‘›Õ£÷–~~", Toast.LENGTH_SHORT).show();
				showMultiDia();
			}
		});
		 set.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_DOWN){  
					 v.setBackgroundResource(R.drawable.setdown);
				 }else if(e.getAction() == MotionEvent.ACTION_UP){    
					 v.setBackgroundResource(R.drawable.set);
			}
				return false;
			}
		});
	 }
	protected void showMultiDia() {
		  AlertDialog.Builder multiDia=new AlertDialog.Builder(GameActivity.this);  
	        multiDia.setTitle("”Œœ∑…Ë÷√");
	        multiDia.setPositiveButton("∑µªÿ±ÍÃ‚", new DialogInterface.OnClickListener() {  
	              
	            @Override  
	            public void onClick(DialogInterface dialog, int which) {
	                 GameActivity.this.finish();
	                 Intent intent=new Intent(GameActivity.this, MainActivity.class);
					 startActivity(intent);
	            }  
	        });  
	        multiDia.setNeutralButton(str, new DialogInterface.OnClickListener() {  
	              
	            @Override  
	            public void onClick(DialogInterface dialog, int which) { 
	            	if(!StopMusic){
	            	mp.pause();
	            	StopMusic=true;
	            	str="ª÷∏¥“Ù¿÷≤•∑≈";
	            	}else{
	            		mp.start();
	            		StopMusic=false;
	            		str="Õ£÷π“Ù¿÷≤•∑≈";
	            	}
	            }  
	        });  
	        multiDia.setNegativeButton("»°œ˚", new DialogInterface.OnClickListener() {  
	              
	            @Override  
	            public void onClick(DialogInterface dialog, int which) { 
	            	if(!gv.gameover){
	            	Toast.makeText(GameActivity.this, "«ÎÀ´ª˜∆¡ƒªª÷∏¥”Œœ∑~~", Toast.LENGTH_SHORT).show();
	            	}
	            }  
	        });  
	        multiDia.create().show();  
		
	}
	
	@Override
	    protected void onStart() {
	    	super.onStart();
	    	mp.start();
	    }
	  @Override
	   protected void onPause() {
		super.onPause();
		gv.stop=true;
		gv.tap=true;
		mp.pause();
	}
	  @Override
	protected void onRestart() {
    	super.onRestart();
    	mp.start();
    	if(!gv.gameover){
    	Toast.makeText(GameActivity.this, "«ÎÀ´ª˜∆¡ƒªª÷∏¥”Œœ∑~~", Toast.LENGTH_SHORT).show();
    	}
	}
	  @Override
	public void onBackPressed() {
		Intent intent=new Intent(GameActivity.this, GameHelper.class);
		startActivity(intent);
		GameActivity.this.finish();
		super.onBackPressed();
	}
}
