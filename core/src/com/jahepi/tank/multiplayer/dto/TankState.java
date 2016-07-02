package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jahepi.tank.entities.PowerUp;
import com.jahepi.tank.entities.Missile.TEXTURE_MISSILE_TYPE;
import com.jahepi.tank.entities.Tank.TEXTURE_TYPE;

public class TankState implements Pool.Poolable {

	private String i, n;
	private float x, y, r, p, v;
	private Array<MissileState> ms;
	private Array<PowerUp.TYPE> ps;
	private Array<PowerUpState> pus;
	private boolean s, rm;
	private int l, w;
	private TEXTURE_TYPE t;
	private TEXTURE_MISSILE_TYPE mt;
	private boolean m, su;
	
	public TankState() {
		ps = new Array<PowerUp.TYPE>();
		ms = new Array<MissileState>();
		pus = new Array<PowerUpState>();
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
		return r;
	}

	public void setRotation(float rotation) {
		this.r = rotation;
	}

	public Array<MissileState> getMissiles() {
		return ms;
	}

	public void setMissiles(Array<MissileState> missiles) {
		this.ms = missiles;
	}
	
	public void addMissile(MissileState missileState) {
		this.ms.add(missileState);
	}
	
	public void addPowerUp(PowerUp.TYPE type) {
		this.ps.add(type);
	}
	
	public Array<PowerUp.TYPE> getPowerUps() {
		return this.ps;
	}

	public float getPoints() {
		return p;
	}

	public void setPoints(float points) {
		this.p = points;
	}

	public void setPowerUps(Array<PowerUp.TYPE> powerUps) {
		this.ps = powerUps;
	}

	public int getLife() {
		return l;
	}

	public void setLife(int life) {
		this.l = life;
	}

	public int getWins() {
		return w;
	}

	public void setWins(int wins) {
		this.w = wins;
	}

	public boolean isShooting() {
		return s;
	}

	public void setShooting(boolean shooting) {
		this.s = shooting;
	}

	public TEXTURE_TYPE getTextureType() {
		return t;
	}

	public void setTextureType(TEXTURE_TYPE textureType) {
		this.t = textureType;
	}

	public String getName() {
		return n;
	}

	public void setName(String name) {
		this.n = name;
	}

	public String getId() {
		return i;
	}

	public void setId(String id) {
		this.i = id;
	}

	public boolean isRemoved() {
		return rm;
	}

	public void setRemoved(boolean removed) {
		this.rm = removed;
	}

	public float getVelocity() {
		return v;
	}

	public void setVelocity(float velocity) {
		this.v = velocity;
	}

	public TEXTURE_MISSILE_TYPE getMissileTextureType() {
		return mt;
	}

	public void setMissileTextureType(TEXTURE_MISSILE_TYPE missileTextureType) {
		this.mt = missileTextureType;
	}

	public boolean isMegaShoot() {
		return m;
	}

	public void setMegaShoot(boolean megaShoot) {
		this.m = megaShoot;
	}

	public boolean isSpeedUp() {
		return su;
	}

	public void setSpeedUp(boolean speedUp) {
		this.su = speedUp;
	}

	public Array<PowerUpState> getPendingPowerUps() {
		return pus;
	}

	public void addPendingPowerUp(PowerUpState powerUpState) {
		this.pus.add(powerUpState);
	}

	@Override
	public void reset() {
		i = null;
		n = null;
		x = y = r = p = v = 0;
		ms.clear();
		ps.clear();
		pus.clear();
		s = false;
		rm = false;
		m = false;
		su = false;
		l = w = 0;
		t = null;
		mt = null;
	}
}
