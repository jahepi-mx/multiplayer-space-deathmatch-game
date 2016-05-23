package com.jahepi.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
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
import com.jahepi.tank.input.Joystick;
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
	private Button rematchBtn;
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
			Button startBtn = new Button(skin);
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
					controller.setStarted(true);
					waitingLabel.setVisible(false);
				}	
			});
			float padding = 10.0f;
			float left = (Config.UI_WIDTH / 2) - ((startBtn.getWidth() + exitBtn.getWidth() + padding) / 2);
			float right = (Config.UI_WIDTH / 2) + ((startBtn.getWidth() + exitBtn.getWidth() + padding) / 2);
			startBtn.setX(right - startBtn.getWidth());
			exitBtn.setX(left);
		}
		
		TextureRegionDrawable shootButtonTexture = new TextureRegionDrawable(assets.getShootButton());
		TextureRegionDrawable shootButtonOn = new TextureRegionDrawable(assets.getShootButtonOn());
		ImageButton shootImageBtn = new ImageButton(shootButtonTexture, shootButtonOn);
		shootImageBtn.setX(Config.UI_WIDTH - shootImageBtn.getWidth());
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
	}
	
	public void removeOpponent(String id) {
		controller.removeOpponent(id);
	}
	
	public void showDisconnectError() {
		//disconnectLabel.setVisible(true);
	}
	
	public void render() {
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
		
		if (controller.isTankDead()) {
			ShaderProgram shader = assets.getMonochromeShader();
			batch.setShader(shader);
			shader.setUniformf("u_amount", 1.0f);
		} else {
			batch.setShader(null);
		}
		
		batch.draw(assets.getBackground(), 0, 0, Config.WIDTH, Config.HEIGHT);
		controller.getTank().render(batch);
		for (PowerUp powerUp : controller.getPowerUps()) {
			if (powerUp != null) {
				powerUp.render(batch);
			}
		}

		for (OpponentTank opponentTank : controller.getOpponentTanks()) {
			opponentTank.render(batch);
		}
		batch.end();

		waitingLabel.setVisible(!controller.isStarted());
		
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
		float y = Config.UI_HEIGHT;
		batch.draw(assets.getLife1(), x, y - 80, 80, 80);
		float life = ((float) controller.getTankLife() / (float) Tank.LIFE * 100);
		assets.getUIFontMain().draw(batch, (life > 0 ? (int) life : 0) + "", x + 12, y - 20);
		assets.getUIFontExtraSmall().draw(batch, String.format(Language.getInstance().get("wins_label"), controller.getTankWins()), x + 12, y - 50);
		float lineBreak = 122.0f;
		for (OpponentTank opponentTank : controller.getOpponentTanks()) {
			float opponentLife = ((float) opponentTank.getLife() / (float) Tank.LIFE * 100);
			batch.draw(assets.getLife2(), x, y - lineBreak - 40, 80, 80);
			assets.getUIFontExtraSmall().setColor(Color.RED);
			assets.getUIFontExtraSmall().draw(batch, opponentTank.getName(), x + 65, y - lineBreak + 35);
			assets.getUIFontExtraSmall().setColor(Color.WHITE);
			assets.getUIFontOpponent().draw(batch, (opponentLife > 0 ? (int) opponentLife : 0) + "", x + 12, y - lineBreak + 20);
			assets.getUIFontExtraSmall().draw(batch, String.format(Language.getInstance().get("wins_label"), opponentTank.getWins()), x + 12, y - lineBreak - 10);
			lineBreak += 82.0f;
		}
		batch.end();
		
		float deltatime = Gdx.graphics.getDeltaTime();
		
		if (isShooting) {
			controller.shoot();
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
		endLabel.setVisible(true);
		endLabel.setFontScale(3);
		endLabel.pack();
		endLabel.setPosition((Config.UI_WIDTH / 2) - (endLabel.getWidth() / 2), Config.UI_HEIGHT / 2);
		if (controller.isServer()) {
			rematchBtn.setVisible(true);
		}
	}

	public void renderJoystick(SpriteBatch batch) {
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
		batch.setProjectionMatrix(stage.getCamera().combined);
		joystick.update();
		joystick.render(batch);
		if (joystick.isActive()) {
			controller.joystickRotate(joystick.getDegrees());
			controller.right(joystick.getDistancePercentage());
			controller.speedUp();
		}
		joystick.resetPosition();
	}

	@Override
	public void onTouch(float x, float y) {
		Vector3 vector = stage.getCamera().unproject(new Vector3(x, y, 0));
		joystick.setPosition(vector.x, vector.y);
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void dispose() {
		stage.clear();
	}
}
