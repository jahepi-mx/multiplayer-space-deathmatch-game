package com.jahepi.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
import com.jahepi.tank.entities.Tile;
import com.jahepi.tank.input.Joystick;
import com.jahepi.tank.levels.Level;
import com.jahepi.tank.multiplayer.dto.GameState;

public class Render implements Disposable, ControllerListener {

	private static final String TAG = "Render";
	
	private TankField tankField;
	private OrthographicCamera camera;
	private OrthographicCamera mapCamera;
	private OrthographicCamera uiCamera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Controller controller;
	private Stage stage;
	private Label endLabel;
	private Label disconnectLabel;
	private Label waitingLabel;
	private Label fightLabel;
	private Button rematchBtn, startBtn;
	private TextButton speedBtn;
	private boolean isShooting;
	private boolean resetFlag;
	private Assets assets;
	private Joystick joystick;
	
	public Render(TankField tankFieldParam, GameChangeStateListener gameStateChangeListener) {
		this.tankField = tankFieldParam;
		this.shapeRenderer = tankField.getDebugRender();
		this.batch = tankField.getBatch();
		assets = Assets.getInstance();
		joystick = new Joystick();
		joystick.setSize(150, 150);
		joystick.setXOffset(10);
		
		controller = new Controller(gameStateChangeListener, this, tankField.isServer(), tankField.getName());
		
		camera = new OrthographicCamera(controller.getCameraHelper().getWidth(), controller.getCameraHelper().getHeight());
		camera.position.x = controller.getCameraHelper().getX();
		camera.position.y = controller.getCameraHelper().getY();
		
		mapCamera = new OrthographicCamera(Config.WIDTH, Config.HEIGHT);
		mapCamera.position.x = Config.WIDTH / 2;
		mapCamera.position.y = Config.HEIGHT / 2;
		mapCamera.update();
		
		uiCamera = new OrthographicCamera(Config.UI_CAMERA_WIDTH * Config.UI_CAMERA_WIDTH_RATIO, Config.UI_CAMERA_HEIGHT * Config.UI_CAMERA_HEIGHT_RATIO);
		uiCamera.position.x = (Config.UI_CAMERA_WIDTH * Config.UI_CAMERA_WIDTH_RATIO) / 2;
		uiCamera.position.y = (Config.UI_CAMERA_WIDTH * Config.UI_CAMERA_HEIGHT_RATIO) / 2;
		uiCamera.update();
		
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, this.batch);
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = assets.getUIFont();
		LabelStyle labelStyleSmall = new LabelStyle();
		labelStyleSmall.font = assets.getUIFontSmall();
		LabelStyle fightLabelStyle = new LabelStyle();
		fightLabelStyle.font = assets.getUIFontTitle();

		fightLabel = new Label(Language.getInstance().get("fight_label"), fightLabelStyle);
		fightLabel.setPosition((Config.UI_WIDTH / 2) - (fightLabel.getWidth() / 2), (Config.UI_HEIGHT / 2) + fightLabel.getHeight());
		fightLabel.setVisible(false);
		stage.addActor(fightLabel);

