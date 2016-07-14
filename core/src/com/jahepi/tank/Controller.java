package com.jahepi.tank;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jahepi.tank.entities.Missile;
import com.jahepi.tank.entities.OpponentTank;
import com.jahepi.tank.entities.PowerUp;
import com.jahepi.tank.entities.Tank;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;
import com.jahepi.tank.levels.Level;
import com.jahepi.tank.levels.LevelFactory;
import com.jahepi.tank.mem.GameStateManager;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.multiplayer.dto.PowerUpState;
import com.jahepi.tank.multiplayer.dto.TankState;

public class Controller {

	private enum GAME_STATUS {
		PLAYING, GAMEOVER
	}
	
	private Tank tank;
	private Array<OpponentTank> opponentTanks;
	private Array<Tank> temporalHolder;
	private boolean isServer;
	private boolean win, started;
	private String winner;
	private ControllerListener controllerListener;
	private GameChangeStateListener gameChangeStateListener;
	private GAME_STATUS gameStatus;
	private Array<PowerUp> powerUps;
	private float powerUpTime;
	private float powerUpInterval;
	private float fightTime;
	private CameraHelper cameraHelper;
	private LevelFactory levelFactory;
	private Level level;
	private GameState gameState;
	private GameStateManager gameStateManager;
	private float deltatimeRegister;
	private float deltatimeLimit;
	private Assets assets;

	public Controller(GameChangeStateListener gameChangeStateListener, ControllerListener controllerListener, boolean isServer, String name, Assets assets) {
		winner = "";
		temporalHolder = new Array<Tank>();
		this.assets = assets;
		opponentTanks = new Array<OpponentTank>();
		tank = new Tank(name, Tank.getTextureType(assets.getPlayer()), Missile.getRandomTextureType(), this.assets);
		tank.setMissileSize(1.0f, 0.5f);
		this.controllerListener = controllerListener;
		this.gameChangeStateListener = gameChangeStateListener;
		this.powerUps = new Array<PowerUp>();
		this.isServer = isServer;
		gameStatus = GAME_STATUS.PLAYING;
		powerUpInterval = MathUtils.random(5.0f, 15.0f);
		cameraHelper = new CameraHelper(Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT, (Config.WIDTH / 2) - (Config.CAMERA_WIDTH / 2), (Config.HEIGHT / 2) - (Config.CAMERA_HEIGHT / 2));
		levelFactory = new LevelFactory(this.assets);
		gameState = new GameState();
		gameStateManager = new GameStateManager();
	}
	
	public void setTankId(String connectionId) {
		tank.setId(connectionId);
	}
	
