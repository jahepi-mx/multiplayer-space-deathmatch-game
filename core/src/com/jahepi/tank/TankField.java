package com.jahepi.tank;

import java.io.IOException;
import java.net.Socket;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.jahepi.tank.Controller.GameChangeStateListener;
import com.jahepi.tank.ads.AdListener;
import com.jahepi.tank.multiplayer.Client;
import com.jahepi.tank.multiplayer.Server;
import com.jahepi.tank.multiplayer.Server.ServerListener;
import com.jahepi.tank.multiplayer.ServerFinder;
import com.jahepi.tank.multiplayer.ServerFinder.ServerFinderListener;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.screens.Configuration;
import com.jahepi.tank.screens.GameOptions;
import com.jahepi.tank.screens.GamePlay;
import com.jahepi.tank.screens.Main;

public class TankField extends Game implements ServerListener, ServerFinderListener, GameChangeStateListener {
	
	private final static String TAG = "TankField";
	
	private SpriteBatch batch;
	private ShapeRenderer debugRender;
	private Server server;
	private Client client;
	private Screen currentScreen;
	private Json json;
	private boolean newConnection;
	private String connectionId;
	private ServerFinder serverFinder;
	private String name;
	private Assets assets;
	private AdListener adListener;
	
	public enum SCREEN_TYPE {
		MAIN, GAMEOPTIONS, CREDITS, CONFIG, GAME
	}

	public TankField() {
	}

	public TankField(AdListener adListener) {
		this.adListener = adListener;
	}
	
	@Override
	public void create() {
		assets = Assets.getInstance();
		Language.getInstance().load(assets.getLanguage());
		batch = new SpriteBatch();
		json = new Json();
		debugRender = new ShapeRenderer();
		debugRender.setAutoShapeType(true);
		changeScreen(SCREEN_TYPE.MAIN);
		serverFinder = new ServerFinder(this);
	}
	
	public void changeScreen(SCREEN_TYPE type) {
		Gdx.app.log(TAG, type.toString());
		if (type == SCREEN_TYPE.MAIN) {
			currentScreen = new Main(this);
			setScreen(currentScreen);
			if (adListener != null) {
				adListener.show(true);
			}
		} else if (type == SCREEN_TYPE.GAMEOPTIONS) {
			currentScreen = new GameOptions(this);
			setScreen(currentScreen);
			if (adListener != null) {
				adListener.show(false);
			}
		} else if (type == SCREEN_TYPE.GAME) {
			currentScreen = new GamePlay(this);
			setScreen(currentScreen);
		} else if (type == SCREEN_TYPE.CONFIG) {
			currentScreen = new Configuration(this);
			setScreen(currentScreen);
			if (adListener != null) {
				adListener.show(false);
			}
		}
	}

	public boolean isNewConnection() {
		return newConnection;
	}

	public void setNewConnection(boolean newConnection) {
		this.newConnection = newConnection;
	}

	@Override
	public void onNewConnection(String id) {
		newConnection = true;
		this.connectionId = id;
	}
	
	public String getConnectionId() {
		return connectionId;
	}
	
	@Override
	public void onConnectionData(final String data) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				//Gdx.app.log(TAG, Thread.currentThread().getName());
				if (currentScreen instanceof GamePlay) {
					GameState gameState = json.fromJson(GameState.class, data);
					((GamePlay) currentScreen).updateGameState(gameState);
				}
			}
		});
	}
	
	@Override
	public void onDisconnect(final String id) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Gdx.app.log(TAG, "onDisconnect");
				if (server != null) {
					if (currentScreen instanceof GamePlay) {
						((GamePlay) currentScreen).removeOpponent(id);
						((GamePlay) currentScreen).showDisconnectError();
					}
				} else {
					changeScreen(SCREEN_TYPE.GAMEOPTIONS);
					((GameOptions) currentScreen).showDisconnectError();
				}
			}
		});
	}
	
	public void searchServer(int port, int ms, String name) {
		this.name = name;
		serverFinder.search(port, ms);
	}
	
	public void stopSearchServer() {
		serverFinder.setActive(false);
	}
	
	public boolean isSearchServerActive() {
		return serverFinder.isActive();
	}
	
	public void runServer(int port, String name) {
		this.name = name;
		if (startServer(port)) {
			changeScreen(SCREEN_TYPE.GAME);
		} else {
			if (currentScreen instanceof GameOptions) {
				((GameOptions) currentScreen).showConnectionError();
			}
		}
	}
	
	@Override
	public void onServerFound(final Socket socket) {
		Gdx.app.postRunnable(new Runnable() {		
			@Override
			public void run() {
				if (startClient(socket)) {
					changeScreen(SCREEN_TYPE.GAME);
				} else {
					if (currentScreen instanceof GameOptions) {
						((GameOptions) currentScreen).showConnectionError();
					}
				}
			}
		});
	}

	@Override
	public void onServerNotFound(final int port) {
		Gdx.app.postRunnable(new Runnable() {		
			@Override
			public void run() {
				if (currentScreen instanceof GameOptions) {
					((GameOptions) currentScreen).showConnectionError();
				}
			}
		});
	}

	@Override
	public void onServerStatus(final String status) {
		Gdx.app.postRunnable(new Runnable() {		
			@Override
			public void run() {
				String searchStatus = String.format(Language.getInstance().get("search_label"), status);
				if (currentScreen instanceof GameOptions) {
					((GameOptions) currentScreen).showSearchStatus(searchStatus);
				}
			}
		});
	}
	
	public void stopServerSearch() {
		if (serverFinder.isActive()) {
			serverFinder.setActive(false);
		}
	}

	private boolean startServer(int port) {
		try {
			server = new Server(port, this);
			server.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean startClient(Socket socket) {
		client = new Client(socket, this, true);
		if (client.isActive()) {
			client.start();
			return true;
		}
		return false;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	public ShapeRenderer getDebugRender() {
		return debugRender;
	}
	
	public boolean isServer() {
		return server != null;
	}

	@Override
	public void onGameChangeState(GameState gameState) {
		if (client != null) {
			String data = json.toJson(gameState);
			client.addData(data);
		}
		if (server != null) {
			String data = json.toJson(gameState);
			server.addData(data);
		}
	}
	
	public void closeConnection() {
		if (client != null) {
			client.close();
			client = null;
		}
		if (server != null) {
			server.close();
			server = null;
		}
	}

	public String getName() {
		return name;
	}
}