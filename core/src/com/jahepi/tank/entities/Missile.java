package com.jahepi.tank.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.entities.Tank.TEXTURE_TYPE;
import com.jahepi.tank.multiplayer.dto.MissileState;

public class Missile extends GameEntity {

	private static final String TAG = "Missile";
	public static enum TEXTURE_MISSILE_TYPE {
		MISSILE1, MISSILE2, MISSILE3, MISSILE4, MISSILE5, MISSILE6
	}
	
	private boolean send;
	private boolean hit;
	private boolean dead;
	private TextureRegion texture;
	private ParticleEffect effect;
	private float effectScale;
	private Sound sound;
	protected TEXTURE_MISSILE_TYPE textureType;
	
	public Missile(float x, float y, float rotation, float width, float height, float effectScale, TEXTURE_MISSILE_TYPE textureType, float speed) {
		super();
		this.textureType = textureType;
		size.set(width, height);
		position.set(x, y);
		this.speed = speed;
		this.rotation = rotation;
		rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
		rectangle.setPosition(position.x, position.y);
		rectangle.setOrigin(size.x / 2, size.y / 2);
		rectangle.setRotation(rotation);
		this.effectScale = effectScale;
		if (textureType == TEXTURE_MISSILE_TYPE.MISSILE1) {
			texture = Assets.getInstance().getRocket1();
		} else if (textureType == TEXTURE_MISSILE_TYPE.MISSILE2) {
			texture = Assets.getInstance().getRocket2();
		} else if (textureType == TEXTURE_MISSILE_TYPE.MISSILE3) {
			texture = Assets.getInstance().getRocket3();
		} else if (textureType == TEXTURE_MISSILE_TYPE.MISSILE4) {
			texture = Assets.getInstance().getRocket4();
		} else if (textureType == TEXTURE_MISSILE_TYPE.MISSILE5) {
			texture = Assets.getInstance().getRocket5();
		} else {
			texture = Assets.getInstance().getRocket6();
		}
		Gdx.app.log(TAG, "Created");
	}
	
	public void setSound(Sound sound) {
		this.sound = sound;
	}
	
	public void playSound() {
		if (sound != null) {
			sound.play();
		}
	}

	public void setEffect(ParticleEffect masterEffect) {
		effect = new ParticleEffect(masterEffect);
		effect.scaleEffect(effectScale);
	}
	
	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.polygon(rectangle.getTransformedVertices());
	}
	
	@Override
	public void render(SpriteBatch batch) {
		if (!isHit()) {
			batch.draw(texture, position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
		if (effect != null) {
			effect.draw(batch);
		}
	}
	
	public void update(float deltatime) {
		if (isHit()) {
			if (effect != null) {
				effect.update(deltatime);
				if (effect.isComplete()) {
					dead = true;
					effect.dispose();
					effect = null;
				}
			}
		} else {
			position.x += (MathUtils.cosDeg(rotation) * speed) * deltatime;
			position.y += (MathUtils.sinDeg(rotation) * speed) * deltatime;
			rectangle.setPosition(position.x, position.y);
		}
	}
	
	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public boolean isOutOfBounds() {
		if (getX() < 0 || getX() > Config.WIDTH) {
			return true;
		}
		if (getY() < 0 || getY() > Config.HEIGHT) {
			return true;
		}
		return false;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		Assets.getInstance().getDestroySound().play();
		effect.setPosition(position.x, position.y);
		effect.start();
		this.hit = hit;
	}

	public MissileState getState() {
		MissileState missileState = new MissileState();
		missileState.setX(position.x);
		missileState.setY(position.y);
		missileState.setRotation(rotation);
		missileState.setSpeed(speed);
		missileState.setTextureType(textureType);
		return missileState;
	}
	
	public static TEXTURE_MISSILE_TYPE getRandomTextureType() {
		int rand = MathUtils.random(0, TEXTURE_TYPE.values().length - 1);
		return TEXTURE_MISSILE_TYPE.values()[rand];
	}
}
