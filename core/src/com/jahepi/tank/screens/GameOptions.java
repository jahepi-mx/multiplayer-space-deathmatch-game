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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.jahepi.tank.Assets;
import com.jahepi.tank.Config;
import com.jahepi.tank.Language;
import com.jahepi.tank.TankField;
import com.jahepi.tank.TankField.SCREEN_TYPE;
import com.jahepi.tank.dialogs.ServerListDialog;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class GameOptions implements Screen, ServerListDialog.ServerListDialogListener {

	private static final String TAG = "GameOptions";
	
	private TankField tankField;
	private Stage stage;
	private SpriteBatch batch;
	private Label startServerLabel, searchServerLabel, ipServerLabel, backLabel, titleLabel, searchLabel, errorLabel;
	private Button serverBtn, searchServerBtn, ipServerBtn, backBtn;
	private TextField ipTextField;
	private Assets assets;
	private ServerListDialog serverListDialog;
	
	public GameOptions(TankField tankField) {
		this.tankField = tankField;
		this.batch = tankField.getBatch();
		StretchViewport viewport = new StretchViewport(Config.UI_WIDTH, Config.UI_HEIGHT);
		stage = new Stage(viewport, this.batch);
		Gdx.input.setInputProcessor(stage);
		assets = Assets.getInstance();
		serverListDialog = new ServerListDialog(this);
	}

	@Override
	public void show() {
		stage.clear();

		if (!assets.getMusic().isPlaying()) {
			assets.playMusic();
		}
		
		Skin skin = assets.getSkin();
		
		LabelStyle style1 = new LabelStyle();
		BitmapFont uiFont = assets.getUIFont();
		style1.font = uiFont;
		
		startServerLabel = new Label(Language.getInstance().get("start_server_btn"), style1);
		serverBtn = new Button(skin);
		serverBtn.add(startServerLabel);
		serverBtn.setColor(Color.GREEN);
		
		Label serverDescLabel = new Label(Language.getInstance().get("server_desc_label"), style1);
		Label searchServerDescLabel = new Label(Language.getInstance().get("search_server_desc_label"), style1);
		Label ipServerDescLabel = new Label(Language.getInstance().get("ip_server_desc_label"), style1);
		serverDescLabel.setWrap(true);
		serverDescLabel.setAlignment(Align.center);
		searchServerDescLabel.setWrap(true);
		serverDescLabel.setColor(Color.YELLOW);
		searchServerDescLabel.setColor(Color.WHITE);
		searchServerDescLabel.setAlignment(Align.center);
		ipServerDescLabel.setWrap(true);
		ipServerDescLabel.setAlignment(Align.center);
		ipServerDescLabel.setColor(Color.CYAN);

		searchServerLabel = new Label(Language.getInstance().get("search_server_btn"), style1);
		searchServerBtn = new Button(skin);
		searchServerBtn.add(searchServerLabel);
		searchServerBtn.setColor(Color.YELLOW);

		ipServerLabel = new Label(Language.getInstance().get("ip_server_btn"), style1);
		ipServerBtn = new Button(skin);
		ipServerBtn.add(ipServerLabel);
		ipServerBtn.setColor(Color.CYAN);

		ipTextField = new TextField("", skin);
		
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
		backLabel.setColor(Color.RED);
		
		searchServerBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On search server ...");
				if (tankField.isSearchServerActive()) {
					tankField.stopSearchServer();
					searchServerLabel.setText(Language.getInstance().get("search_server_btn"));
				} else {
					tankField.searchServer(assets.getPort(), assets.getMs(), assets.getNickname());
					searchServerLabel.setText(Language.getInstance().get("stop_search_btn"));
				}
			}
		});
		
		serverBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "On run server ...");
				if (tankField.isSearchServerActive()) {
					tankField.stopSearchServer();
				}
				tankField.runServer(assets.getPort(), assets.getNickname());
			}
		});

		ipServerBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String ip = ipTextField.getText();
				InetAddress tempAddress = null;
				try {
					tempAddress = InetAddress.getByName(ip);
					InetSocketAddress socketAddress = new InetSocketAddress(tempAddress, assets.getPort());
					if (!tankField.connect(socketAddress)) {
						errorLabel.setText(Language.getInstance().get("error_network"));
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
					errorLabel.setText(Language.getInstance().get("error_network"));
				}
			}
		});
		
		backBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tankField.changeScreen(SCREEN_TYPE.MAIN);
			}
		});
		
		Table table = new Table();
		table.add(titleLabel).pad(3.0f).colspan(2);
		table.row();
		table.add(searchLabel).pad(3.0f).colspan(2);
		table.row();
		table.add(errorLabel).pad(3.0f).colspan(2);
		table.row();
		table.add(serverDescLabel).width(Config.UI_WIDTH * 0.95f).pad(3.0f).colspan(2);
		table.row();
		table.add(serverBtn).pad(3.0f).colspan(2);
		table.row();
		table.add(searchServerDescLabel).width(Config.UI_WIDTH * 0.95f).pad(3.0f).colspan(2);
		table.row();
		table.add(searchServerBtn).pad(3.0f).colspan(2);
		table.row();
		table.add(ipServerDescLabel).width(Config.UI_WIDTH * 0.95f).pad(3.0f).colspan(2);
		table.row();
		table.add(ipTextField).pad(3.0f).align(Align.right);
		table.add(ipServerBtn).pad(3.0f).align(Align.left);
		table.row();
		table.add(backBtn).pad(10.0f).colspan(2);
		table.setFillParent(true);
		table.getColor().a = 0;
		table.addAction(Actions.fadeIn(0.5f));
		table.pack();
		
		stage.addActor(table);
	}

	public void showDialog(Array<InetSocketAddress> addresses) {
		searchServerLabel.setText(Language.getInstance().get("search_server_btn"));
		serverListDialog.show(addresses, stage);
	}

	public void showConnectionError() {
		errorLabel.setText(Language.getInstance().get("error_network"));
		searchServerLabel.setText(Language.getInstance().get("search_server_btn"));
	}
	
	public void showDisconnectError() {
		errorLabel.setText(Language.getInstance().get("error_disconnect"));
	}
	
	public void showSearchStatus(String status) {
		searchLabel.setText(status);
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
		tankField.stopSearchServer();
	}

	@Override
	public void dispose() {
		stage.clear();
		stage = null;
	}

	@Override
	public void onSelectServer(InetSocketAddress address) {
		if (!tankField.connect(address)) {
			if (serverListDialog.isVisible()) {
				serverListDialog.setErrorLabel(Language.getInstance().get("error_network"));
			}
		}
	}
}
