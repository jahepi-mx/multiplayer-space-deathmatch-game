package com.jahepi.tank.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jahepi.tank.Config;
import com.jahepi.tank.TankField;
import com.jahepi.tank.ads.AdListener;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.UI_WIDTH;
		config.height = Config.UI_HEIGHT;
		config.foregroundFPS = 60;
		new LwjglApplication(new TankField(new AdListener() {
			@Override
			public void show(boolean active) {

			}

			@Override
			public void showInterstitial() {

			}
		}), config);
	}
}
