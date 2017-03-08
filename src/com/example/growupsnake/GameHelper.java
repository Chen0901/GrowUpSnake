package com.example.growupsnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class GameHelper extends Activity{
	private GestureDetector GD; 
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.game_helper);
	GD=new GestureDetector(GameHelper.this, new SimpleOnGestureListener(){
		@Override
		public boolean onDown(MotionEvent e) {
			Intent intent=new Intent(GameHelper.this, GameActivity.class);
		 	startActivity(intent);
		 	GameHelper.this.finish();
			return super.onDown(e);
		}
	});
}
@Override
public void onBackPressed() {
	super.onBackPressed();
	Intent intent=new Intent(GameHelper.this, MainActivity.class);
 	startActivity(intent);
 	GameHelper.this.finish();
}
@Override
public boolean onTouchEvent(MotionEvent event) {
	GD.onTouchEvent(event);    
    return true; 
}
}
