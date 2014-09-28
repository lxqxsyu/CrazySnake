package com.example.crazysnake;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	private MySnake mySnake;
	private static String ICICLE_KEY = "snake-view";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mySnake = (MySnake) findViewById(R.id.mysnake);
		if(savedInstanceState == null){
			mySnake.setCurrentState(MySnake.State.READY);
		}else{
			Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
			if(map != null){
				mySnake.restoreState(map);
			}else{
				mySnake.setCurrentState(MySnake.State.PAUSE);
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mySnake.setCurrentState(MySnake.State.PAUSE);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBundle(ICICLE_KEY, mySnake.saveState());
	}
}