	public void removeOpponent(String id) {
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null && opponent.getId().equals(id)) {
				opponent.setRemoved(true);
				break;
			}
		}
	}
	
	private void createOpponentInstances(GameState gameState) {
		for (TankState tankState : gameState.getTankStates()) {
			boolean found = false;
			if (tankState != null) {
				if (tank.getId().equals(tankState.getId())) {
					found = true;
				}
				for (OpponentTank opponent : opponentTanks) {
					if (opponent != null && opponent.getId().equals(tankState.getId())) {
						found = true;
						break;
					}
				}
				if (!found) {
					OpponentTank opponent = new OpponentTank(tankState.getName(), tankState.getTextureType(), tankState.getMissileTextureType(), assets);
					opponent.setMissileSize(1.0f, 0.5f);
					opponent.setId(tankState.getId());
					opponentTanks.add(opponent);
				}
			}
		}
	}
	
	public void updateGameState(GameState gameState) {
		boolean firstTime = false;
		createOpponentInstances(gameState);
		if (!isServer) {
			level = levelFactory.getLevel(gameState.getLevelIndex());
			started = gameState.isStarted();
			if (!gameState.isPlaying()) {
				gameStatus = GAME_STATUS.GAMEOVER;
				if (gameState.isWin()) {
					if (gameState.getWinner().equals(tank.getId())) {
						controllerListener.onWinMatch();
					} else {
						controllerListener.onLostMatch();
					}
				}
			} else {
				gameStatus = GAME_STATUS.PLAYING;
				controllerListener.onPlaying();
			}
			for (PowerUpState powerUpState : gameState.getPowerUps()) {
				powerUps.add(new PowerUp(powerUpState.getX(), powerUpState.getY(), powerUpState.getType(), assets));
			}
		}
		for (OpponentTank opponent : opponentTanks) {
			for (TankState tankState : gameState.getTankStates()) {
				if (!isServer) {
					if (tank.getId().equals(tankState.getId()) && !firstTime) {
						tank.setLife(tankState.getLife());
						tank.setWins(tankState.getWins());
						for (PowerUpState powerUpState : tankState.getPendingPowerUps()) {
							powerUps.add(new PowerUp(powerUpState.getX(), powerUpState.getY(), powerUpState.getType(), assets));
						}
						firstTime = true;
					}
				}
				if (opponent != null && opponent.getId().equals(tankState.getId())) {
					opponent.updateState(tankState, !isServer);
				}
			}
		}
	}
	
	public boolean isPlaying() {
		return gameStatus == GAME_STATUS.PLAYING;
	}
	
	public boolean isGameOver() {
		return gameStatus == GAME_STATUS.GAMEOVER;
	}
	
	private void setAliveTanks() {
		if (!tank.isDead()) {
			temporalHolder.add(tank);
		}
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null && !opponent.isDead()) {
				temporalHolder.add(opponent);
			}
		}
	}
	
	private void checkIfFinish() {
		if (isPlaying() && isServer && started && opponentTanks.size >= 1) {
			setAliveTanks();
			if (temporalHolder.size == 1) {
				Tank tank = temporalHolder.get(0);
				tank.addWin();
				winner = tank.getId();
				gameStatus = GAME_STATUS.GAMEOVER;
				win = true;
				started = false;
				if (this.tank == tank) {
					controllerListener.onWinMatch();
				} else {
					controllerListener.onLostMatch();
				}
			}
			temporalHolder.clear();
		}
	}
	
	public void update(float deltatime) {

		if (!started) {
			fightTime = 0;
		}

		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null && opponent.isReadyRemove()) {
				opponentTanks.removeValue(opponent, true);
			}
		}
		
		checkIfFinish();
		
		cameraHelper.setX(tank.getX() + (tank.getWidth() / 2));
		cameraHelper.setY(tank.getY() + (tank.getHeight() / 2));
		OpponentTank tempOpponent = null;
		float tempDist = Config.WIDTH_HEIGHT_DST;
		
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null) {
				float dist = tank.distance2(opponent.getX(), opponent.getY());
				if (dist < tempDist) {
					tempDist = dist;
					tempOpponent = opponent;
				}
			}
		}

		if (tempOpponent != null) {
			float dist = tank.distance(tempOpponent.getX(), tempOpponent.getY());
			if (dist <= Config.CAMERA_CENTER_DISTANCE) {
				float width = Math.abs(tank.getX() - tempOpponent.getX()) / 2;
				float height = Math.abs(tank.getY() - tempOpponent.getY()) / 2;
				float x = Math.min(tank.getX(), tempOpponent.getX()) + width + (tank.getWidth() / 2);
				float y = Math.min(tank.getY(), tempOpponent.getY()) + height + (tank.getHeight() / 2);
				cameraHelper.setX(x);
				cameraHelper.setY(y);
			}
		}
		cameraHelper.update();
		
		if (isPlaying()) {
			
			if (isServer && started) {
				powerUpTime += deltatime;
				if (powerUpTime >= powerUpInterval) {
					powerUps.add(new PowerUp(assets));
					powerUpTime = 0;
				}
			}
	
			tank.update(deltatime);
			tank.isHitLevel(level);
			tank.checkLevelCollision(deltatime, level);
			
			if (started) {
				fightTime += deltatime;
				for (OpponentTank opponent : opponentTanks) {
					if (opponent != null) {
						// Check if main ship collide with missiles of the opponents
						opponent.isHit(tank);
						opponent.isHitLevel(level);
						tank.isHit(opponent);
						// Check if each opponent´s missile collide against the rest of opponents
						for (int e = 0; e < opponentTanks.size; e++) {
							OpponentTank innerOpponent = opponentTanks.get(e);
							if (innerOpponent != null && opponent != innerOpponent) {
								opponent.isHit(innerOpponent);
							}
						}
						opponent.update(deltatime);
					}
				}
			} else {
				// Just update opponents position if the match has not started
				for (OpponentTank opponent : opponentTanks) {
					if (opponent != null) {
						opponent.isHitLevel(level);
						opponent.update(deltatime);
					}
				}
			}
		}

		// Send data through sockets N times per second
		float fps = 1 / deltatime;
		deltatimeLimit = (fps / 16) / fps;
		deltatimeRegister += deltatime;

		if (deltatimeRegister >= deltatimeLimit) {
			gameState.reset();
		}
		
		for (PowerUp powerUp : powerUps) {
			if (powerUp != null) {
				
				powerUp.update(deltatime);
				if (powerUp.isActive() && powerUp.collide(tank.getRectangle())) {
					powerUp.setActive(false);
					PowerUpStateStrategy powerUpStrategy = PowerUp.getPowerUpStrategy(powerUp.getType());
					tank.addPowerUpStrategy(powerUpStrategy);
				}
				
				for (OpponentTank opponent : opponentTanks) {
					if (opponent != null && powerUp.isActive() && powerUp.collide(opponent.getRectangle())) {
						powerUp.setActive(false);
					}
					if (isServer && opponent != null && powerUp.isActive()) {
						if (opponent.isNew()) {
							opponent.addPendingPowerUp(powerUp);
						}
					}
				}
				
				if (!powerUp.isSend()) {
					if (deltatimeRegister >= deltatimeLimit) {
						gameState.getPowerUps().add(powerUp.getState(gameStateManager.obtainPowerUpState()));
						powerUp.setSend(true);
					}
				}
				
				if (powerUp.isDead()) {
					powerUps.removeValue(powerUp, true);
				}
			}
		}

		if (isServer) {
			for (OpponentTank opponent : opponentTanks) {
				if (opponent != null && opponent.isNew()) {
					opponent.setIsNew(false);
				}
			}
		}

		if (deltatimeRegister >= deltatimeLimit) {
			gameState.setPlaying(isPlaying());
			gameState.setWin(win);
			gameState.setWinner(winner);
			gameState.setStarted(started);
			gameState.setId(tank.getId());
			gameState.addTankState(tank.getState(gameStateManager));
			if (isServer) {
				gameState.setLevelIndex(levelFactory.getSelectedLevel());
				for (OpponentTank opponent : opponentTanks) {
					if (opponent != null) {
						gameState.addTankState(opponent.getState(gameStateManager));
					}
				}
			}
			gameChangeStateListener.onGameChangeState(gameState);
			gameState.free(gameStateManager);
			deltatimeRegister = 0;
		}
	}
	
	public void speedUp() {
		tank.speedUp();
	}
	
	public void right(float percentage) {
		tank.right(percentage);
	}
	
	public void shoot(float deltatime) {
		tank.shoot(deltatime);
	}
	
	public void onReleaseShoot() {
		tank.onReleaseShoot();
	}

	public int getTankWins() {
		return tank.getWins();
	}
	
	public int getTankLife() {
		return tank.getLife();
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public boolean isTankDead() {
		if (tank != null) {
			return tank.isDead();
		}
		return false;
	}
	
	public boolean isServer() {
		return isServer;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isNotStarted() {
		return !started && !win;
	}

	public Array<OpponentTank> getOpponentTanks() {
		return opponentTanks;
	}
	
	public Array<PowerUp> getPowerUps() {
		return powerUps;
	}
	
	public CameraHelper getCameraHelper() {
		return cameraHelper;
	}

	public void joystickRotate(float degrees) {
		tank.setTargetRotation(degrees);
	}

	public Level getLevel() {
		return level;
	}

	public boolean showFight() {
		if (started && fightTime <= 1.0f) {
			return true;
		}
		return false;
	}

	public void reset() {
		powerUps.clear();
		gameStatus = GAME_STATUS.PLAYING;
		tank.reset();
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null) {
				opponent.reset();
			}
		}
		win = false;
	}

	public void setLevel(int index) {
		if (isServer) {
			level = levelFactory.getLevel(index);
		}
	}
	
	public interface ControllerListener {
		void onWinMatch();
		void onLostMatch();
		void onPlaying();
	}
	
	public interface GameChangeStateListener {
		void onGameChangeState(GameState gameState);
	}
}
