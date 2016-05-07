package com.jahepi.tank.entities.powerups;

import com.jahepi.tank.entities.Tank;

public class PowerUpMini extends PowerUpStateStrategy {

	public PowerUpMini() {
		super();
		timeLimit = 15.0f;
	}

	@Override
	public void start() {
		Tank tank = (Tank) entity;
		float size = 1.2f;
		tank.setWidth(size);
		tank.setHeight(size);
		tank.setRectangleSize(size, size);
	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onFinish() {
		Tank tank = (Tank) entity;
		float size = 2.0f;
		tank.setWidth(size);
		tank.setHeight(size);
		tank.setRectangleSize(size, size);
	}

}
