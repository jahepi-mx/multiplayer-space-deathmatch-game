package com.jahepi.tank.multiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import com.badlogic.gdx.Gdx;

public class Server {

	private final int MAX_CONNECTIONS = 3;
	private final static String TAG = "Server";
	
	public final static int PORT = 38000;
	
	private ServerSocket server;
	private Thread thread;
	private boolean running;
	private ArrayBlockingQueue<Client> clients;
	private ServerListener listener;
	
	public Server(int port, ServerListener listener) throws IOException {
		server = new ServerSocket(port);
		clients = new ArrayBlockingQueue<Client>(MAX_CONNECTIONS);
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
							if (clients.size() < MAX_CONNECTIONS) {
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
					clients.remove(client);
				}
			}
		}
	}
	
	public void addData(String data) {
		for (Client client : clients) {
			if (client != null) {
				if (client.isActive()) {
					client.addData(data);
				} else {
					clients.remove(client);
				}
			}
		}
	}
	
	public int getNumberOfClients() {
		return clients.size();
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
		clients.clear();
	}
	
	public interface ServerListener {
		void onNewConnection(String id);
		void onConnectionData(String data);
		void onDisconnect(String id);
	}
}
