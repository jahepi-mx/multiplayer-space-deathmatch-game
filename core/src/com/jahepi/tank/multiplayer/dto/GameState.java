package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;

public class GameState {

	private String id;
	private Array<TankState> tankStates;
	private boolean isPlaying;
	private boolean win;
	private Array<PowerUpState> powerUps;
	
	public GameState() {
		powerUps = new Array<PowerUpState>();
		tankStates = new Array<TankState>();
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public Array<PowerUpState> getPowerUps() {
		return powerUps;
	}

	public void setPowerUps(Array<PowerUpState> powerUps) {
		this.powerUps = powerUps;
	}

	public Array<TankState> getTankStates() {
		return tankStates;
	}

	public void addTankState(TankState tankState) {
		this.tankStates.add(tankState);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
