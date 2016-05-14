package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;
import com.jahepi.tank.TankField.SCREEN_TYPE;

public class Main implements Screen {
	
	public static final String TAG = "Main";

	private Stage stage;
	private TankField tankField;
	private SpriteBatch batch;
	
	public Main(TankField tankField) {
		this.tankField = tankField;
		batch = tankField.getBatch();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		stage.clear();
		if (!Assets.getInstance().getMusic().isPlaying()) {
			Assets.getInstance().getMusic().play();
		}
		
		float margin = 60.0f; 
		
		LabelStyle styleTitle = new LabelStyle();
		styleTitle.font = Assets.getInstance().getUIFontTitle();
		Label titleLabel = new Label(String.format(Language.getInstance().get("game_title"), "\n", Config.VERSION), styleTitle);
		titleLabel.setAlignment(Align.center);
		titleLabel.setX((Config.UI_WIDTH / 2) - (titleLabel.getWidth() / 2));
		titleLabel.setY(Config.UI_HEIGHT - titleLabel.getHeight() - margin);
		
		LabelStyle style1 = new LabelStyle();
		BitmapFont uiFont = Assets.getInstance().getUIFontMain();
		style1.font = uiFont;
		
		Label playLabel = new Label(Language.getInstance().get("play_btn"), style1);
		Button playBtn = new Button(new ButtonStyle());
		playBtn.setWidth(playLabel.getWidth());
		playBtn.add(playLabel);
		
		float height = (playBtn.getHeight() * 3) + (margin * 2);
		float top = (Config.UI_HEIGHT / 2) + (height / 2) - margin;
		
		Label configLabel = new Label(Language.getInstance().get("config_btn"), style1);
		Button configBtn = new Button(new ButtonStyle());
		configBtn.setWidth(configLabel.getWidth());
		configBtn.add(configLabel);
		
		Label creditsLabel = new Label(Language.getInstance().get("credits_btn"), style1);
		Button creditsBtn = new Button(new ButtonStyle());
		creditsBtn.setWidth(creditsLabel.getWidth());
		creditsBtn.add(creditsLabel);
		
		playBtn.setPosition((Config.UI_WIDTH / 2) - (playBtn.getWidth() / 2) , top);
		configBtn.setPosition((Config.UI_WIDTH / 2) - (configBtn.getWidth() / 2) , playBtn.getY() - playBtn.getHeight() - margin);
		creditsBtn.setPosition((Config.UI_WIDTH / 2) - (creditsBtn.getWidth() / 2) , configBtn.getY() - configBtn.getHeight() - margin);
		
		playBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.GAMEOPTIONS);
			}		
		});
		
		playBtn.pack();
		configBtn.pack();
		creditsBtn.pack();
		
		stage.addActor(titleLabel);
		stage.addActor(playBtn);
		stage.addActor(configBtn);
		stage.addActor(creditsBtn);
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

	}

	@Override
	public void dispose() {
		stage.clear();
		stage = null;
	}
}
