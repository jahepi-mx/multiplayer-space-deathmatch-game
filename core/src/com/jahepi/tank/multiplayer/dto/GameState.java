package com.jahepi.tank.multiplayer.dto;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jahepi.tank.mem.GameStateManager;

public class GameState implements Pool.Poolable {

	// Id of the main player sending the data through sockets
	private String i;
	private int l;
	private Array<TankState> ts;
	private boolean p, s;
	private boolean w;
	private String wr;
	private Array<PowerUpState> ps;
	
	public GameState() {
		ps = new Array<PowerUpState>();
		ts = new Array<TankState>();
	}

	public boolean isPlaying() {
		return p;
	}

	public void setPlaying(boolean isPlaying) {
		this.p = isPlaying;
	}

	public boolean isWin() {
		return w;
	}

	public void setWin(boolean win) {
		this.w = win;
	}

	public Array<PowerUpState> getPowerUps() {
		return ps;
	}

	public void setPowerUps(Array<PowerUpState> powerUps) {
		this.ps = powerUps;
	}

	public Array<TankState> getTankStates() {
		return ts;
	}

	public void addTankState(TankState tankState) {
		this.ts.add(tankState);
	}

	public boolean isStarted() {
		return s;
	}

	public void setStarted(boolean isStarted) {
		this.s = isStarted;
	}

	public String getWinner() {
		return wr;
	}

	public void setWinner(String winner) {
		this.wr = winner;
	}

	public String getId() {
		return i;
	}

	public void setId(String id) {
		this.i = id;
	}

	public int getLevelIndex() {
		return l;
	}

	public void setLevelIndex(int levelIndex) {
		this.l = levelIndex;
	}

	@Override
	public void reset() {
		i = null;
		l = 0;
		ts.clear();
		p = false;
		s = false;
		w = false;
		wr = null;
		ps.clear();
	}

	public void free(GameStateManager gameStateManager) {
		for (PowerUpState powerUpState : ps) {
			gameStateManager.freePowerUpState(powerUpState);
		}
		for (TankState tankState : ts) {
			for (MissileState missileState : tankState.getMissiles()) {
				gameStateManager.freeMissileState(missileState);
			}
			for (PowerUpState powerUpState : tankState.getPendingPowerUps()) {
				gameStateManager.freePowerUpState(powerUpState);
			}
			gameStateManager.freeTankState(tankState);
		}
	}
}