		waitingLabel = new Label(controller.isServer() ? Language.getInstance().get("waiting_label") : Language.getInstance().get("waiting_opponent_label"), labelStyle);
		waitingLabel.setColor(Color.RED);
		waitingLabel.setPosition((Config.UI_WIDTH / 2) - (waitingLabel.getWidth() / 2), (Config.UI_HEIGHT / 2) + waitingLabel.getHeight());
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
					startBtn.setDisabled(false);
					waitingLabel.setVisible(true);
					rematchBtn.setVisible(false);
					endLabel.setVisible(false);
					controller.reset();
				}
			}	
		});

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
				tankField.changeScreen(SCREEN_TYPE.GAMEOPTIONS);
				tankField.closeConnection();
			}	
		});
		
		if (controller.isServer()) {
			Label startLabel = new Label(Language.getInstance().get("start_game_btn"), labelStyle);
			startBtn = new Button(skin);
			startBtn.add(startLabel);
			startBtn.setHeight(startLabel.getHeight());
			startBtn.setWidth(startLabel.getWidth() + 10.0f);
			startBtn.setX((Config.UI_WIDTH / 2) - (startLabel.getWidth() / 2));
			startBtn.setY(Config.UI_HEIGHT - startLabel.getHeight());
			startBtn.setColor(Color.GREEN);
			stage.addActor(startBtn);
			
			startBtn.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (!startBtn.isDisabled()) {
						startBtn.setDisabled(true);
						controller.setStarted(true);
						waitingLabel.setVisible(false);
					}
				}	
			});
			float padding = 10.0f;
			float left = (Config.UI_WIDTH / 2) - ((startBtn.getWidth() + exitBtn.getWidth() + padding) / 2);
			float right = (Config.UI_WIDTH / 2) + ((startBtn.getWidth() + exitBtn.getWidth() + padding) / 2);
			startBtn.setX(right - startBtn.getWidth());
			exitBtn.setX(left);
		}

		float marginRight = 10;
		float marginBottom = 10;
		TextureRegionDrawable shootButtonTexture = new TextureRegionDrawable(assets.getButton1());
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.up = shootButtonTexture;
		buttonStyle.font = assets.getUIFontSmall();
		TextButton shootBtn = new TextButton(Language.getInstance().get("shoot_btn"), buttonStyle);
		shootBtn.getLabel().setAlignment(Align.center);
		shootBtn.setSize(100, 100);
		shootBtn.setX(Config.UI_WIDTH - shootBtn.getWidth() - marginRight);
		shootBtn.setY(marginBottom);
		stage.addActor(shootBtn);
		shootBtn.addListener(new ClickListener() {
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

		TextureRegionDrawable speedButtonTexture = new TextureRegionDrawable(assets.getButton2());
		TextButton.TextButtonStyle speedButtonStyle = new TextButton.TextButtonStyle();
		speedButtonStyle.up = speedButtonTexture;
		speedButtonStyle.font = assets.getUIFontSmall();
		speedBtn = new TextButton(Language.getInstance().get("speed_btn"), speedButtonStyle);
		speedBtn.getLabel().setAlignment(Align.center);
		speedBtn.setSize(100, 100);
		speedBtn.setX(shootBtn.getX() - speedBtn.getWidth() - marginRight);
		speedBtn.setY(marginBottom);
		stage.addActor(speedBtn);
		speedBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				controller.speedUp();
				return true;
			}
		});

		TextureRegionDrawable joystickBtnTexture = new TextureRegionDrawable(assets.getControlField());
		ImageButton joystickBtn = new ImageButton(joystickBtnTexture);
		joystickBtn.setX(joystick.getXOffset());
		joystickBtn.setSize(joystick.getWidth(), joystick.getHeight());
		stage.addActor(joystickBtn);
		joystickBtn.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				joystick.setPosition(joystick.getXOffset() + x, joystick.getYOffset() + y);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				joystick.resetPosition();
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				joystick.setPosition(joystick.getXOffset() + x, joystick.getYOffset() + y);
			}
		});
		
		Gdx.input.setInputProcessor(stage);
	}
	
	public void updateGameState(GameState gameState) {
		controller.updateGameState(gameState);
	}
	
	public void removeOpponent(String id) {
		controller.removeOpponent(id);
	}
	
	public void showDisconnectError() {
		//disconnectLabel.setVisible(true);
	}
	
	public void render() {

		float deltatime = Gdx.graphics.getDeltaTime();

		camera.position.x = controller.getCameraHelper().getX();
		camera.position.y = controller.getCameraHelper().getY();
		camera.update();

		float left = camera.position.x - (Config.CAMERA_WIDTH / 2);
		float right = camera.position.x + (Config.CAMERA_WIDTH / 2);
		float bottom = camera.position.y - (Config.CAMERA_HEIGHT / 2);
		float top = camera.position.y + (Config.CAMERA_HEIGHT / 2);

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
		
		if (controller.isTankDead()) {
			ShaderProgram shader = assets.getMonochromeShader();
			batch.setShader(shader);
			shader.setUniformf("u_amount", 1.0f);
		} else {
			batch.setShader(null);
		}

		Level level = controller.getLevel();
		if (level != null) {
			batch.draw(level.getBackground(), 0, 0, Config.WIDTH, Config.HEIGHT);
			for (Tile tile : level.getTileMap()) {
				if (tile.isOnArea(left, right, bottom, top)) {
					tile.render(batch);
					// This must be updated on the controller but i changed to the render class to save an iteration process
					tile.update(deltatime);
				}
			}
		}

		controller.getTank().renderMissiles(batch, left, right, bottom, top);
		controller.getTank().render(batch);
		for (PowerUp powerUp : controller.getPowerUps()) {
			if (powerUp != null && powerUp.isOnArea(left, right, bottom, top)) {
				powerUp.render(batch);
			}
		}

		for (OpponentTank opponentTank : controller.getOpponentTanks()) {
			opponentTank.renderMissiles(batch, left, right, bottom, top);
			opponentTank.render(batch);
		}
		batch.end();

		fightLabel.setVisible(controller.showFight());
		waitingLabel.setVisible(!controller.isStarted());
		Tank tank = controller.getTank();
		if (tank.getSpeedUpReloadPercentage() < 100) {
			speedBtn.setText("" + tank.getSpeedUpReloadPercentage());
			speedBtn.getLabel().setFontScale(2.5f);
		}	else {
			speedBtn.setText(Language.getInstance().get("speed_btn"));
			speedBtn.getLabel().setFontScale(1.0f);
		}
		
		stage.draw();
		
		uiCamera.position.x = camera.position.x * Config.UI_WIDTH_RATIO;
		uiCamera.position.y = camera.position.y * Config.UI_HEIGHT_RATIO;
		uiCamera.update();
		
		batch.setColor(1, 1, 1, 1);
		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();
		controller.getTank().renderName(batch);
		for (OpponentTank opponentTank : controller.getOpponentTanks()) {
			opponentTank.renderName(batch);
		}
		batch.end();

		batch.setColor(1, 1, 1, 1);
		batch.setProjectionMatrix(stage.getCamera().combined);
		batch.begin();
		float x = 0;
		float size = 80;
		float marginLeft = 12;
		float marginTopLife = 20;
		float marginTopWins = 50;
		float y = Config.UI_HEIGHT;
		batch.draw(assets.getLife1(), x, y - size, size, size);
		float life = ((float) controller.getTankLife() / (float) Tank.LIFE * 100);
		assets.getUIFontMain().draw(batch, (life > 0 ? (int) life : 0) + "", x + marginLeft, y - marginTopLife);
		assets.getUIFontExtraSmall().draw(batch, String.format(Language.getInstance().get("wins_label"), controller.getTankWins()), x + marginLeft, y - marginTopWins);
		if (Config.SHOW_FPS) {
			assets.getUIFontSmall().draw(batch, "" + Gdx.graphics.getFramesPerSecond(), x + Config.UI_WIDTH - 25, y - 5);
		}
		float lineBreak = 160.0f;
		for (OpponentTank opponentTank : controller.getOpponentTanks()) {
			float opponentLife = ((float) opponentTank.getLife() / (float) Tank.LIFE * 100);
			batch.draw(assets.getLife2(), x, y - lineBreak, size, size);
			assets.getUIFontExtraSmall().setColor(Color.RED);
			assets.getUIFontExtraSmall().draw(batch, opponentTank.getName(), x + (marginLeft * 5.4f), y - lineBreak + size);
			assets.getUIFontExtraSmall().setColor(Color.WHITE);
			assets.getUIFontOpponent().draw(batch, (opponentLife > 0 ? (int) opponentLife : 0) + "", x + marginLeft, y - lineBreak + size - marginTopLife);
			assets.getUIFontExtraSmall().draw(batch, String.format(Language.getInstance().get("wins_label"), opponentTank.getWins()), x + marginLeft, y - lineBreak + size - marginTopWins);
			lineBreak += size;
		}
		batch.end();
		
		if (isShooting) {
			controller.shoot(deltatime);
		} else {
			controller.onReleaseShoot();
		}
		
		controller.update(deltatime);
		
		if (controller.isGameOver()) {
			return;
		}
		
		if (Config.DEBUG) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin();
			controller.getTank().debugRender(shapeRenderer);
			ArrayIterator<OpponentTank> opponents2 = new ArrayIterator<OpponentTank>(controller.getOpponentTanks());
			while (opponents2.hasNext()) {
				OpponentTank opponentTank = opponents2.next();
				opponentTank.debugRender(shapeRenderer);
			}
			for (PowerUp powerUp : controller.getPowerUps()) {
				if (powerUp != null) {
					powerUp.debugRender(shapeRenderer);
				}
			}
			if (level != null) {
				for (Tile tile : level.getTileMap()) {
					tile.debugRender(shapeRenderer);
				}
			}
			shapeRenderer.end();
		}

		drawMap(shapeRenderer);
		renderJoystick(batch);
	}
	
	public void drawMap(ShapeRenderer renderer) {
		shapeRenderer.setProjectionMatrix(mapCamera.combined);
		float mapWidth = Config.WIDTH * Config.MAP_SCALE_FACTOR;
		float mapHeight = Config.HEIGHT * Config.MAP_SCALE_FACTOR;
		renderer.setColor(Color.WHITE);
		float x = (Config.WIDTH / 2) - (mapWidth / 2);
		float y = Config.HEIGHT - mapHeight - 5.0f;
		shapeRenderer.begin();
		renderer.rect(x, y, 0, 0, mapWidth, mapHeight, 1, 1, 0);
		shapeRenderer.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		Tank tank = controller.getTank();
		if (tank != null) {
			float width = tank.getWidth() * Config.MAP_SCALE_FACTOR;
			float height = tank.getHeight() * Config.MAP_SCALE_FACTOR;
			float xTank = tank.getX() * Config.MAP_SCALE_FACTOR;
			float yTank = tank.getY() * Config.MAP_SCALE_FACTOR;
			float rotation = tank.getRotation();
			renderer.setColor(Color.GREEN);
			renderer.rect(x + xTank, y + yTank, width / 2, height / 2, width, height, 1, 1, rotation);
		}
		
		ArrayIterator<OpponentTank> opponents = new ArrayIterator<OpponentTank>(controller.getOpponentTanks());
		renderer.setColor(Color.RED);
		while (opponents.hasNext()) {
			OpponentTank opponentTank = opponents.next();
			float width = opponentTank.getWidth() * Config.MAP_SCALE_FACTOR;
			float height = opponentTank.getHeight() * Config.MAP_SCALE_FACTOR;
			float xTank = opponentTank.getX() * Config.MAP_SCALE_FACTOR;
			float yTank = opponentTank.getY() * Config.MAP_SCALE_FACTOR;
			float rotation = opponentTank.getRotation();
			renderer.rect(x + xTank, y + yTank, width / 2, height / 2, width, height, 1, 1, rotation);
		}
		if (controller.getLevel() != null) {
			renderer.setColor(Color.BROWN);
			for (Tile tile : controller.getLevel().getTileMap()) {
				if (tile != null) {
					float xTile = tile.getX() * Config.MAP_SCALE_FACTOR;
					float yTile = tile.getY() * Config.MAP_SCALE_FACTOR;
					float size = tile.getWidth() * Config.MAP_SCALE_FACTOR;
					renderer.rect(x + xTile, y + yTile, size, size);
				}
			}
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
		shapeRenderer.end();
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
		tankField.showInterstitial();
		endLabel.setVisible(true);
		endLabel.setFontScale(3);
		endLabel.pack();
		endLabel.setPosition((Config.UI_WIDTH / 2) - (endLabel.getWidth() / 2), Config.UI_HEIGHT / 2);
		if (controller.isServer()) {
			rematchBtn.setVisible(true);
		}
	}

	public void renderJoystick(SpriteBatch batch) {
		batch.setProjectionMatrix(stage.getCamera().combined);
		joystick.update();
		joystick.render(batch);
		if (joystick.isActive()) {
			controller.joystickRotate(joystick.getDegrees());
			controller.right(joystick.getDistancePercentage());
		}
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void dispose() {
		stage.clear();
	}
}
