package com.jahepi.tank.entities.powerups;

import com.jahepi.tank.entities.Tank;

public class PowerUpFreeze extends PowerUpStateStrategy {

	public PowerUpFreeze() {
		super();
		timeLimit = 5.0f;
	}

	@Override
	public void start() {
		Tank tank = (Tank) entity;
		tank.setVelocity(2.0f);
	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onFinish() {
		Tank tank = (Tank) entity;
		tank.setVelocity(10.0f);
	}
}
