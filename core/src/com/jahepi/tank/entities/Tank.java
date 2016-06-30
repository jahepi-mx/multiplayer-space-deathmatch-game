package com.jahepi.tank.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Util;
import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;
import com.jahepi.tank.entities.PowerUp.TYPE;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;
import com.jahepi.tank.levels.Level;
import com.jahepi.tank.multiplayer.dto.MissileState;
import com.jahepi.tank.multiplayer.dto.TankState;

public class Tank extends GameEntity {

	public static final String TAG = "Tank";
	public static final float DEFAULT_VELOCITY = 10.0f;
	public static final float FRICTION = 0.99f;
	public static final float MEGA_SHOOT_TIME = 2.0f;
	public static final float MEGA_SHOOT_ANIMATION_TIME = 0.5f;
	public static final float SPEED_UP_TIME = 5.0f;
	public static final int LIFE = 100;
	public enum TEXTURE_TYPE {
		SHIP1, SHIP2, SHIP3, SHIP4, SHIP5
	}
	
	protected Array<Missile> missiles;
	protected Laser laser;
	protected float megaShootTime;
    protected boolean megaShootEnable;
	protected boolean megaShootEnableAnimation;
	protected int life;
	protected int wins;
	protected TextureRegion texture;
	protected ParticleEffect effect;
	protected Sound sound;
	protected Vector2 missileSize;
	protected boolean shooting;
	protected Array<PowerUpStateStrategy> powerUpStrategies;
	protected Array<PowerUpStateStrategy> collectedPowerUpStrategies;
	protected float defaultSize = 2.0f;
	protected int missileDamage = 1;
	protected TEXTURE_MISSILE_TYPE missileTextureType;
	protected float missileEffectScale = Config.MIN_EXPLOSION_SIZE;
	protected float missileSpeed = 11.0f;
	protected TEXTURE_TYPE textureType;
	protected BitmapFont font;
	protected float speedUpTime;
	protected boolean activeSpeedUp;
	protected Animation speedUpAnimation;
	protected Animation megaShootAnimation;
	protected TextureRegion speedUpTexture;
	protected float time;
	protected Assets assets;
	protected float targetRotation;
	protected Vector2 lastPosition;
	protected boolean isNew;
	protected Array<PowerUp> pendingPowerUps;
	
	public Tank(String name, TEXTURE_TYPE textureType, TEXTURE_MISSILE_TYPE missileTextureType, ParticleEffect effect, Sound sound) {
		super();
		isNew = true;
		pendingPowerUps = new Array<PowerUp>();
		assets = Assets.getInstance();
		this.name = name;
		font = assets.getUIFontExtraExtraSmall();
		this.textureType = textureType;
		if (textureType == TEXTURE_TYPE.SHIP1) {
			texture = assets.getShip1();
		} else if (textureType == TEXTURE_TYPE.SHIP2) {
			texture = assets.getShip2();
		} else if (textureType == TEXTURE_TYPE.SHIP3) {
			texture = assets.getShip3();
		} else if (textureType == TEXTURE_TYPE.SHIP4) {
			texture = assets.getShip4();
		}  else {
			texture = assets.getShip5();
		}
		velocity = DEFAULT_VELOCITY;
		this.speedUpAnimation = assets.getSpeedUpAnimation();
		this.megaShootAnimation = assets.getMegaShootAnimation();
		size.set(defaultSize, defaultSize);
		position.set(Config.WIDTH / 2, Config.HEIGHT / 2);
		missiles = new Array<Missile>();
		powerUpStrategies = new Array<PowerUpStateStrategy>();
		collectedPowerUpStrategies = new Array<PowerUpStateStrategy>();
		life = LIFE;
		this.missileTextureType = missileTextureType;
		this.effect = effect;
		this.sound = sound;
		missileSize = new Vector2();
		rectangle.setVertices(new float[] {0, 0, size.x, 0, size.x, size.y, 0, size.y});
		rectangle.setPosition(position.x, position.y);
		rectangle.setOrigin(size.x / 2, size.y / 2);
		laser = new Laser();
		setRandomPosition();
		lastPosition = new Vector2(0, 0);
		Gdx.app.log(TAG, "Created");
	}
	
	private void setRandomPosition() {
		position.set(MathUtils.random(size.x, Config.WIDTH - size.x), MathUtils.random(size.y, Config.HEIGHT - size.y));
		rectangle.setPosition(position.x, position.y);
	}
	
	public void setMissileSpeed(float missileSpeed) {
		this.missileSpeed = missileSpeed;
	}

	public void setMissileSize(float width, float height) {
		missileSize.set(width, height);
	}
	
	public Vector2 getMissileSize() {
		return missileSize;
	}

	public void setMissileDamage(int missileDamage) {
		this.missileDamage = missileDamage;
	}

	public void setMissileEffectScale(float missileEffectScale) {
		this.missileEffectScale = missileEffectScale;
	}
	
	public void setLaserVisible(Boolean visible) {
		laser.setVisible(visible);
	}
	
