package com.jahepi.tank.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.entities.powerups.PowerUpBigMissile;
import com.jahepi.tank.entities.powerups.PowerUpFreeze;
import com.jahepi.tank.entities.powerups.PowerUpLaser;
import com.jahepi.tank.entities.powerups.PowerUpLife;
import com.jahepi.tank.entities.powerups.PowerUpMini;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;
import com.jahepi.tank.multiplayer.dto.PowerUpState;

public class PowerUp extends GameEntity {

	public enum TYPE {
		N, // None
		L, // Laser
		S, // Shot
		M, // Mini
		H, // Heal
		F, // Froze
	}
	
	private boolean active;
	private boolean dead;
	private TextureRegion texture;
	private ParticleEffect startEffect;
	private ParticleEffect endEffect;
	private boolean send;
	private TYPE type;
	private Assets assets;
	
	public PowerUp(Assets assets) {
		size.set(2.0f, 2.0f);
		this.assets = assets;
		float randX = MathUtils.random(size.x, Config.WIDTH - size.x);
		float randY = MathUtils.random(size.y, Config.HEIGHT - size.y);
		position.set(randX, randY);
		active = true;
		rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
		rectangle.setPosition(position.x, position.y);
		rectangle.setOrigin(size.x / 2, size.y / 2);
		int rand = MathUtils.random(1, TYPE.values().length - 1);
		type = TYPE.values()[rand];
		if (type == TYPE.S) {
			texture = assets.getNukeItem();
		} else if (type == TYPE.M) {
			texture = assets.getShieldItem();
		} else if (type == TYPE.H) {
			texture = assets.getHealthItem();
		} else if (type == TYPE.F) {
			texture = assets.getFreezeItem();
		} else {
			texture = assets.getEneryItem();
		}
		startEffect = assets.getParticleEffect();
		startEffect.setPosition(position.x, position.y);
		startEffect.start();
		this.rotationSpeed = 40;
	}
	
	public PowerUp(float x, float y, TYPE type, Assets assets) {
		size.set(2.0f, 2.0f);
		this.assets = assets;
		position.set(x, y);
		active = true;
		rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
		rectangle.setPosition(position.x, position.y);
		rectangle.setOrigin(size.x / 2, size.y / 2);
		this.type = type;
		if (type == TYPE.S) {
			texture = assets.getNukeItem();
		} else if (type == TYPE.M) {
			texture = assets.getShieldItem();
		} else if (type == TYPE.H) {
			texture = assets.getHealthItem();
		} else if (type == TYPE.F) {
			texture = assets.getFreezeItem();
		} else {
			texture = assets.getEneryItem();
		}
		startEffect = assets.getParticleEffect();
		startEffect.setPosition(position.x, position.y);
		startEffect.start();
		this.rotationSpeed = 40;
	}

	@Override
	public void render(SpriteBatch batch) {
		if (active) {
			batch.draw(texture, position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
		if (startEffect != null) {
			startEffect.draw(batch);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		if (endEffect != null) {
			endEffect.draw(batch);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.polygon(rectangle.getTransformedVertices());
	}

	@Override
	public void update(float deltatime) {
		rotation += rotationSpeed * deltatime;
		if (startEffect != null) {
			startEffect.update(deltatime);
			if (startEffect.isComplete()) {
				assets.freeParticleEffect(startEffect);
				startEffect = null;
			}
		}
		if (endEffect != null) {
			endEffect.update(deltatime);
			if (endEffect.isComplete()) {
				assets.freeParticleEffect(endEffect);
				endEffect = null;
				dead = true;
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (!active) {
			assets.playAudioItem();
			endEffect = assets.getParticleEffect();
			endEffect.setPosition(position.x, position.y);
			endEffect.start();
		}
		this.active = active;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public TYPE getType() {
		return type;
	}
	
	public static PowerUpStateStrategy getPowerUpStrategy(TYPE type) {
		PowerUpStateStrategy strategy = null;
		if (type == TYPE.S) {
			strategy = new PowerUpBigMissile();
			strategy.setType(type);
		} else if (type == TYPE.L) {
			strategy = new PowerUpLaser();
			strategy.setType(type);
		} else if (type == TYPE.M) {
			strategy = new PowerUpMini();
			strategy.setType(type);
		} else if (type == TYPE.H) {
			strategy = new PowerUpLife();
			strategy.setType(type);
		} else if (type == TYPE.F) {
			strategy = new PowerUpFreeze();
			strategy.setType(type);
		}
		return strategy;
	}
	
	public PowerUpState getState(PowerUpState powerUpState) {
		powerUpState.setX(position.x);
		powerUpState.setY(position.y);
		powerUpState.setType(type);
		return powerUpState;
	}
}
