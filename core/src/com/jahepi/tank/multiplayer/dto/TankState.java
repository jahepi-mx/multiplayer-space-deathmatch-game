package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.entities.PowerUp;

public class TankState {

	private String id;
	private float x, y, rotation, points;
	private Array<MissileState> missiles;
	private Array<PowerUp.TYPE> powerUps;
	private boolean shooting, removed;
	private int life, wins;
	
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
}
