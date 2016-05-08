package com.jahepi.tank.multiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Server {

	private final int MAX_CONNECTIONS = 3;
	private final static String TAG = "Server";
	
	public final static int PORT = 38000;
	
	private ServerSocket server;
	private Thread thread;
	private boolean running;
	private Array<Client> clients;
	private ServerListener listener;
	
	public Server(int port, ServerListener listener) throws IOException {
		server = new ServerSocket(port);
		clients = new Array<Client>();
		this.listener = listener;
	}
	
	public void start() {
		if (!running) {
			running = true;
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (running) {
						try {
							Socket socket = server.accept();
							socket.setTcpNoDelay(true);
							if (clients.size < MAX_CONNECTIONS) {
								Gdx.app.log(TAG, "Client connected");
								Client client = new Client(socket, listener, false);
								clients.add(client);
								client.start();
							} else {
								socket.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			thread.start();
		}
	}
	
	public void send(String data) {
		for (Client client : clients) {
			if (client != null) {
				if (client.isActive()) {
					client.send(data);
				} else {
					clients.removeValue(client, true);
				}
			}
		}
	}
	
	public int getNumberOfClients() {
		return clients.size;
	}
	
	public void close() {
		running = false;
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Client client : clients) {
			if (client != null) {
				client.close();
			}
		}
	}
	
	public interface ServerListener {
		void onNewConnection(String id);
		void onConnectionData(String data);
		void onDisconnect(String id);
	}
}
