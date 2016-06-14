package com.jahepi.tank.multiplayer.dto;

import com.jahepi.tank.entities.PowerUp;

public class PowerUpState {

	private float x;
	private float y;
	private PowerUp.TYPE t;
	
	public PowerUpState() {
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

	public PowerUp.TYPE getType() {
		return t;
	}

	public void setType(PowerUp.TYPE type) {
		this.t = type;
	}

}
