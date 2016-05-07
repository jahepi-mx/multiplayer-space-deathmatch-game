package com.jahepi.tank.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.jahepi.tank.Util;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;

public class OpponentTank extends Tank {
	
	private final static float REMOVE_LIMIT_TIME = 2.0f;
	
	private boolean readyRemove;
	private float readyRemoveTime;
	
	public OpponentTank(TextureRegion texture, TextureRegion missileTexture, ParticleEffect effect, Sound sound) {
		super(texture, missileTexture, effect, sound);
	}
	
	public void update(float deltatime) {
		
		if (isRemoved()) {
			readyRemoveTime += deltatime;
			if (readyRemoveTime >= REMOVE_LIMIT_TIME) {
				readyRemove = true;
			}
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
			float y = position.y - ((laser.getHeight() - size.y) / 2);
			Vector2 position = Util.getRotationPosition(size.x, size.y, this.position.x, y);
			laser.setX(position.x);
			laser.setY(position.y);
			laser.setRotation(rotation);
			laser.update(deltatime);
		}
		
		effect.update(deltatime);
	}

	public boolean isReadyRemove() {
		return readyRemove;
	}
}
