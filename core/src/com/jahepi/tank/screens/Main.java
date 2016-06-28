package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;
import com.jahepi.tank.TankField.SCREEN_TYPE;
import com.jahepi.tank.dialogs.IpDialog;

public class Main implements Screen {
	
	public static final String TAG = "Main";

	private Stage stage;
	private TankField tankField;
	private SpriteBatch batch;
	private Assets assets;
	private IpDialog ipDialog;

	public Main(TankField tankField) {
		this.tankField = tankField;
		batch = tankField.getBatch();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);
		assets = Assets.getInstance();
		ipDialog = new IpDialog();
	}

	@Override
	public void show() {
		stage.clear();
		
		if (!assets.getMusic().isPlaying()) {
			assets.playMusic();
		}
		
		LabelStyle titleStyle = new LabelStyle();
		titleStyle.font = assets.getUIFontTitle();
		Label titleLabel = new Label(String.format(Language.getInstance().get("game_title"), Config.VERSION), titleStyle);
		titleLabel.setAlignment(Align.center);
		titleLabel.setWrap(true);

		LabelStyle websiteStyle = new LabelStyle();
		websiteStyle.font = assets.getUIFontSmall();
		Label websiteLabel = new Label(Language.getInstance().get("website"), websiteStyle);
		websiteLabel.setAlignment(Align.center);
		websiteLabel.setWrap(true);
		
		LabelStyle style = new LabelStyle();
		BitmapFont uiFont = assets.getUIFontMain();
		style.font = uiFont;
		
		Label playLabel = new Label(Language.getInstance().get("play_btn"), style);
		Button playBtn = new Button(new ButtonStyle());
		playBtn.setWidth(playLabel.getWidth());
		playBtn.add(playLabel);
		
		Label configLabel = new Label(Language.getInstance().get("config_btn"), style);
		Button configBtn = new Button(new ButtonStyle());
		configBtn.setWidth(configLabel.getWidth());
		configBtn.add(configLabel);
		
		Label creditsLabel = new Label(Language.getInstance().get("credits_btn"), style);
		Button creditsBtn = new Button(new ButtonStyle());
		creditsBtn.setWidth(creditsLabel.getWidth());
		creditsBtn.add(creditsLabel);

		Label ipLabel = new Label(Language.getInstance().get("ip_btn"), style);
		Button ipBtn = new Button(new ButtonStyle());
		ipBtn.setWidth(ipLabel.getWidth());
		ipBtn.add(ipLabel);
		
		Label exitLabel = new Label(Language.getInstance().get("exit_btn"), style);
		Button exitBtn = new Button(new ButtonStyle());
		exitBtn.setWidth(exitLabel.getWidth());
		exitBtn.add(exitLabel);
		
		playBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.GAMEOPTIONS);
			}
		});
		
		configBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.CONFIG);
			}
		});

		ipBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ipDialog.show(stage);
			}
		});

		creditsBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.CREDITS);
			}
		});

		exitBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		Table table = new Table();
		table.padTop(50);
		table.add(titleLabel).width(Config.UI_WIDTH * 0.9f).pad(10.0f);
		table.row();
		table.add(playBtn).pad(8.0f).uniform();
		table.row();
		table.add(configBtn).pad(8.0f).uniform();
		table.row();
		table.add(ipBtn).pad(8.0f).uniform();
		table.row();
		table.add(creditsBtn).pad(8.0f).uniform();
		table.row();
		table.add(exitBtn).pad(8.0f).uniform();
		table.row();
		table.add(websiteLabel).pad(5.0f).uniform();
		table.setFillParent(true);
		table.getColor().a = 0;
		table.addAction(Actions.fadeIn(0.5f));
		table.pack();

		stage.addActor(ipDialog);
		stage.addActor(table);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		Color color = batch.getColor();
		batch.setColor(color.r, color.g, color.b, 1.0f);
		batch.setShader(null);
		batch.draw(assets.getMainBackground(), 0, 0, Config.UI_WIDTH, Config.UI_HEIGHT);
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

	}

	@Override
	public void dispose() {
		stage.clear();
		stage = null;
	}
}
