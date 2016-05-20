package com.jahepi.tank.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.jahepi.tank.Util;
import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;

public class OpponentTank extends Tank {
	
	private final static float REMOVE_LIMIT_TIME = 2.0f;
	
	private boolean readyRemove;
	private float readyRemoveTime;
	
	public OpponentTank(String name, TEXTURE_TYPE textureType, TEXTURE_MISSILE_TYPE missileTextureType, ParticleEffect effect, Sound sound) {
		super(name, textureType, missileTextureType, effect, sound);
	}
	
	public void update(float deltatime) {
		
		time += deltatime;
		
		if (isRemoved()) {
			readyRemoveTime += deltatime;
			if (readyRemoveTime >= REMOVE_LIMIT_TIME) {
				readyRemove = true;
			}
		}
		
		if (velocity > DEFAULT_VELOCITY) {
			speedUpTexture = speedUpAnimation.getKeyFrame(time);
		}

		for (Missile missile : missiles) {
			if (missile != null) {
				if (missile.isOutOfBounds() || missile.isDead()) {
					missiles.removeValue(missile, true);
				}
				missile.update(deltatime);
			}
		}
		
		for (PowerUpStateStrategy strategy : collectedPowerUpStrategies) {
			if (strategy != null) {
				if (strategy.isSend()) {
					collectedPowerUpStrategies.removeValue(strategy, true);
				}
			}
		}
		
		for (PowerUpStateStrategy strategy : powerUpStrategies) {
			if (strategy != null) {
				if (!strategy.isActive()) {
					powerUpStrategies.removeValue(strategy, true);
				}
				strategy.update(deltatime);
			}
		}
		
		if (laser.isVisible()) {
			Vector2 position = Util.getRotationPosition(size.x, size.y, getX(), getY(), rotation);
			laser.setX(position.x - (size.x / 2));
			laser.setY(position.y - (size.y / 2));
			laser.setRotation(rotation);
			laser.update(deltatime);
		}
		
		effect.update(deltatime);
	}

	public boolean isReadyRemove() {
		return readyRemove;
	}
}
