package com.example.growupsnake;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class Music extends Service{
    private MediaPlayer mp;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public void onCreate() {
    	super.onCreate();
    	mp=MediaPlayer.create(this, R.raw.bgm);
    }
    @SuppressWarnings("deprecation")
    @Override
	public void onStart(Intent intent,int startId){
    	super.onStart(intent, startId);
    	mp.start();
    	mp.setLooping(true);
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mp.stop();
    }
}
