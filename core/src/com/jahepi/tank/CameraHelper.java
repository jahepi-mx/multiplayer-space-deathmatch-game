package com.jahepi.tank;

import com.badlogic.gdx.math.Vector2;

public class CameraHelper {

	private Vector2 position;
	private Vector2 lerpPosition;
	private Vector2 size;
	
	public CameraHelper(float width, float height, float x, float y) {
		size = new Vector2(width, height);
		position = new Vector2(x, y);
		lerpPosition = new Vector2(0, 0);
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public void setY(float y) {
		position.y = y;
	}
	
	public float getX() {
		return lerpPosition.x;
	}
	
	public float getY() {
		return lerpPosition.y;
	}
	
	public float getWidth() {
		return size.x;
	}
	
	public float getHeight() {
		return size.y;
	}
	
	public void update() {
		
		lerpPosition.lerp(position, 0.05f);
		
		if (lerpPosition.x <= (size.x / 2)) {
			lerpPosition.x = (size.x / 2);
		}
		if (lerpPosition.x >= Config.WIDTH - (size.x / 2)) {
			lerpPosition.x = Config.WIDTH - (size.x / 2);
		}
		if (lerpPosition.y <= (size.y / 2)) {
			lerpPosition.y = (size.y / 2);
		}
		if (lerpPosition.y >= Config.HEIGHT - (size.y / 2)) {
			lerpPosition.y = Config.HEIGHT - (size.y / 2);
		}
	}
}
