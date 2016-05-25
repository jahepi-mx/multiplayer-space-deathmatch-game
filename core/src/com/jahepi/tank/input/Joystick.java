package com.jahepi.tank.input;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.jahepi.tank.Assets;

/**
 * Created by jahepi on 22/05/16.
 */
public class Joystick {

    public static final String TAG = "Joystick";

    private Vector2 size;
    private float xOffset;
    private float yOffset;
    private Vector2 position;
    private boolean isActive;
    private float degrees;
    private float distance;

    public Joystick() {
        size = new Vector2();
        position = new Vector2();
    }

    public void setSize(float width, float height) {
        size.x = width;
        size.y = height;
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

    public void update() {
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
        }
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        float distance = 0;
        float xCenter = xOffset + (size.x / 2);
        float yCenter = yOffset + (size.y / 2);
        float stickSize = 40;
        if (!isActive()) {
            batch.draw(Assets.getInstance().getControlStick(), xCenter - (stickSize / 2), yCenter - (stickSize / 2), stickSize, stickSize);
        } else {
            if (this.distance >= ((size.x / 2) * 0.75f)) {
                distance = (size.x / 2) * 0.75f;
            } else {
                distance = this.distance;
            }
            float x = xCenter - (stickSize / 2) + (MathUtils.cosDeg(degrees) * distance);
            float y = yCenter - (stickSize / 2) + (MathUtils.sinDeg(degrees) * distance);
            batch.draw(Assets.getInstance().getControlStick(), x, y, stickSize, stickSize);
        }
        batch.end();
    }

    public float getDegrees() {
        return degrees;
    }

    public void resetPosition() {
        position.x = 0;
        position.y = 0;
        isActive = false;
    }

    public float getDistancePercentage() {
        float percentage =  distance / ((size.x / 2) * 0.75f);
        if (percentage < 1) {
            return percentage;
        }
        return 1;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public float getXOffset() {
        return xOffset;
    }

    public float getYOffset() {
        return yOffset;
    }
}
