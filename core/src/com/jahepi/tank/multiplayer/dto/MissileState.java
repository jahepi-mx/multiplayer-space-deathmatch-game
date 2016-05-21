package com.jahepi.tank.multiplayer.dto;

import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;

public class MissileState {

	private float effectScale, width, height, x, y, rotation, speed;
	private TEXTURE_MISSILE_TYPE textureType;
	private int damage;
	
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
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

    public float getEffectScale() {
        return effectScale;
    }

    public void setEffectScale(float effectScale) {
        this.effectScale = effectScale;
    }

    public TEXTURE_MISSILE_TYPE getTextureType() {
		return textureType;
	}

	public void setTextureType(TEXTURE_MISSILE_TYPE textureType) {
		this.textureType = textureType;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}
