package com.jahepi.tank;

public class Config {

	public static final String VERSION = "v1.3";
	public static final String ADMOB_KEY = "ca-app-pub-3227769552322799/6244007069";

	public static final float WIDTH = 21.33f * 4.0f;
	public static final float HEIGHT = 16 * 4;
	public static final float WIDTH_HEIGHT_DST = (WIDTH * WIDTH) + (HEIGHT * HEIGHT);
	public static final float CAMERA_WIDTH = WIDTH / 3.5f;
	public static final float CAMERA_HEIGHT = HEIGHT / 3.5f;
	public static final float MAP_SCALE_FACTOR = 0.2f;
	public static final float CAMERA_CENTER_DISTANCE = 18.0f;

	public static final int UI_WIDTH = 640;
	public static final int UI_HEIGHT = 480;

	// Camera constants for nicknames display
	public static final float UI_CAMERA_WIDTH = 1440;
	public static final float UI_CAMERA_HEIGHT = 1080;
	public static final float UI_CAMERA_WIDTH_RATIO = CAMERA_WIDTH / WIDTH;
	public static final float UI_CAMERA_HEIGHT_RATIO = CAMERA_HEIGHT / HEIGHT;
	public static final float UI_WIDTH_RATIO = UI_CAMERA_WIDTH / WIDTH;
	public static final float UI_HEIGHT_RATIO = UI_CAMERA_HEIGHT / HEIGHT;

	public static final float MIN_EXPLOSION_SIZE = 0.05f;
	public static final float MAX_EXPLOSION_SIZE = 0.15f;


	public static final boolean DEBUG = false;

	private Config() {
		// TODO Auto-generated constructor stub
	}

}
