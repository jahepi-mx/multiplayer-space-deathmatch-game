package com.jahepi.tank;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.jahepi.tank.Controller.GameChangeStateListener;
import com.jahepi.tank.ads.AdListener;
import com.jahepi.tank.dialogs.Option;
import com.jahepi.tank.mem.RunnableManager;
import com.jahepi.tank.multiplayer.Client;
import com.jahepi.tank.multiplayer.Server;
import com.jahepi.tank.multiplayer.Server.ServerListener;
import com.jahepi.tank.multiplayer.ServerFinder;
import com.jahepi.tank.multiplayer.ServerFinder.ServerFinderListener;
import com.jahepi.tank.multiplayer.ServerFinderExecutor;
import com.jahepi.tank.multiplayer.dto.GameState;
import com.jahepi.tank.screens.Configuration;
import com.jahepi.tank.screens.Credits;
import com.jahepi.tank.screens.GameOptions;
import com.jahepi.tank.screens.GamePlay;
import com.jahepi.tank.screens.Main;

public class TankField extends Game implements ServerListener, ServerFinderListener, ServerFinderExecutor.ServerFinderExecutorListener, GameChangeStateListener {
	
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
	private ServerFinderExecutor serverFinderExecutor;
	private String name;
	private Assets assets;
	private AdListener adListener;
	private Option[] maps;
	private RunnableManager runnableManager;
	
	public enum SCREEN_TYPE {
		MAIN, GAMEOPTIONS, CREDITS, CONFIG, GAME
	}

	public TankField(AdListener adListener) {
		this.adListener = adListener;
	}
	
	@Override
	public void create() {
		assets = new Assets();
		Language.getInstance().load(assets.getLanguage());
		batch = new SpriteBatch();
		json = new Json();
		debugRender = new ShapeRenderer();
		debugRender.setAutoShapeType(true);
		changeScreen(SCREEN_TYPE.MAIN);
		//serverFinder = new ServerFinder(this);
		serverFinderExecutor = new ServerFinderExecutor(this);
		Option map1 = new Option(0, Language.getInstance().get("map1_text"));
		Option map2 = new Option(1, Language.getInstance().get("map2_text"));
		Option map3 = new Option(2, Language.getInstance().get("map3_text"));
		Option map4 = new Option(3, Language.getInstance().get("map4_text"));
		maps = new Option[] {map1, map2, map3, map4};
		runnableManager = new RunnableManager(16);
		Gdx.input.setCatchBackKey(true);
	}
	
	public void changeScreen(SCREEN_TYPE type) {
		Gdx.app.log(TAG, type.toString());
		if (type == SCREEN_TYPE.MAIN) {
			currentScreen = new Main(this);
			setScreen(currentScreen);
			adListener.show(true);
		} else if (type == SCREEN_TYPE.GAMEOPTIONS) {
			currentScreen = new GameOptions(this);
			setScreen(currentScreen);
			adListener.show(true);
		} else if (type == SCREEN_TYPE.GAME) {
			currentScreen = new GamePlay(this);
			setScreen(currentScreen);
			adListener.show(false);
		} else if (type == SCREEN_TYPE.CONFIG) {
			currentScreen = new Configuration(this);
			setScreen(currentScreen);
			adListener.show(true);
		} else if (type == SCREEN_TYPE.CREDITS) {
			currentScreen = new Credits(this);
			setScreen(currentScreen);
			adListener.show(true);
		}
	}

	public void showInterstitial() {
		adListener.showInterstitial();
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
		//Gdx.app.log(TAG, "Queue size " + runnableManager.getSize());
		RunnableManager.RunnableTask runnableTask = (RunnableManager.RunnableTask) runnableManager.poll();
		if (runnableTask != null) {
			runnableTask.setData(data);
			runnableTask.setTankField(this);
			Gdx.app.postRunnable(runnableTask);
		}
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
		//serverFinder.search(port, ms);
		serverFinderExecutor.search(port, ms);
	}
	
	public void stopSearchServer() {
		//serverFinder.setActive(false);
		serverFinderExecutor.deactive();
	}
	
	public boolean isSearchServerActive() {
		//return serverFinder.isActive();
		return  serverFinderExecutor.isActive();
	}

	public boolean connect(InetSocketAddress socketAddress) {
		try {
			this.name = assets.getNickname();
			Socket socket = new Socket();
			socket.connect(socketAddress, 3000);
			if (startClient(socket)) {
				changeScreen(SCREEN_TYPE.GAME);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void runServer(int port, String name) {
		this.name = name;
		if (startServer(port)) {
			changeScreen(SCREEN_TYPE.GAME);
		} else {
			if (currentScreen instanceof GameOptions) {
				((GameOptions) currentScreen).showServerConnectionError();
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

	@Override
	public void dispose() {
		batch.dispose();
		debugRender.dispose();
		assets.dispose();
		debugRender = null;
		batch = null;
		assets = null;
		closeConnection();
		runnableManager.dispose();
	}

	@Override
	public void onServerFoundExecutor(final Array<InetSocketAddress> addresses) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (currentScreen instanceof GameOptions) {
					((GameOptions) currentScreen).showDialog(addresses);
				}
			}
		});
	}

	@Override
	public void onServerStatusExecutor(String status) {
		this.onServerStatus(status);
	}

	@Override
	public void onServerNotFoundExecutor(int port) {
		this.onServerNotFound(port);
	}

	public Option[] getMaps() {
		return maps;
	}

	public Screen getCurrentScreen() {
		return currentScreen;
	}

	public Json getJson() {
		return json;
	}

	public Assets getAssets() {
		return assets;
	}
}