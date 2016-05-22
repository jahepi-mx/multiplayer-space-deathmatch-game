package com.jahepi.tank.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by jahepi on 22/05/16.
 */
public class Joystick {

    public static final float ACTIVE_RADIUS = 2.0f;
    public static final String TAG = "Joystick";

    private Vector2 boundary;
    private Vector2 size;
    private float xOffset;
    private float yOffset;
    private Vector2 position;
    private boolean isActive;
    private float degrees;
    private float distance;

    public Joystick() {
        boundary = new Vector2();
        size = new Vector2();
        position = new Vector2();
    }

    public void setSize(float width, float height) {
        size.x = width;
        size.y = height;
    }

    public void setBoundary(float width, float height) {
        boundary.x = width;
        boundary.y = height;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setXOffset(float xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public void update(float deltatime) {
        float xCenter = size.x / 2;
        float yCenter = size.y / 2;

        float xTouch = position.x - xOffset;
        float yTouch = position.y - yOffset;

        if (xTouch >= 0 && xTouch <= size.x && yTouch >= 0 && yTouch <= size.y) {
            float x = xTouch - xCenter;
            float y = yTouch - yCenter;
            float radians = MathUtils.atan2(y, x);
            distance = (float) Math.sqrt((x * x) + (y * y));
            degrees = radians * MathUtils.radiansToDegrees;
            isActive = true;
            Gdx.app.log(TAG, "d " + degrees);
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin();
        renderer.rect(xOffset, yOffset, size.x, size.y);
        float xCenter = xOffset + (size.x / 2);
        float yCenter = yOffset + (size.y / 2);
        if (position.x == 0 && position.y == 0) {
            renderer.circle(xCenter, yCenter, 20, 10);
        } else {
            float x = xCenter + (MathUtils.cosDeg(degrees) * distance);
            float y = yCenter + (MathUtils.sinDeg(degrees) * distance);
            renderer.circle(x, y, 20, 10);
        }
        renderer.end();
    }

    public float getDegrees() {
        return degrees;
    }

    public void resetPosition() {
        position.x = 0;
        position.y = 0;
        isActive = false;
    }
}
