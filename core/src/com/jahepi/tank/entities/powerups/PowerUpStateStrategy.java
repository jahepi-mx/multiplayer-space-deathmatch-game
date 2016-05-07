package com.jahepi.tank.entities.powerups;

import com.jahepi.tank.entities.GameEntity;
import com.jahepi.tank.entities.PowerUp.TYPE;

public abstract class PowerUpStateStrategy {

	protected boolean active;
	protected float time;
	protected float timeLimit;
	protected boolean send;
	protected TYPE type;
	protected GameEntity entity;
	
	public PowerUpStateStrategy() {
		active = true;
		time = 0;
	}

	public void setEntity(GameEntity entity) {
		this.entity = entity;
	}

	public void update(float deltatime) {
		if (active) {
			onUpdate();
			time += deltatime;
			if (time > timeLimit) {
				onFinish();
				active = false;
			}
		}
	}
	
	public abstract void start();
	
	public abstract void onUpdate();
	
	public abstract void onFinish();

	public boolean isActive() {
		return active;
	}

	public float getTimeLimit() {
		return timeLimit;
	}

	public void addTimeLimit(float timeLimit) {
		this.timeLimit += timeLimit;
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

	public void setType(TYPE type) {
		this.type = type;
	}
	
	public boolean equals(PowerUpStateStrategy strategy) {
		return this.type == strategy.type;
	}
}
