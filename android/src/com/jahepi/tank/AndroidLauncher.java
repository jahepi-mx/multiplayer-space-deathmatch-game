package com.jahepi.tank;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.jahepi.tank.TankField;

public class AndroidLauncher extends AndroidApplication {
	
	private static final String TAG = "AndroidLauncher";
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new TankField(), config);
	}
}
