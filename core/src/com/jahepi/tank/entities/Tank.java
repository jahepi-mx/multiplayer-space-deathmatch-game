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
import com.jahepi.tank.multiplayer.dto.MissileState;
import com.jahepi.tank.multiplayer.dto.TankState;

public class Tank extends GameEntity {

	public static final String TAG = "Tank";
	public static final float DEFAULT_VELOCITY = 10.0f;
	public static final float FRICTION = 0.99f;
	public static final float MEGA_SHOOT_TIME = 2.0f;
	public static final float MEGA_SHOOT_ANIMATION_TIME = 0.5f;
	public static final int LIFE = 50;
	public static enum TEXTURE_TYPE {
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
	protected float missileEffectScale = 1.0f;
	protected float missileSpeed = 11.0f;
	protected TEXTURE_TYPE textureType;
	protected BitmapFont font;
	protected float speedUpTime;
	protected float lastSpeedUpTime;
	protected boolean activeSpeedUpTime;
	protected Animation speedUpAnimation;
	protected Animation megaShootAnimation;
	protected TextureRegion speedUpTexture;
	protected float time;
	protected Assets assets;
	
	public Tank(String name, TEXTURE_TYPE textureType, TEXTURE_MISSILE_TYPE missileTextureType, ParticleEffect effect, Sound sound) {
		super();
		assets = Assets.getInstance();
		this.name = name;
		font = assets.getUIFontExtraSmall();
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
	
	@Override
	public void render(SpriteBatch batch) {
		laser.render(batch);
		for (Missile missile : missiles) {
			if (missile != null) {
				missile.render(batch);
			}
		}
		effect.draw(batch);
		if (isDead()) {
			batch.draw(assets.getSKull(), position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, 90.0f, true);
		} else {
			batch.draw(texture, position.x, position.y, size.x / 2, size.y / 2, size.x, size.y, 1.0f, 1.0f, rotation, true);
		}
		
		if (velocity > DEFAULT_VELOCITY && speedUpTexture != null) {
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
            Missile missile = new Missile(position.x, position.y, rotation, 4.0f, 4.0f, 3.0f, TEXTURE_MISSILE_TYPE.MISSILE7, missileSpeed, 5, true);
            missile.setEffect(effect);
            missile.setSound(sound);
            missile.playSound();
            missiles.add(missile);
        }
	}
	
	public void shoot() {
		if (!isDead()) {
			megaShootTime += Gdx.graphics.getDeltaTime();
			laser.shoot();
			if (!shooting) {
				Vector2 position = Util.getRotationPosition(size.x, size.y, getX(), getY(), rotation);
				Missile missile = new Missile(position.x, position.y, rotation, missileSize.x, missileSize.y, missileEffectScale, missileTextureType, missileSpeed, missileDamage, true);
				missile.setEffect(effect);
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
		float diff = speedUpTime - lastSpeedUpTime;
		if (!activeSpeedUpTime) {
			if (diff > 0.1f && diff < 0.4f) {
				activeSpeedUpTime = true;
				velocity = 17.0f;
				speedUpTime = 0;
				assets.playAudioSpeedUp();
			}
		}
		lastSpeedUpTime = speedUpTime;
	}
	
	public void right() {
		speed = this.velocity;	
		if (isDead()) {
			speed = 0;
		}
	}
	
	public void rotateUp() {
		rotationSpeed = 90.0f;
		if (isDead()) {
			rotationSpeed = 0;
		}
	}
	
	public void rotateDown() {
		rotationSpeed = -90.0f;
		if (isDead()) {
			rotationSpeed = 0;
		}
	}
	
	public void update(float deltatime) {
		speedUpTime += deltatime;
		time += deltatime;
		speed *= FRICTION;
		rotationSpeed *= FRICTION;
		rotation += rotationSpeed * deltatime;
		
		if (activeSpeedUpTime) {
			if (speedUpTime >= 1.0f) {
				velocity = DEFAULT_VELOCITY;
				activeSpeedUpTime = false;
			}
		}
		
		if (velocity > DEFAULT_VELOCITY) {
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
	
	public void startEffect() {
		effect.reset();
		effect.start();
		effect.setPosition(position.x, position.y);
		assets.playDestroySound();
	}
	
	public void setRectangleSize(float width, float height) {
		rectangle.setOrigin(width / 2, height / 2);
		rectangle.setScale(width / defaultSize, height / defaultSize);
		rectangle.setVertices(new float[] {0, 0, width, 0, width, height, 0, height});
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
		return tankState;
	}

	public void updateState(TankState tankState, boolean isSend) {
		position.set(tankState.getX(), tankState.getY());
		rotation = tankState.getRotation();
		rectangle.setPosition(position.x, position.y);
		rectangle.setRotation(rotation);
		shooting = tankState.isShooting();
		removed = tankState.isRemoved();
		velocity = tankState.getVelocity();
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
			Missile missile = new Missile(missileState.getX(), missileState.getY(), missileState.getRotation(), missileState.getWidth(), missileState.getHeight(), missileState.getEffectScale(), missileState.getTextureType(), missileState.getSpeed(), missileState.getDamage(), false);
			missile.setEffect(effect);
			missile.setSound(sound);
			missile.playSound();
			if (isSend) {
				missile.setSend(true);
			}
			missiles.add(missile);
		}
		for (TYPE type : tankState.getPowerUps()) {
			PowerUpStateStrategy strategy = PowerUp.getPowerUpStrategy(type);
			if (isSend) {
				strategy.setSend(true);
			}
			addPowerUpStrategy(strategy);
		}
	}
	
	public static TEXTURE_TYPE getRandomTextureType() {
		int rand = MathUtils.random(0, TEXTURE_TYPE.values().length - 1);
		return TEXTURE_TYPE.values()[rand];
	}
}
