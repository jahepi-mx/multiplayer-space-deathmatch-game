package com.jahepi.tank.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Util;
import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;
import com.jahepi.tank.multiplayer.dto.MissileState;
import com.jahepi.tank.multiplayer.dto.TankState;

public class OpponentTank extends Tank {
	
	private final static float REMOVE_LIMIT_TIME = 2.0f;
	
	private boolean readyRemove;
	private float readyRemoveTime;
	private Vector2 newPosition;
	private float newRotation;
	
	public OpponentTank(String name, TEXTURE_TYPE textureType, TEXTURE_MISSILE_TYPE missileTextureType, Assets assets) {
		super(name, textureType, missileTextureType, assets);
		newPosition = new Vector2();
	}
	
	public void update(float deltatime) {
		
		time += deltatime;

		float alpha = 0.2f;
		float dtheta = newRotation - rotation;
		if (dtheta > 180) {
			rotation += 360;
		} else if (dtheta < -180) {
			rotation -= 360;
		}
		position.x += (newPosition.x - position.x) * alpha;
		position.y += (newPosition.y - position.y) * alpha;
		rotation += (newRotation - rotation) * alpha;
		rectangle.setPosition(position.x, position.y);
		rectangle.setRotation(rotation);

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
					missilePool.free(missile);
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

	public void updateState(TankState tankState, boolean isSend) {
		newPosition.set(tankState.getX(), tankState.getY());
		newRotation = tankState.getRotation();
		rectangle.setPosition(position.x, position.y);
		rectangle.setRotation(rotation);
		shooting = tankState.isShooting();
		removed = tankState.isRemoved();
		velocity = tankState.getVelocity();
		activeSpeedUp = tankState.isSpeedUp();
		megaShootEnableAnimation = tankState.isMegaShoot();
		if (isSend) {
			life = tankState.getLife();
			wins = tankState.getWins();
		}

		if (tankState.isShooting()) {
			laser.shoot();
		} else {
			laser.releaseShoot();
		}
		for (MissileState missileState : tankState.getMissiles()) {
			Missile missile = missilePool.obtain();
			missile.init(missileState.getX(), missileState.getY(), missileState.getRotation(), missileState.getWidth(), missileState.getHeight(), missileState.getEffectScale(), missileState.getTextureType(), missileState.getSpeed(), missileState.getDamage(), false, assets);
			missile.setSound(sound);
			missile.playSound();
			if (isSend) {
				missile.setSend(true);
			}
			missiles.add(missile);
		}
		for (PowerUp.TYPE type : tankState.getPowerUps()) {
			PowerUpStateStrategy strategy = PowerUp.getPowerUpStrategy(type);
			if (isSend) {
				strategy.setSend(true);
			}
			addPowerUpStrategy(strategy);
		}
	}
}
