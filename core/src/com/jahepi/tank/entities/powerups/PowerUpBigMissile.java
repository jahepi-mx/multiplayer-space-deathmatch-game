package com.jahepi.tank.entities.powerups;

import com.badlogic.gdx.math.Vector2;
import com.jahepi.tank.Config;
import com.jahepi.tank.entities.Tank;

public class PowerUpBigMissile extends PowerUpStateStrategy {
	
	public PowerUpBigMissile() {
		super();
		timeLimit = 15.0f;
	}
	
	@Override
	public void start() {
		Tank tank = (Tank) entity;
		Vector2 size = tank.getMissileSize();
		tank.setMissileSize(size.x * 2.0f,size.y * 2.0f);
		tank.setMissileEffectScale(Config.MAX_EXPLOSION_SIZE);
		tank.setMissileDamage(2);
		tank.setMissileSpeed(18.0f);
	}

	@Override
	public void onUpdate() {
		
	}

	@Override
	public void onFinish() {
		Tank tank = (Tank) entity;
		Vector2 size = tank.getMissileSize();
		tank.setMissileSize(size.x / 2.0f,size.y / 2.0f);
		tank.setMissileEffectScale(Config.MIN_EXPLOSION_SIZE);
		tank.setMissileDamage(1);
		tank.setMissileSpeed(16.0f);
	}
}
