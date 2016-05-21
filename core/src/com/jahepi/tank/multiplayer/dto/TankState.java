package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.entities.PowerUp;
import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;
import com.jahepi.tank.entities.Tank.TEXTURE_TYPE;

public class TankState {

	private String id, name;
	private float x, y, rotation, points, velocity;
	private Array<MissileState> missiles;
	private Array<PowerUp.TYPE> powerUps;
	private boolean shooting, removed;
	private int life, wins;
	private TEXTURE_TYPE textureType;
	private TEXTURE_MISSILE_TYPE missileTextureType;
	private boolean megaShoot;
	
	public TankState() {
		powerUps = new Array<PowerUp.TYPE>();
		missiles = new Array<MissileState>();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Array<MissileState> getMissiles() {
		return missiles;
	}

	public void setMissiles(Array<MissileState> missiles) {
		this.missiles = missiles;
	}
	
	public void addMissile(MissileState missileState) {
		this.missiles.add(missileState);
	}
	
	public void addPowerUp(PowerUp.TYPE type) {
		this.powerUps.add(type);
	}
	
	public Array<PowerUp.TYPE> getPowerUps() {
		return this.powerUps;
	}

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	public void setPowerUps(Array<PowerUp.TYPE> powerUps) {
		this.powerUps = powerUps;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public boolean isShooting() {
		return shooting;
	}

	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}

	public TEXTURE_TYPE getTextureType() {
		return textureType;
	}

	public void setTextureType(TEXTURE_TYPE textureType) {
		this.textureType = textureType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public TEXTURE_MISSILE_TYPE getMissileTextureType() {
		return missileTextureType;
	}

	public void setMissileTextureType(TEXTURE_MISSILE_TYPE missileTextureType) {
		this.missileTextureType = missileTextureType;
	}

	public boolean isMegaShoot() {
		return megaShoot;
	}

	public void setMegaShoot(boolean megaShoot) {
		this.megaShoot = megaShoot;
	}
}
