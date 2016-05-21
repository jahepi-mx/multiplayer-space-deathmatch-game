package com.jahepi.tank;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Util {

	public Util() {
	}

	public static Vector2 getRotationPosition(float width, float height, float x, float y, float rotation) {
        x = x + (width / 2);
        y = y + (height / 2);
		float radius = (float) (Math.sqrt((width * width) + (height * height))) / 2;
		float xTemp = x + (MathUtils.cosDeg(rotation) * radius);
		float yTemp = y + (MathUtils.sinDeg(rotation) * radius);
		return new Vector2(xTemp, yTemp);
	}

	public static Vector2 getRotationPositionFromBack(float width, float height, float x, float y, float rotation) {
		x = x + (width / 2);
		y = y + (height / 2);
		float radius = (float) -(Math.sqrt((width * width) + (height * height))) / 2;
		float xTemp = x + (MathUtils.cosDeg(rotation) * radius);
		float yTemp = y + (MathUtils.sinDeg(rotation) * radius);
		return new Vector2(xTemp, yTemp);
	}
}
