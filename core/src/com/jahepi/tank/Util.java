package com.jahepi.tank;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Util {

	public Util() {
	}
	
	public static Vector2 getRotationPosition(float width, float height, float x, float y) {
		float co = height / 2;
		float ca = width;
		float deg = MathUtils.radiansToDegrees * MathUtils.atan2(co, ca);
		float tempX = (width / 2) * MathUtils.cosDeg(deg);
		float tempY = (height / 2) * MathUtils.sinDeg(deg);
		float xR = x + tempX;
		float yR = y + tempY;
		return new Vector2(xR, yR);
	}
}
