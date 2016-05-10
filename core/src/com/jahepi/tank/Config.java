package com.jahepi.tank;

public class Config {

	public static final float WIDTH = 20 * 3;
	public static final float HEIGHT = 16 * 3;
	public static final float CAMERA_WIDTH = WIDTH / 3;
	public static final float CAMERA_HEIGHT = HEIGHT / 3;
	public static final float MAP_SCALE_FACTOR = 0.2f;
	public static final float CAMERA_CENTER_DISTANCE = 18.0f;
	
	public static final int UI_WIDTH = 640;
	public static final int UI_HEIGHT = 480;
	
	// Camera constants for nicknames display
	public static final float UI_CAMERA_WIDTH = 1920;
	public static final float UI_CAMERA_HEIGHT = 1080;
	public static final float UI_CAMERA_WIDTH_RATIO = CAMERA_WIDTH / WIDTH;
	public static final float UI_CAMERA_HEIGHT_RATIO = CAMERA_HEIGHT / HEIGHT;
	public static final float UI_WIDTH_RATIO = UI_CAMERA_WIDTH / WIDTH;
	public static final float UI_HEIGHT_RATIO = UI_CAMERA_HEIGHT / HEIGHT;
	
	public static final boolean DEBUG = false;
	
	private Config() {
		// TODO Auto-generated constructor stub
	}

}
