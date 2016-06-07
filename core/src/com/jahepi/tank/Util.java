package com.jahepi.tank;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class Util {

	private static Vector2 vector1 = new Vector2();
	private static Vector2 vector2 = new Vector2();

	public Util() {
	}

	public static Vector2 getRotationPosition(float width, float height, float x, float y, float rotation) {
		if (vector1 == null) {
			vector1 = new Vector2();
		}
        x = x + (width / 2);
        y = y + (height / 2);
		//float radius = (float) (Math.sqrt((width * width) + (height * height))) / 2;
		float radius = width / 2;
		vector1.x = x + (MathUtils.cosDeg(rotation) * radius);
		vector1.y = y + (MathUtils.sinDeg(rotation) * radius);
		return vector1;
	}

	public static Vector2 getRotationPositionFromBack(float width, float height, float x, float y, float rotation) {
		if (vector2 == null) {
			vector2 = new Vector2();
		}
		x = x + (width / 2);
		y = y + (height / 2);
		//float radius = (float) -(Math.sqrt((width * width) + (height * height))) / 2;
		float radius = -(width / 2);
		vector2.x = x + (MathUtils.cosDeg(rotation) * radius);
		vector2.y = y + (MathUtils.sinDeg(rotation) * radius);
		return vector2;
	}
}
