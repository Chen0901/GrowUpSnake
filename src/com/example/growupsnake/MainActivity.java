package com.example.growupsnake;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SharedPreferences sp1,sp2,sp3;
		@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp1=getSharedPreferences("ScoreData1", 0);
		sp2=getSharedPreferences("ScoreData2", 0);
		sp3=getSharedPreferences("ScoreData3", 0);
        Button hs=(Button)findViewById(R.id.button2);
        hs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				RankDialog();
			}
		});
        hs.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if(e.getAction() == MotionEvent.ACTION_DOWN){  
					 v.setBackgroundResource(R.drawable.button2down);
				 }else if(e.getAction() == MotionEvent.ACTION_UP){    
					 v.setBackgroundResource(R.drawable.button2);
            }    
				return false;
			}
		});
        Button start=(Button)findViewById(R.id.button1);
        start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				    MainActivity.this.finish();
				 	Intent intent=new Intent(MainActivity.this, GameHelper.class);
				 	startActivity(intent);
			}
		});
        start.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				 if(e.getAction() == MotionEvent.ACTION_DOWN){  
					 v.setBackgroundResource(R.drawable.button1down);
				 }else if(e.getAction() == MotionEvent.ACTION_UP){    
					 v.setBackgroundResource(R.drawable.button1);
             }    

				return false;
			}
		});
    }
		
		
		@SuppressWarnings("deprecation")
		protected void RankDialog() {
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.rank_dialog,null);
			int score1=sp1.getInt("hscore1", 0);
			int score2=sp2.getInt("hscore2", 0);
			int score3=sp3.getInt("hscore3", 0);
			String str1=String.valueOf(score1);
			String str2=String.valueOf(score2);
			String str3=String.valueOf(score3);
			TextView s1=(TextView)layout.findViewById(R.id.textView1);
			TextView s2=(TextView)layout.findViewById(R.id.textView2);
			TextView s3=(TextView)layout.findViewById(R.id.textView3);
			s1.setText(str1);
			s2.setText(str2);
			s3.setText(str3);
			final Dialog Rank=new Dialog(MainActivity.this, R.style.Rank);
			Rank.setContentView(layout);
			Window dialogWindow=Rank.getWindow();
			WindowManager m = getWindowManager();
	        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
	        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
	        p.height = (int) (d.getHeight()); 
	        p.width = (int) (d.getWidth()); 
	        dialogWindow.setAttributes(p);
			Rank.show();
			Button negativeButton=(Button)layout.findViewById(R.id.button1);
			negativeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
				    Rank.dismiss();
				}
			});
			negativeButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent e) {
					 if(e.getAction() == MotionEvent.ACTION_DOWN){  
						 v.setBackgroundResource(R.drawable.x2);
					 }else if(e.getAction() == MotionEvent.ACTION_UP){    
						 v.setBackgroundResource(R.drawable.x);
	             }    
					return false;
				}
			});
		}
		
		
		public void onBackPressed() { 
		  new AlertDialog.Builder(this).setTitle("确认退出吗？") 
	            .setIcon(android.R.drawable.ic_dialog_info) 
	            .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	         
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“确认”后的操作 
	                    MainActivity.this.finish(); 
	         
	                } 
	            }) 
	            .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
	         
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“返回”后的操作,这里不设置没有任何操作 
	                } 
	            }).show(); 
	} 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onStart() {
    	super.onStart();
    	startService(new Intent(MainActivity.this, Music.class));
    }
    @Override
    protected void onPause() {
    	super.onPause();
	    stopService(new Intent(MainActivity.this, Music.class));
    }
}
