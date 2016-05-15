package com.jahepi.tank.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;
import com.jahepi.tank.TankField.SCREEN_TYPE;

public class Configuration implements Screen {

	public static final String TAG = "Configuration";

	private Stage stage;
	private TankField tankField;
	private SpriteBatch batch;
	private Label titleLabel,soundEffectLabel, musicLabel, nicknameLabel, nicknameDescLabel, portLabel, portDescLabel, msLabel, msDescLabel, backLabel;
	private CheckBox englishCheckbox, spanishCheckbox;
	private TextField nicknameTextField, portTextField, msTextField;
	private Button backButton;
	
	public Configuration(TankField tankField) {
		this.tankField = tankField;
		batch = tankField.getBatch();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		stage.clear();
		
		LabelStyle style = new LabelStyle();
		style.font = Assets.getInstance().getUIFontTitle();
		titleLabel = new Label(String.format(Language.getInstance().get("config_title")), style);
		
		Skin skin = Assets.getInstance().getSkin();
		final Assets assets = Assets.getInstance();
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = Assets.getInstance().getUIFontSmall();
		
		soundEffectLabel = new Label(Language.getInstance().get("effects_label"), labelStyle);
		final Slider soundEffectsSlider = new Slider(0, 1, 0.1f, false, skin);
		soundEffectsSlider.setValue(assets.getEffectsVolume());
		
		musicLabel = new Label(Language.getInstance().get("music_label"), labelStyle);
		final Slider musicSlider = new Slider(0, 1, 0.1f, false, skin);
		musicSlider.setValue(assets.getMusicVolume());
		
		englishCheckbox = new CheckBox(Language.getInstance().get("english_btn"), skin);
		spanishCheckbox = new CheckBox(Language.getInstance().get("spanish_btn"), skin);
		new ButtonGroup<CheckBox>(englishCheckbox, spanishCheckbox);
		
		if (assets.getLanguage().equals(Language.ENGLISH)) {
			englishCheckbox.setChecked(true);
		} else {
			spanishCheckbox.setChecked(true);
		}
		
		nicknameDescLabel = new Label(Language.getInstance().get("nickname_desc_label"), labelStyle);
		nicknameDescLabel.setColor(Color.YELLOW);
		
		nicknameLabel = new Label(Language.getInstance().get("nickname_label"), labelStyle);
		nicknameTextField = new TextField(assets.getNickname(), skin);
		
		portDescLabel = new Label(Language.getInstance().get("port_desc_label"), labelStyle);
		portDescLabel.setColor(Color.YELLOW);
		
		portLabel = new Label(Language.getInstance().get("port_label"), labelStyle);
		portTextField = new TextField("" + assets.getPort(), skin);
		DigitsOnlyFilter filter = new DigitsOnlyFilter();
		portTextField.setTextFieldFilter(filter);
		
		msDescLabel = new Label(Language.getInstance().get("ms_desc_label"), labelStyle);
		msDescLabel.setColor(Color.YELLOW);
		
		msLabel = new Label(Language.getInstance().get("ms_label"), labelStyle);
		msTextField = new TextField("" + assets.getMs(), skin);
		msTextField.setTextFieldFilter(filter);
		
		backLabel = new Label(Language.getInstance().get("back_btn"), style);
		backButton = new Button(skin);
		backButton.add(backLabel);
		
		englishCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (englishCheckbox.isChecked()) {
					assets.setLanguage(Language.ENGLISH);
					Language.getInstance().load(Language.ENGLISH);
					updateTexts();
				}
			}	
		});
		
		spanishCheckbox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (spanishCheckbox.isChecked()) {
					assets.setLanguage(Language.SPANISH);
					Language.getInstance().load(Language.SPANISH);
					updateTexts();
				}
			}	
		});
		
		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float value = musicSlider.getValue();
				assets.setMusicVolume(value);
				assets.getActionMusic().setVolume(value);
				assets.getMusic().setVolume(value);
			}
		});
		
		soundEffectsSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float value = soundEffectsSlider.getValue();
				assets.setEffectsVolume(value);
			}
		});
		
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				assets.setNickname(nicknameTextField.getText());
				assets.setPort(Integer.parseInt(portTextField.getText()));
				assets.setMs(Integer.parseInt(msTextField.getText()));
				tankField.changeScreen(SCREEN_TYPE.MAIN);
			}		
		});
		
		Table table = new Table();
		table.add(titleLabel).pad(5.0f).colspan(2);
		table.row();
		table.add(musicLabel).pad(5.0f).uniform();
		table.add(musicSlider).pad(5.0f).uniform();
		table.row();
		table.add(soundEffectLabel).pad(5.0f).uniform();
		table.add(soundEffectsSlider).pad(5.0f).uniform();
		table.row();
		table.add(englishCheckbox).pad(0.5f).uniform();
		table.add(spanishCheckbox).pad(0.5f).uniform();
		table.row();
		table.add(nicknameDescLabel).pad(5.0f).colspan(2);
		table.row();
		table.add(nicknameLabel).pad(5.0f).uniform();
		table.add(nicknameTextField).pad(5.0f).uniform();
		table.row();
		table.add(portDescLabel).pad(5.0f).colspan(2);
		table.row();
		table.add(portLabel).pad(5.0f).uniform();
		table.add(portTextField).pad(5.0f).uniform();
		table.row();
		table.add(msDescLabel).pad(5.0f).colspan(2);
		table.row();
		table.add(msLabel).pad(5.0f).uniform();
		table.add(msTextField).pad(5.0f).uniform();
		table.row();
		table.add(backButton).pad(0.5f).colspan(2);
		table.setFillParent(true);
		table.getColor().a = 0;
		table.addAction(Actions.fadeIn(0.5f));
		table.pack();
		stage.addActor(table);
	}
	
	private void updateTexts() {
		titleLabel.setText(Language.getInstance().get("config_title"));
		musicLabel.setText(Language.getInstance().get("music_label"));
		soundEffectLabel.setText(Language.getInstance().get("effects_label"));
		englishCheckbox.setText(Language.getInstance().get("english_btn"));
		spanishCheckbox.setText(Language.getInstance().get("spanish_btn"));
		nicknameDescLabel.setText(Language.getInstance().get("nickname_desc_label"));
		nicknameLabel.setText(Language.getInstance().get("nickname_label"));
		portDescLabel.setText(Language.getInstance().get("port_desc_label"));
		portLabel.setText(Language.getInstance().get("port_label"));
		msDescLabel.setText(Language.getInstance().get("ms_desc_label"));
		msLabel.setText(Language.getInstance().get("ms_label"));
		backLabel.setText(Language.getInstance().get("back_btn"));
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