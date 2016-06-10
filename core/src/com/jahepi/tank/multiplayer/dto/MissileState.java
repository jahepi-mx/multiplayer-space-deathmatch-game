package com.jahepi.tank.multiplayer.dto;

import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;

public class MissileState {

	private float e, w, h, x, y, r, s;
	private TEXTURE_MISSILE_TYPE t;
	private int d;
	
	public MissileState() {
		// TODO Auto-generated constructor stub
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRotation() {
		return r;
	}

	public void setRotation(float rotation) {
		this.r = rotation;
	}

	public float getSpeed() {
		return s;
	}

	public void setSpeed(float speed) {
		this.s = speed;
	}

	public float getWidth() {
		return w;
	}

	public void setWidth(float width) {
		this.w = width;
	}

	public float getHeight() {
		return h;
	}

	public void setHeight(float height) {
		this.h = height;
	}

    public float getEffectScale() {
        return e;
    }

    public void setEffectScale(float effectScale) {
        this.e = effectScale;
    }

    public TEXTURE_MISSILE_TYPE getTextureType() {
		return t;
	}

	public void setTextureType(TEXTURE_MISSILE_TYPE textureType) {
		this.t = textureType;
	}

	public int getDamage() {
		return d;
	}

	public void setDamage(int damage) {
		this.d = damage;
	}
}
