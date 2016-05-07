package com.jahepi.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Controller.ControllerListener;
import com.jahepi.tank.Controller.GameChangeStateListener;
import com.jahepi.tank.TankField.SCREEN_TYPE;
import com.jahepi.tank.entities.OpponentTank;
import com.jahepi.tank.entities.PowerUp;
import com.jahepi.tank.entities.Tank;
import com.jahepi.tank.multiplayer.dto.GameState;

public class Render implements Disposable, ControllerListener {

	private static final String TAG = "Render";
	
	private TankField tankField;
	private OrthographicCamera camera;
	private OrthographicCamera mapCamera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Controller controller;
	private Stage stage;
	private Label lifeLabel;
	private Label endLabel;
	private Label disconnectLabel;
	private Label waitingLabel;
	private Label enemyLifeLabel;
	private Label winLabel;
	private Label winEnemyLabel;
	private Button rematchBtn;
	private boolean isShooting;
	private boolean isMovingLeft;
	private boolean isMovingRight;
	private boolean isRotatingUp;
	private boolean isRotatingDown;
	private boolean resetFlag;
	
	public Render(TankField tankFieldParam, GameChangeStateListener gameStateChangeListener) {
		this.tankField = tankFieldParam;
		this.shapeRenderer = tankField.getDebugRender();
		this.batch = tankField.getBatch();
		
		controller = new Controller(gameStateChangeListener, this, tankField.isServer());
		
		camera = new OrthographicCamera(controller.getCameraHelper().getWidth(), controller.getCameraHelper().getHeight());
		camera.position.x = controller.getCameraHelper().getX();
		camera.position.y = controller.getCameraHelper().getY();
		
		mapCamera = new OrthographicCamera(Config.WIDTH, Config.HEIGHT);
		mapCamera.position.x = Config.WIDTH / 2;
		mapCamera.position.y = Config.HEIGHT / 2;
		mapCamera.update();
		
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, this.batch);
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = Assets.getInstance().getUIFont();
		LabelStyle labelStyleSmall = new LabelStyle();
		labelStyleSmall.font = Assets.getInstance().getUIFontSmall();
		
		waitingLabel = new Label(Language.getInstance().get("waiting_label"), labelStyle);
		waitingLabel.setColor(Color.RED);
		waitingLabel.setPosition((Config.UI_WIDTH / 2) - (waitingLabel.getWidth() / 2), (Config.UI_HEIGHT / 2) + waitingLabel.getHeight());
		waitingLabel.setVisible(tankField.isServer());
		stage.addActor(waitingLabel);
		
		disconnectLabel = new Label(Language.getInstance().get("disconnect_opponent_label"), labelStyle);
		disconnectLabel.setColor(Color.RED);
		disconnectLabel.setVisible(false);
		disconnectLabel.setPosition((Config.UI_WIDTH / 2) - (disconnectLabel.getWidth() / 2), Config.UI_HEIGHT / 2);
		stage.addActor(disconnectLabel);
		
		endLabel = new Label("", labelStyle);
		endLabel.setVisible(false);
		stage.addActor(endLabel);
		
		Label rematchLabel = new Label(Language.getInstance().get("rematch_label"), labelStyle);
		rematchBtn = new Button(skin);
		rematchBtn.add(rematchLabel);
		rematchBtn.setHeight(rematchLabel.getHeight());
		rematchBtn.setWidth(rematchLabel.getWidth() + 10.0f);
		rematchBtn.setX((Config.UI_WIDTH / 2) - (rematchBtn.getWidth() / 2));
		rematchBtn.setY((Config.UI_HEIGHT / 2) - (rematchBtn.getHeight() / 2));
		rematchBtn.setColor(Color.BLUE);
		rematchBtn.setVisible(false);
		stage.addActor(rematchBtn);
		
		rematchBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (tankField.isServer()) {
					rematchBtn.setVisible(false);
					endLabel.setVisible(false);
					controller.reset();
				}
			}	
		});
		
		boolean leftSide = tankField.isServer();
		float marginRight = 20.0f;
		lifeLabel = new Label(String.format(Language.getInstance().get("life_label"), controller.getTankLife()), labelStyle);
		float x = leftSide ? 0 :  Config.UI_WIDTH - lifeLabel.getWidth() - marginRight;
		float y = Config.UI_HEIGHT;
		lifeLabel.setX(x);
		lifeLabel.setY(y - lifeLabel.getHeight());
		stage.addActor(lifeLabel);
		
		winLabel = new Label(String.format(Language.getInstance().get("wins_label"), controller.getTankWins()), labelStyle);
		winLabel.setColor(Color.GREEN);
		winLabel.setX(x);
		winLabel.setY(lifeLabel.getY() - winLabel.getHeight());
		stage.addActor(winLabel);
		
		// Change here for opponents life label
		enemyLifeLabel = new Label(String.format(Language.getInstance().get("opponent_life_label"), controller.getOpponntTankLife()), labelStyle);
		x = !leftSide ? 0 :  Config.UI_WIDTH - enemyLifeLabel.getWidth() - marginRight;
		enemyLifeLabel.setX(x);
		enemyLifeLabel.setY(y - enemyLifeLabel.getHeight());
		stage.addActor(enemyLifeLabel);
		
		// Change here for opponents win label
		winEnemyLabel = new Label(String.format(Language.getInstance().get("wins_label"), controller.getOpponentTankWins()), labelStyle);
		winEnemyLabel.setColor(Color.GREEN);
		winEnemyLabel.setX(x);
		winEnemyLabel.setY(enemyLifeLabel.getY() - winEnemyLabel.getHeight());
		stage.addActor(winEnemyLabel);
		
		Label exitLabel = new Label(Language.getInstance().get("quit_btn"), labelStyle);
		Button exitBtn = new Button(skin);
		exitBtn.add(exitLabel);
		exitBtn.setHeight(exitLabel.getHeight());
		exitBtn.setWidth(exitLabel.getWidth() + 10.0f);
		exitBtn.setX((Config.UI_WIDTH / 2) - (exitBtn.getWidth() / 2));
		exitBtn.setY(Config.UI_HEIGHT - exitBtn.getHeight());
		exitBtn.setColor(Color.RED);
		stage.addActor(exitBtn);
		
		exitBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On click ...");
				tankField.changeScreen(SCREEN_TYPE.MAIN);
				tankField.closeConnection();
			}	
		});
		
		float padX = Config.UI_WIDTH * 0.07f;
		float padY = Config.UI_WIDTH * 0.07f;
		
		TextureRegionDrawable leftArrow = new TextureRegionDrawable(Assets.getInstance().getLeftArrow());
		TextureRegionDrawable leftArrowOn = new TextureRegionDrawable(Assets.getInstance().getLeftArrowOn());
		ImageButton leftImageBtn = new ImageButton(leftArrow, leftArrowOn);
		leftImageBtn.setY(padY);
		leftImageBtn.setOrigin(leftImageBtn.getWidth() / 2, leftImageBtn.getHeight() / 2);
		stage.addActor(leftImageBtn);
		leftImageBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				isMovingLeft = true;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isMovingLeft = false;
			}
		});
		
		TextureRegionDrawable rightArrow = new TextureRegionDrawable(Assets.getInstance().getRightArrow());
		TextureRegionDrawable rightArrowOn = new TextureRegionDrawable(Assets.getInstance().getRightArrowOn());
		ImageButton rightImageBtn = new ImageButton(rightArrow, rightArrowOn);
		rightImageBtn.setX(leftImageBtn.getX() + leftImageBtn.getWidth() + padX);
		rightImageBtn.setY(padY);
		stage.addActor(rightImageBtn);
		rightImageBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				isMovingRight = true;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isMovingRight = false;
			}
		});
		
		TextureRegionDrawable upArrow = new TextureRegionDrawable(Assets.getInstance().getTopArrow());
		TextureRegionDrawable upArrowOn = new TextureRegionDrawable(Assets.getInstance().getTopArrowOn());
		ImageButton upImageBtn = new ImageButton(upArrow, upArrowOn);
		upImageBtn.setX(((rightImageBtn.getX() + rightImageBtn.getWidth()) / 2) - (upImageBtn.getWidth() / 2));
		upImageBtn.setY(leftImageBtn.getY() + padY);
		stage.addActor(upImageBtn);
		upImageBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				isRotatingUp = true;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isRotatingUp = false;
			}
		});
		
		TextureRegionDrawable downArrow = new TextureRegionDrawable(Assets.getInstance().getBottomArrow());
		TextureRegionDrawable bottomArrowOn = new TextureRegionDrawable(Assets.getInstance().getBottomArrowOn());
		ImageButton downImageBtn = new ImageButton(downArrow, bottomArrowOn);
		downImageBtn.setX(((rightImageBtn.getX() + rightImageBtn.getWidth()) / 2) - (downImageBtn.getWidth() / 2));
		downImageBtn.setY(leftImageBtn.getY() - padY);
		stage.addActor(downImageBtn);
		downImageBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				isRotatingDown = true;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isRotatingDown = false;
			}
		});
		
		TextureRegionDrawable shootButtonTexture = new TextureRegionDrawable(Assets.getInstance().getShootButton());
		TextureRegionDrawable shootButtonOn = new TextureRegionDrawable(Assets.getInstance().getShootButtonOn());
		ImageButton shootImageBtn = new ImageButton(shootButtonTexture, shootButtonOn);
		shootImageBtn.setX(Config.UI_WIDTH - shootImageBtn.getWidth());
		shootImageBtn.setY(padY);
		stage.addActor(shootImageBtn);
		shootImageBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				isShooting = true;
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				isShooting = false;
			}
		});
		
		Gdx.input.setInputProcessor(stage);
	}
	
	public void updateGameState(GameState gameState) {
		controller.updateGameState(gameState);
		if (!tankField.isServer()) {
			// Change this for opponents labels.
			int opponentLife = 0; //gameState.getOpponentTankState() != null ? gameState.getOpponentTankState().getLife() : 0;
			int opponentWins = 0; //gameState.getOpponentTankState() != null ? gameState.getOpponentTankState().getWins() : 0;
			lifeLabel.setText(String.format(Language.getInstance().get("life_label"), opponentLife));
			winLabel.setText(String.format(Language.getInstance().get("wins_label"), opponentWins));
			//enemyLifeLabel.setText(String.format(Language.getInstance().get("opponent_life_label"), gameState.getTankState().getLife()));
			//winEnemyLabel.setText(String.format(Language.getInstance().get("wins_label"), gameState.getTankState().getWins()));
		}
	}
	
	public void removeOpponent(String id) {
		controller.removeOpponent(id);
	}
	
	public void showDisconnectError() {
		disconnectLabel.setVisible(true);
	}
	
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.position.x = controller.getCameraHelper().getX();
		camera.position.y = controller.getCameraHelper().getY();
		camera.update();
		
		// Assign id to main player if it is a client socket.
		if (tankField.isNewConnection()) {
			disconnectLabel.setVisible(false);
			tankField.setNewConnection(false);
			controller.reset();
			controller.setTankId(tankField.getConnectionId());
			if (tankField.isServer()) {
				waitingLabel.setVisible(false);
			}
		}
		
		batch.setColor(1, 1, 1, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Assets.getInstance().getBackground(), 0, 0, Config.WIDTH, Config.HEIGHT);
		controller.getTank().render(batch);
		ArrayIterator<OpponentTank> opponents = new ArrayIterator<OpponentTank>(controller.getOpponentTanks());
		while (opponents.hasNext()) {
			OpponentTank opponentTank = (OpponentTank) opponents.next();
			opponentTank.render(batch);
		}
		for (PowerUp powerUp : controller.getPowerUps()) {
			if (powerUp != null) {
				powerUp.render(batch);
			}
		}
		batch.end();
		
		if (tankField.isServer()) {
			lifeLabel.setText(String.format(Language.getInstance().get("life_label"), controller.getTankLife()));
			winLabel.setText(String.format(Language.getInstance().get("wins_label"), controller.getTankWins()));
			enemyLifeLabel.setText(String.format(Language.getInstance().get("opponent_life_label"), controller.getOpponntTankLife()));
			winEnemyLabel.setText(String.format(Language.getInstance().get("wins_label"), controller.getOpponentTankWins()));
		}
		
		stage.draw();
		
		if (controller.isGameOver()) {
			return;
		}
		
		float deltatime = Gdx.graphics.getDeltaTime();
		
		if (isShooting) {
			controller.shoot();
		} else {
			controller.onReleaseShoot();
		}
		
		if (isMovingLeft) {
			controller.left();
		}
		
		if (isMovingRight) {
			controller.right();
		}
		
		if (isRotatingUp) {
			controller.rotateUp();
		}
		
		if (isRotatingDown) {
			controller.rotateDown();
		}
		
		controller.update(deltatime);
		
		if (Config.DEBUG) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin();
			controller.getTank().debugRender(shapeRenderer);
			ArrayIterator<OpponentTank> opponents2 = new ArrayIterator<OpponentTank>(controller.getOpponentTanks());
			while (opponents2.hasNext()) {
				OpponentTank opponentTank = (OpponentTank) opponents2.next();
				opponentTank.debugRender(shapeRenderer);
			}
			for (PowerUp powerUp : controller.getPowerUps()) {
				if (powerUp != null) {
					powerUp.debugRender(shapeRenderer);
				}
			}
			shapeRenderer.end();
		}
		
		shapeRenderer.setProjectionMatrix(mapCamera.combined);
		shapeRenderer.begin();
		drawMap(shapeRenderer);
		shapeRenderer.end();
	}
	
	public void drawMap(ShapeRenderer renderer) {
		float mapWidth = Config.WIDTH * Config.MAP_SCALE_FACTOR;
		float mapHeight = Config.HEIGHT * Config.MAP_SCALE_FACTOR;
		renderer.setColor(Color.GREEN);
		float x = (Config.WIDTH / 2) - (mapWidth / 2);
		float y = Config.HEIGHT - mapHeight - 5.0f;
		renderer.rect(x, y, 0, 0, mapWidth, mapHeight, 1, 1, 0);
		Tank tank = controller.getTank();
		if (tank != null) {
			float width = tank.getWidth() * Config.MAP_SCALE_FACTOR;
			float height = tank.getHeight() * Config.MAP_SCALE_FACTOR;
			float xTank = tank.getX() * Config.MAP_SCALE_FACTOR;
			float yTank = tank.getY() * Config.MAP_SCALE_FACTOR;
			float rotation = tank.getRotation();
			renderer.rect(x + xTank, y + yTank, width / 2, height / 2, width, height, 1, 1, rotation);
		}
		
		ArrayIterator<OpponentTank> opponents = new ArrayIterator<OpponentTank>(controller.getOpponentTanks());
		while (opponents.hasNext()) {
			OpponentTank opponentTank = (OpponentTank) opponents.next();
			float width = opponentTank.getWidth() * Config.MAP_SCALE_FACTOR;
			float height = opponentTank.getHeight() * Config.MAP_SCALE_FACTOR;
			float xTank = opponentTank.getX() * Config.MAP_SCALE_FACTOR;
			float yTank = opponentTank.getY() * Config.MAP_SCALE_FACTOR;
			float rotation = opponentTank.getRotation();
			renderer.rect(x + xTank, y + yTank, width / 2, height / 2, width, height, 1, 1, rotation);
		}
		Array<PowerUp> powerUps = controller.getPowerUps();
		renderer.setColor(Color.YELLOW);
		for (PowerUp powerUp : powerUps) {
			if (powerUp != null) {
				float xPowerUp = powerUp.getX() * Config.MAP_SCALE_FACTOR;
				float yPowerUp = powerUp.getY() * Config.MAP_SCALE_FACTOR;
				float size = powerUp.getWidth() * Config.MAP_SCALE_FACTOR;
				renderer.rect(x + xPowerUp, y + yPowerUp, size, size);
			}
		}
	}

	@Override
	public void onWinMatch() {
		resetFlag = false;
		endLabel.setText(Language.getInstance().get("win_label"));
		showRematchOptions();
	}

	@Override
	public void onLostMatch() {
		resetFlag = false;
		endLabel.setText(Language.getInstance().get("lost_label"));
		showRematchOptions();
	}
	
	@Override
	public void onPlaying() {
		if (!resetFlag) {
			resetFlag = true;
			rematchBtn.setVisible(false);
			endLabel.setVisible(false);
			controller.reset();
		}
	}

	private void showRematchOptions() {
		endLabel.setVisible(true);
		endLabel.setFontScale(3);
		endLabel.pack();
		endLabel.setPosition((Config.UI_WIDTH / 2) - (endLabel.getWidth() / 2), Config.UI_HEIGHT / 2);
		rematchBtn.setVisible(true);
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void dispose() {
		stage.clear();
	}
}
