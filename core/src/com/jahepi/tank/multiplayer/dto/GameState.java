package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;

public class GameState {

	// Id of the main player sending the data through sockets
	private String id;
	private int levelIndex;
	private Array<TankState> tankStates;
	private boolean isPlaying, isStarted;
	private boolean win;
	private String winner;
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

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevelIndex() {
		return levelIndex;
	}

	public void setLevelIndex(int levelIndex) {
		this.levelIndex = levelIndex;
	}
}
