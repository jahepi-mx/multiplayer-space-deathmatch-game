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
import com.jahepi.tank.Language.LANG;
import com.jahepi.tank.multiplayer.Client;
import com.jahepi.tank.multiplayer.Server;
import com.jahepi.tank.multiplayer.Server.ServerListener;
import com.jahepi.tank.multiplayer.ServerFinder;
import com.jahepi.tank.multiplayer.ServerFinder.ServerFinderListener;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.screens.Field;
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
	
	public enum SCREEN_TYPE {
		MAIN, FIELD
	}
	
	@Override
	public void create() {
		Language.getInstance().load(LANG.SPANISH);
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
		}
		if (type == SCREEN_TYPE.FIELD) {
			currentScreen = new Field(this);
			setScreen(currentScreen);
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
				if (currentScreen instanceof Field) {
					GameState gameState = json.fromJson(GameState.class, data);
					((Field) currentScreen).updateGameState(gameState);
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
					if (currentScreen instanceof Field) {
						((Field) currentScreen).removeOpponent(id);
						((Field) currentScreen).showDisconnectError();
					}
				} else {
					changeScreen(SCREEN_TYPE.MAIN);
					((Main) currentScreen).showDisconnectError();
				}
			}
		});
	}
	
	public void start(int port) {
		serverFinder.search(port);
	}
	
	@Override
	public void onServerFound(final Socket socket) {
		Gdx.app.postRunnable(new Runnable() {		
			@Override
			public void run() {
				if (startClient(socket)) {
					changeScreen(SCREEN_TYPE.FIELD);
				} else {
					((Main) currentScreen).showConnectionError();
				}
			}
		});
	}

	@Override
	public void onServerNotFound(final int port) {
		Gdx.app.postRunnable(new Runnable() {		
			@Override
			public void run() {
				if (startServer(port)) {
					changeScreen(SCREEN_TYPE.FIELD);
				} else {
					((Main) currentScreen).showConnectionError();
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
				((Main) currentScreen).showSearchStatus(searchStatus);
			}
		});
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
			client.send(data);
		}
		if (server != null) {
			String data = json.toJson(gameState);
			server.send(data);
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
}