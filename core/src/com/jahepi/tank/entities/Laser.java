package com.jahepi.tank.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;

public class Laser extends GameEntity {

	private static final String TAG = "Laser";
	private static final float DAMAGE_TIME = 1.0f;
	private static final float LASER_SOUND_TIME_LIMIT = 0.1f;
	
	private TextureRegion texture;
	private boolean visible;
	private boolean activated;
	private float damageTime;
	private float laserSoundTime;
	private Assets assets;
	private int damage;
	
	public Laser() {
		float height = 3.0f;
		assets = Assets.getInstance();
		size.set(Config.WIDTH, height);
		rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
		rectangle.setPosition(position.x, position.y);
		rectangle.setOrigin(size.y / 2, size.y / 2);
		rectangle.setRotation(rotation);
		this.texture = assets.getLaser();
		laserSoundTime = LASER_SOUND_TIME_LIMIT;
		damage = 2;
	}

	@Override
	public void render(SpriteBatch batch) {
		if (isVisible() && isActivated()) {
			batch.draw(texture, position.x, position.y, size.y / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		if (isVisible() && isActivated()) {
			renderer.setColor(Color.WHITE);
			renderer.polygon(rectangle.getTransformedVertices());
		}
	}

	@Override
	public void update(float deltatime) {
		laserSoundTime += deltatime;
		rectangle.setRotation(rotation);
		rectangle.setPosition(position.x, position.y);
		if (activated) {
			damageTime += deltatime;
		}
	}
	
	public boolean isHit(Tank tank) {
		if (isVisible() && isActivated()) {
			boolean hit = collide(tank.getRectangle());
			if (hit) {
				if (damageTime > DAMAGE_TIME) {
					damageTime = 0;
					return true;
				}
			}
		}
		return false;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		if (activated) {
			if (laserSoundTime >= LASER_SOUND_TIME_LIMIT) {
				Gdx.app.log(TAG, "Sound: " + laserSoundTime);
				assets.playAudio1();
				laserSoundTime = 0;
			}
		}
		this.activated = activated;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		this.activated = false;
	}
	
	public void shoot() {
		if (isVisible()) {
			setActivated(true);
		}
	}
	
	public void releaseShoot() {
		if (isVisible()) {
			setActivated(false);
		}
	}

	public int getDamage() {
		return damage;
	}
}
