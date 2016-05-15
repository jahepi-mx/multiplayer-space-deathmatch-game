package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;
import com.jahepi.tank.TankField.SCREEN_TYPE;

public class GameOptions implements Screen {

	private static final String TAG = "GameOptions";
	
	private TankField tankField;
	private Stage stage;
	private SpriteBatch batch;
	private Label startServerLabel, searchServerLabel, backLabel, titleLabel, searchLabel, errorLabel;
	private Button serverBtn, searchServerBtn, backBtn;
	
	public GameOptions(TankField tankField) {
		this.tankField = tankField;
		this.batch = tankField.getBatch();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, this.batch);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		stage.clear();

		final Assets assets = Assets.getInstance();
		Skin skin = assets.getSkin();
		
		LabelStyle style1 = new LabelStyle();
		BitmapFont uiFont = assets.getUIFont();
		style1.font = uiFont;
		
		startServerLabel = new Label(Language.getInstance().get("start_server_btn"), style1);
		serverBtn = new Button(skin);
		serverBtn.add(startServerLabel);
		
		searchServerLabel = new Label(Language.getInstance().get("search_server_btn"), style1);
		searchServerBtn = new Button(skin);
		searchServerBtn.add(searchServerLabel);
		
		LabelStyle style = new LabelStyle();
		style.font = assets.getUIFont();
		errorLabel = new Label("", style);
		errorLabel.setColor(Color.RED);
		
		searchLabel = new Label("", style);
		searchLabel.setColor(Color.WHITE);
		
		LabelStyle styleTitle = new LabelStyle();
		styleTitle.font = assets.getUIFontTitle();
		titleLabel = new Label(String.format(Language.getInstance().get("game_options_title")), styleTitle);
		
		backLabel = new Label(Language.getInstance().get("back_btn"), style);
		backBtn = new Button(skin);
		backBtn.add(backLabel);
		
		searchServerBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On search server ...");
				tankField.searchServer(assets.getPort(), assets.getNickname());
			}	
		});
		
		serverBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On run server ...");
				tankField.runServer(assets.getPort(), assets.getNickname());
			}	
		});
		
		backBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.MAIN);
			}		
		});
		
		Table table = new Table();
		table.add(titleLabel).pad(40.0f);
		table.row();
		table.add(searchLabel).pad(10.0f).uniform();
		table.row();
		table.add(errorLabel).pad(10.0f).uniform();
		table.row();
		table.add(serverBtn).pad(10.0f).uniform();
		table.row();
		table.add(searchServerBtn).pad(10.0f).uniform();
		table.row();
		table.add(backBtn).pad(10.0f).uniform();
		table.setFillParent(true);
		table.getColor().a = 0;
		table.addAction(Actions.fadeIn(0.5f));
		table.pack();
		
		stage.addActor(table);
	}
	
	public void showConnectionError() {
		errorLabel.setText(Language.getInstance().get("error_network"));
	}
	
	public void showDisconnectError() {
		errorLabel.setText(Language.getInstance().get("error_disconnect"));
	}
	
	public void showSearchStatus(String status) {
		searchLabel.setText(status);
	}

	@Override
	public void render(float delta) {		
		batch.begin();
		batch.setShader(null);
		batch.draw(Assets.getInstance().getMainBackground(), 0, 0, Config.UI_WIDTH, Config.UI_HEIGHT);
		batch.end();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		Assets.getInstance().getMusic().stop();
	}

	@Override
	public void dispose() {
		stage.clear();
		stage = null;
	}
}
