package com.jahepi.tank.entities.powerups;

import com.jahepi.tank.entities.Tank;

public class PowerUpLaser extends PowerUpStateStrategy {
	
	public PowerUpLaser() {
		super();
		timeLimit = 10.0f;
	}
	
	@Override
	public void start() {
		Tank tank = (Tank) entity;
		tank.setLaserVisible(true);
		//tank.setDisableShooting(true);	
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onFinish() {
		Tank tank = (Tank) entity;
		//tank.setDisableShooting(false);
		tank.setLaserVisible(false);
	}
}