	public void addPowerUpStrategy(PowerUpStateStrategy powerUpStrategy) {
		boolean found = false;
		collectedPowerUpStrategies.add(powerUpStrategy);
		for (PowerUpStateStrategy strategy : powerUpStrategies) {
			if (strategy != null) {
				if (powerUpStrategy.equals(strategy) && strategy.isActive()) {
					strategy.addTimeLimit(powerUpStrategy.getTimeLimit());
					found = true;
				}
			}
		}
		if (!found) {
			powerUpStrategy.setEntity(this);
			powerUpStrategies.add(powerUpStrategy);
			powerUpStrategy.start();
		}
	}
	
	public boolean isDead() {
		return life <= 0;
	}

	public void renderMissiles(SpriteBatch batch, float left, float right, float bottom, float top) {
		for (Missile missile : missiles) {
			if (missile != null && missile.isOnArea(left, right, bottom, top)) {
				missile.render(batch);
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		laser.render(batch);
		effect.draw(batch);
		if (isDead()) {
			batch.draw(assets.getSkull(), position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, 90.0f, true);
		} else {
			batch.draw(texture, position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
		
		if (activeSpeedUp && speedUpTexture != null) {
			Vector2 vector = Util.getRotationPositionFromBack(size.x, size.y, position.x, position.y, rotation);
			batch.draw(speedUpTexture, vector.x - (size.x / 2), vector.y - (size.y / 2), size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}

		if (megaShootEnableAnimation) {
			Vector2 vector = Util.getRotationPosition(size.x, size.y, position.x, position.y, rotation);
			batch.draw(megaShootAnimation.getKeyFrame(time), vector.x - (size.x / 2), vector.y - (size.y / 2), size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
	}
	
	public void renderName(SpriteBatch batch) {
		font.draw(batch, name, (position.x * Config.UI_WIDTH_RATIO), (position.y * Config.UI_HEIGHT_RATIO));
	}

	@Override
	public void debugRender(ShapeRenderer renderer) {
		renderer.setColor(Color.WHITE);
		renderer.polygon(rectangle.getTransformedVertices());
		for (Missile missile : missiles) {
			missile.debugRender(renderer);
		}
		if (laser.isVisible()) {
			laser.debugRender(renderer);
		}
	}
	
	public void onReleaseShoot() {
		shooting = false;
		laser.releaseShoot();
        megaShootTime = 0;
		megaShootEnableAnimation = false;
        if (megaShootEnable) {
            megaShootEnable = false;
            Vector2 position = Util.getRotationPosition(size.x, size.y, getX(), getY(), rotation);
            Missile missile = new Missile(position.x, position.y, rotation, 4.0f, 4.0f, Config.MAX_EXPLOSION_SIZE, TEXTURE_MISSILE_TYPE.M7, missileSpeed, 5, true);
            missile.setSound(sound);
            missile.playSound();
            missiles.add(missile);
        }
	}
	
	public void shoot(float deltatime) {
		if (!isDead()) {
			megaShootTime += deltatime;
			laser.shoot();
			if (!shooting) {
				Vector2 position = Util.getRotationPosition(size.x, size.y, getX(), getY(), rotation);
				Missile missile = new Missile(position.x, position.y, rotation, missileSize.x, missileSize.y, missileEffectScale, missileTextureType, missileSpeed, missileDamage, true);
				missile.setSound(sound);
				missile.playSound();
				missiles.add(missile);
			}
            if (megaShootTime >= MEGA_SHOOT_TIME) {
                megaShootTime = 0;
                megaShootEnable = true;
            }
			if (megaShootTime >= MEGA_SHOOT_ANIMATION_TIME) {
				megaShootEnableAnimation = true;
			}
			shooting = true;
		}
	}
	
	public void speedUp() {
		if (speedUpTime >= SPEED_UP_TIME) {
			activeSpeedUp = true;
			velocity *= 1.4;
			speed = velocity;
			speedUpTime = 0;
			assets.playAudioSpeedUp();
		}
	}
	
	public void right(float percentage) {
		speed = this.velocity * percentage;
		if (isDead()) {
			speed = 0;
		}
	}

	public void checkLevelCollision(float deltatime, Level level) {
		if (level != null) {
			Tile[] tiles = level.getSurroundedTiles(position.x, position.y);
			//Array<Tile> tiles = level.getTileMap();
			for (Tile tile : tiles) {
				if (tile != null && tile.collide(rectangle)) {
					float x = (tile.getX() + (tile.getWidth() / 2)) - (getX() + (size.x / 2));
					float y = (tile.getY() + (tile.getHeight() / 2)) - (getY() + (size.y / 2));
					float dist = (float) Math.sqrt((x * x) + (y * y));
					float radius = (size.x / 2) + (tile.getWidth() / 2);
					if (dist < radius) {
						float alpha = radius - dist;
						float cosDeg = MathUtils.cosDeg(rotation);
						float sinDeg = MathUtils.sinDeg(rotation);
						if ((x < 0 && cosDeg < 0) || (x > 0 && cosDeg > 0)) {
							position.x = position.x + (-alpha * cosDeg);
						} else {
							position.x = position.x + (alpha * cosDeg);
						}
						if ((y < 0 && sinDeg < 0) || (y > 0 && sinDeg > 0)) {
							position.y = position.y + (-alpha * sinDeg);
						} else {
							position.y = position.y + (alpha * sinDeg);
						}
					}
				}
			}
		}
	}
	
	public void update(float deltatime) {
		speedUpTime += deltatime;
		time += deltatime;
		speed *= FRICTION;

		float alpha = 0.05f;
		float dtheta = targetRotation - rotation;
		if (dtheta > 180) {
			rotation += 360;
		} else if (dtheta < -180) {
			rotation -= 360;
		}
		rotation += (targetRotation - rotation) * alpha;

		if (activeSpeedUp) {
			if (speedUpTime >= 2.5f) {
				activeSpeedUp = false;
				velocity = DEFAULT_VELOCITY;
			}
		}
		
		if (activeSpeedUp) {
			speedUpTexture = speedUpAnimation.getKeyFrame(time);
		}
		
		position.x += (MathUtils.cosDeg(rotation) * speed) * deltatime;
		position.y += (MathUtils.sinDeg(rotation) * speed) * deltatime;		
		rectangle.setPosition(position.x, position.y);
		rectangle.setRotation(rotation);

		if (position.x < 0) {
			position.x = 0;
		}
		
		if (position.x > Config.WIDTH - size.x) {
			position.x = Config.WIDTH - size.x;
		}
		
		if (position.y < 0) {
			position.y = 0;
		}
		
		if (position.y > Config.HEIGHT - size.y) {
			position.y = Config.HEIGHT - size.y;
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
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getWins() {
		return wins;
	}

	public void addWin() {
		this.wins++;
	}
	
	public void isHit(Tank tank) {
		if (!isDead()) {
			for (Missile missile : missiles) {
				if (missile != null && missile.collide(tank.getRectangle()) && !missile.isHit()) {
					missile.setHit(true);
					tank.setLife(tank.getLife() - missile.getDamage());
				}
			}
			if (laser.isHit(tank)) {
				tank.startEffect();
				tank.setLife(tank.getLife() - laser.getDamage());
			}
		}
	}

	public void isHitLevel(Level level) {
		if (level != null) {
			for (Missile missile : missiles) {
				if (missile != null && !missile.isHit()) {
					Tile[] tiles = level.getSurroundedTiles(missile.getX(), missile.getY());
					//Array<Tile> tiles = level.getTileMap();
					for (Tile tile : tiles) {
						if (tile != null && tile.collide(missile.getRectangle())) {
							missile.setHit(true);
						}
					}
				}
			}
		}
	}
	
	public void startEffect() {
		effect.reset();
		effect.start();
		effect.setPosition(position.x, position.y);
		assets.playDestroySound();
	}
	
	public void setRectangleSize(float width, float height) {
		rectangle.setOrigin(width / 2, height / 2);
		rectangle.setScale(width / defaultSize, height / defaultSize);
		rectangle.setVertices(new float[]{0, 0, width, 0, width, height, 0, height});
	}

	public void reset() {
		life = LIFE;
		megaShootTime = 0;
		missiles.clear();
		setRandomPosition();
	}
	
	public TankState getState() {
		TankState tankState = new TankState();
		tankState.setX(position.x);
		tankState.setY(position.y);
		tankState.setRotation(rotation);
		tankState.setShooting(shooting);
		tankState.setLife(life);
		tankState.setWins(wins);
		tankState.setId(id);
		tankState.setRemoved(removed);
		tankState.setVelocity(velocity);
		tankState.setTextureType(textureType);
		tankState.setMissileTextureType(missileTextureType);
		tankState.setName(name);
		tankState.setMegaShoot(megaShootEnableAnimation);
		tankState.setSpeedUp(activeSpeedUp);
		for (Missile missile : missiles) {
			if (missile != null && !missile.isSend()) {
				missile.setSend(true);
				tankState.addMissile(missile.getState());
			}
		}
		for (PowerUpStateStrategy strategy: collectedPowerUpStrategies) {
			if (strategy != null && !strategy.isSend()) {
				strategy.setSend(true);
				tankState.addPowerUp(strategy.getType());
			}
		}
		if (pendingPowerUps.size > 0) {
			for (PowerUp powerUp : pendingPowerUps) {
				tankState.addPendingPowerUp(powerUp.getState());
			}
			pendingPowerUps.clear();
		}
		return tankState;
	}
	
	public static TEXTURE_TYPE getTextureType(int index) {
		//int rand = MathUtils.random(0, TEXTURE_TYPE.values().length - 1);
		return TEXTURE_TYPE.values()[index];
	}

	public void setTargetRotation(float targetRotation) {
		this.targetRotation = targetRotation;
	}

	public int getSpeedUpReloadPercentage() {
		float percentage = speedUpTime / SPEED_UP_TIME;
		if (percentage > 1) {
			return 100;
		}
		return (int) (percentage * 100.0f);
	}

	public boolean isNew() {
		return isNew;
	}

	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void addPendingPowerUp(PowerUp powerUp) {
		pendingPowerUps.add(powerUp);
	}

	public Array<PowerUp> getPendingPowerUps() {
		return pendingPowerUps;
	}
}
