package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.Language.LANG;
import com.jahepi.tank.TankField;
import com.jahepi.tank.multiplayer.Server;

public class Main implements Screen {

	private static final String TAG = "Main";
	
	private TankField tankField;
	private Stage stage;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Label errorLabel;
	private Label searchLabel;
	private Label titleLabel;
	private Label startServerLabel;
	private Label englishLabel;
	private Label spanishLabel;
	private Label portLabel;
	private TextField portTextField;
	private Button serverBtn;
	private Button englishBtn;
	private Button spanishBtn;
	
	public Main(TankField tankField) {
		this.tankField = tankField;
		this.batch = tankField.getBatch();
		camera = new OrthographicCamera(Config.WIDTH, Config.HEIGHT);
		camera.position.x = Config.WIDTH / 2;
		camera.position.y = Config.HEIGHT / 2;
		camera.update();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, this.batch);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		stage.clear();
		if (!Assets.getInstance().getMusic().isPlaying()) {
			Assets.getInstance().getMusic().play();
		}
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		
		LabelStyle style1 = new LabelStyle();
		BitmapFont uiFont = Assets.getInstance().getUIFont();
		style1.font = uiFont;
		
		startServerLabel = new Label(Language.getInstance().get("start_btn"), style1);
		serverBtn = new Button(skin);
		serverBtn.add(startServerLabel);
		stage.addActor(serverBtn);
		
		LabelStyle style = new LabelStyle();
		style.font = Assets.getInstance().getUIFont();
		errorLabel = new Label("", style);
		errorLabel.setColor(Color.RED);
		stage.addActor(errorLabel);
		
		searchLabel = new Label("", style);
		searchLabel.setColor(Color.WHITE);
		stage.addActor(searchLabel);
		
		LabelStyle styleTitle = new LabelStyle();
		styleTitle.font = Assets.getInstance().getUIFontTitle();
		titleLabel = new Label(String.format(Language.getInstance().get("game_title"), "\n"), styleTitle);
		titleLabel.setAlignment(Align.center);
		stage.addActor(titleLabel);
		
		portTextField = new TextField("", skin);
		portTextField.getStyle().font = Assets.getInstance().getUIFont();
		portTextField.setText(Integer.toString(Server.PORT));
		portTextField.setWidth(100);
		stage.addActor(portTextField);
		
		portLabel = new Label(Language.getInstance().get("port_label"), style);
		portLabel.setColor(Color.GREEN);
		stage.addActor(portLabel);
		
		serverBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On click ...");
				String port = portTextField.getText();
				tankField.start((int) Integer.parseInt(port));
			}	
		});
		
		englishLabel = new Label(Language.getInstance().get("english_btn"), style1);
		englishBtn = new Button(skin);
		englishBtn.add(englishLabel);
		stage.addActor(englishBtn);
		
		englishBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Language.getInstance().load(LANG.ENGLISH);
				updateUITexts();
			}	
		});
		
		spanishLabel = new Label(Language.getInstance().get("spanish_btn"), style1);
		spanishBtn = new Button(skin);
		spanishBtn.add(spanishLabel);
		stage.addActor(spanishBtn);
		
		spanishBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Language.getInstance().load(LANG.SPANISH);
				updateUITexts();
			}	
		});
		
		updateUITexts();
	}
	
	public void updateUITexts() {
		startServerLabel.setText(Language.getInstance().get("start_btn"));
		startServerLabel.pack();
		serverBtn.setWidth(startServerLabel.getWidth() + 10.0f);
		serverBtn.setHeight(startServerLabel.getHeight());
		serverBtn.setX((Config.UI_WIDTH / 2) - (serverBtn.getWidth() / 2));
		serverBtn.setY((Config.UI_HEIGHT / 2) - (serverBtn.getHeight() / 2));
		serverBtn.pack();
		
		int padding = 10;
		titleLabel.setText(String.format(Language.getInstance().get("game_title"), "\n"));
		titleLabel.pack();
		titleLabel.setX((Config.UI_WIDTH / 2) - (titleLabel.getWidth() / 2));
		titleLabel.setY(Config.UI_HEIGHT - titleLabel.getHeight() - padding);
		
		portTextField.setY(serverBtn.getY() - portTextField.getHeight() - padding);
		portTextField.setWidth(100);
		portTextField.pack();
		
		portLabel.setText(Language.getInstance().get("port_label"));
		portLabel.setY(portTextField.getY());
		portLabel.pack();
		
		float width = (portTextField.getWidth() + portLabel.getWidth() + padding) / 2;
		float left = (Config.UI_WIDTH / 2) - width;
		float right = (Config.UI_WIDTH / 2) + width - portTextField.getWidth();
		portLabel.setX(left);
		portTextField.setX(right);
		
		englishLabel.setText(Language.getInstance().get("english_btn"));
		englishLabel.pack();
		englishBtn.setWidth(englishLabel.getWidth() + 10.0f);
		englishBtn.setHeight(englishLabel.getHeight());
		englishBtn.setX(left);
		englishBtn.setY(portTextField.getY() - portTextField.getHeight() - 30.0f);
		englishBtn.pack();
		
		spanishLabel.setText(Language.getInstance().get("spanish_btn"));
		spanishLabel.pack();
		spanishBtn.setWidth(spanishLabel.getWidth() + 10.0f);
		spanishBtn.setHeight(spanishLabel.getHeight());
		spanishBtn.setX(right);
		spanishBtn.setY(portTextField.getY() - portTextField.getHeight() - 30.0f);
		spanishBtn.pack();
	}
	
	public void showConnectionError() {
		errorLabel.setText(Language.getInstance().get("error_network"));
		errorLabel.pack();
		errorLabel.setX((Config.UI_WIDTH / 2) - (errorLabel.getWidth() / 2));
		errorLabel.setY(serverBtn.getY() + errorLabel.getHeight());
	}
	
	public void showDisconnectError() {
		errorLabel.setText(Language.getInstance().get("error_disconnect"));
		errorLabel.pack();
		errorLabel.setX((Config.UI_WIDTH / 2) - (errorLabel.getWidth() / 2));
		errorLabel.setY(serverBtn.getY() + errorLabel.getHeight());
	}
	
	public void showSearchStatus(String status) {
		searchLabel.setText(status);
		searchLabel.pack();
		searchLabel.setX((Config.UI_WIDTH / 2) - (searchLabel.getWidth() / 2));
		searchLabel.setY(serverBtn.getY() + (searchLabel.getHeight() * 2));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Assets.getInstance().getMainBackground(), 0, 0, Config.WIDTH, Config.HEIGHT);
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		Assets.getInstance().getMusic().stop();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.clear();
	}

}
