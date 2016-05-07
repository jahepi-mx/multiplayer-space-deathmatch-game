package com.jahepi.tank;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.jahepi.tank.entities.OpponentTank;
import com.jahepi.tank.entities.PowerUp;
import com.jahepi.tank.entities.Tank;
import com.jahepi.tank.entities.powerups.PowerUpStateStrategy;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.multiplayer.dto.PowerUpState;
import com.jahepi.tank.multiplayer.dto.TankState;

public class Controller {

	private static enum GAME_STATUS {
		PLAYING, GAMEOVER
	}
	
	private Tank tank;
	private Array<OpponentTank> opponentTanks;
	private boolean isServer;
	private boolean win;
	private ControllerListener controllerListener;
	private GameChangeStateListener gameChangeStateListener;
	private GAME_STATUS gameStatus;
	private Array<PowerUp> powerUps;
	private float powerUpTime;
	private float powerUpInterval;
	private CameraHelper cameraHelper;
	
	public Controller(GameChangeStateListener gameChangeStateListener, ControllerListener controllerListener, boolean isServer) {
		Assets assets = Assets.getInstance();
		opponentTanks = new Array<OpponentTank>();
		if (isServer) {
			tank = new Tank(assets.getShip1(), assets.getRocket1(), assets.getEffect2(), assets.getAudio1());
			tank.setMissileSize(1.0f, 0.5f);
		} else {
			tank = new Tank(assets.getShip2(), assets.getRocket2(), assets.getEffect1(), assets.getAudio2());
			tank.setMissileSize(0.8f, 0.8f);
		}
		this.controllerListener = controllerListener;
		this.gameChangeStateListener = gameChangeStateListener;
		this.powerUps = new Array<PowerUp>();
		this.isServer = isServer;
		gameStatus = GAME_STATUS.PLAYING;
		if (isServer) {
			tank.startOnLeftSide();
		} else {
			tank.startOnRightSide();
		}
		powerUpInterval = MathUtils.random(10.0f, 20.0f);
		cameraHelper = new CameraHelper(Config.CAMERA_WIDTH, Config.CAMERA_HEIGHT, (Config.WIDTH / 2) - (Config.CAMERA_WIDTH / 2), (Config.HEIGHT / 2) - (Config.CAMERA_HEIGHT / 2));
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
				if (found == false) {
					Assets assets = Assets.getInstance();
					OpponentTank opponent = new OpponentTank(assets.getShip1(), assets.getRocket1(), assets.getEffect2(), assets.getAudio1());
					opponent.setMissileSize(1.0f, 0.5f);
					opponent.setId(tankState.getId());
					opponentTanks.add(opponent);
				}
			}
		}
	}
	
	public void updateGameState(GameState gameState) {
		createOpponentInstances(gameState);
		if (!isServer) {
			if (!gameState.isPlaying()) {
				gameStatus = GAME_STATUS.GAMEOVER;
				if (!gameState.isWin()) {
					controllerListener.onWinMatch();
				} else {
					controllerListener.onLostMatch();
				}
			} else {
				gameStatus = GAME_STATUS.PLAYING;
				controllerListener.onPlaying();
			}
			for (PowerUpState powerUpState : gameState.getPowerUps()) {
				powerUps.add(new PowerUp(powerUpState.getX(), powerUpState.getY(), powerUpState.getType()));
			}
		}
		for (OpponentTank opponent : opponentTanks) {
			for (TankState tankState : gameState.getTankStates()) {
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
	
	private Array<Tank> getAliveTanks() {
		Array<Tank> tanks = new Array<Tank>();
		if (!tank.isDead()) {
			tanks.add(tank);
		}
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null && !opponent.isDead()) {
				tanks.add(opponent);
			}
		}
		return tanks;
	}
	
	private void checkIfFinish() {
		if (isPlaying() && isServer) {
			Array<Tank> aliveTanks = getAliveTanks();
			if (aliveTanks.size == 8) {
				Tank tank = aliveTanks.get(0);
				tank.addWin();
				gameStatus = GAME_STATUS.GAMEOVER;
				win = true;
				if (this.tank == tank) {
					controllerListener.onWinMatch();
				} else {
					controllerListener.onLostMatch();
				}
			}
		}
	}
	
	public void update(float deltatime) {
		
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null && opponent.isCommitRemove()) {
				opponentTanks.removeValue(opponent, true);
			}
		}
		
		checkIfFinish();
		
		cameraHelper.setX(tank.getX());
		cameraHelper.setY(tank.getY());
		OpponentTank tempOpponent = null;
		float tempDist = Config.WIDTH;
		
		for (OpponentTank opponent : opponentTanks) {
			if (opponent != null) {
				float dist = tank.distance(opponent.getX(), opponent.getY());
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
			
			if (isServer) {
				powerUpTime += deltatime;
				if (powerUpTime >= powerUpInterval) {
					powerUps.add(new PowerUp());
					powerUpTime = 0;
				}
			}
			
			if (Gdx.app.getType() == ApplicationType.Desktop) {
				if (Gdx.input.isKeyPressed(Keys.A)) {
					tank.left();
				}
				if (Gdx.input.isKeyPressed(Keys.D)) {
					tank.right();
				}
				if (Gdx.input.isKeyPressed(Keys.W)) {
					tank.rotateUp();
				}
				if (Gdx.input.isKeyPressed(Keys.S)) {
					tank.rotateDown();
				}
				if (Gdx.input.isKeyPressed(Keys.SPACE)) {
					tank.shoot();
				} else {
					tank.onReleaseShoot();
				}
			}
	
			tank.update(deltatime);
			
			for (OpponentTank opponent : opponentTanks) {
				if (opponent != null) {
					// Check if main ship collide with missile of the opponents
					opponent.isHit(tank);
					tank.isHit(opponent);
					// Check if each opponent´s missile collide against the rest of opponents
					ArrayIterator<OpponentTank> iterator = new ArrayIterator<OpponentTank>(opponentTanks);
					while (iterator.hasNext()) {
						OpponentTank innerOpponent = (OpponentTank) iterator.next();
						if (innerOpponent != null && opponent != innerOpponent) {
							opponent.isHit(innerOpponent);
						}
					}
					opponent.update(deltatime);
				}
			}
		}
		
		GameState gameState = new GameState();
		
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
				}
				
				if (!powerUp.isSend()) {
					gameState.getPowerUps().add(powerUp.getState());
					powerUp.setSend(true);
				}
				
				if (powerUp.isDead()) {
					powerUps.removeValue(powerUp, true);
				}
			}
		}
		
		gameState.setPlaying(isPlaying());
		gameState.setWin(win);
		gameState.setId(tank.getId());
		gameState.addTankState(tank.getState());
		
		if (isServer) {
			for (OpponentTank opponent : opponentTanks) {
				if (opponent != null) {
					gameState.addTankState(opponent.getState());
				}
			}
		}
		gameChangeStateListener.onGameChangeState(gameState);
	}
	
	public void left() {
		tank.left();
	}
	
	public void right() {
		tank.right();
	}
	
	public void rotateUp() {
		tank.rotateUp();
	}
	
	public void rotateDown() {
		tank.rotateDown();
	}
	
	public void shoot() {
		tank.shoot();
	}
	
	public void onReleaseShoot() {
		tank.onReleaseShoot();
	}

	public int getTankWins() {
		return tank.getWins();
	}
	
	public int getOpponentTankWins() {
		return 0;
	}
	
	public int getTankLife() {
		return tank.getLife();
	}
	
	public int getOpponntTankLife() {
		return 0;
	}
	
	public Tank getTank() {
		return tank;
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
	
	public interface ControllerListener {
		void onWinMatch();
		void onLostMatch();
		void onPlaying();
	}
	
	public interface GameChangeStateListener {
		void onGameChangeState(GameState gameState);
	}
}
