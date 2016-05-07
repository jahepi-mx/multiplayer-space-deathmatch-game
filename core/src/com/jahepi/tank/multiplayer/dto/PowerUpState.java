package com.jahepi.tank.multiplayer.dto;

import com.jahepi.tank.entities.PowerUp;

public class PowerUpState {

	private float x;
	private float y;
	private PowerUp.TYPE type;
	
	public PowerUpState() {
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

	public PowerUp.TYPE getType() {
		return type;
	}

	public void setType(PowerUp.TYPE type) {
		this.type = type;
	}
}
