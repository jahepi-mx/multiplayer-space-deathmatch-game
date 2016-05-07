package com.jahepi.tank.entities.powerups;

import com.jahepi.tank.entities.Tank;

public class PowerUpLife extends PowerUpStateStrategy {

	public PowerUpLife() {
		super();
		timeLimit = 1.0f;
	}

	@Override
	public void start() {
		Tank tank = (Tank) entity;
		tank.setLife(tank.getLife() + 5);
	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onFinish() {
		
	}
}
