package com.jahepi.tank.multiplayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.jahepi.tank.multiplayer.Server.ServerListener;

public class Client extends Thread {

	private final static String TAG = "Client";
	
	private ServerListener listener;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private boolean active, notifyNewConnection;
	private String identifier;
	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(240);
	
	public Client(Socket socket, ServerListener listener, boolean notifyNewConnection) {
		active = true;
		this.notifyNewConnection = notifyNewConnection;
		this.socket = socket;
		try {
			this.socket.setTcpNoDelay(true);
		} catch (SocketException exp) {
			exp.printStackTrace();
		}
		this.listener = listener;
		try {
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			active = false;
		}
	}
	
	public Client(String host, int port, ServerListener listener, boolean notifyNewConnection) {
		active = true;
		this.notifyNewConnection = notifyNewConnection;
		this.listener = listener;
		try {
			socket = new Socket();
			socket.setTcpNoDelay(true);
			socket.connect(new InetSocketAddress(host, port), 2000);
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (UnknownHostException e) {
			active = false;
		} catch (IOException e) {
			active = false;
		} 
	}

	@Override
	public void run() {
		try {
			InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
			if (notifyNewConnection) {
				identifier = socket.getLocalAddress() + ":" + socket.getLocalPort();
			} else {
				identifier = address.getAddress() + ":" + address.getPort();
			}
			if (active && notifyNewConnection) {
				listener.onNewConnection(identifier);
			}
			SendDataThread sendThread = new SendDataThread();
			sendThread.start();
			while (active) {
				String data = in.readUTF();
				if (listener != null) {
					listener.onConnectionData(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			active = false;
			Gdx.app.log(TAG, identifier);
			if (listener != null) {
				listener.onDisconnect(identifier);
			}
		}
	}
	
	public void send(String data) {
		//Gdx.app.log(TAG, identifier + " " + Thread.currentThread().getName());
		try {
			if (isActive()) {
				out.writeUTF(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		active = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isActive() {
		return active;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public void addData(String data) {
		try {
			//Gdx.app.log("Queue size", "" + queue.size());
			queue.add(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class SendDataThread extends Thread {

		@Override
		public void run() {
			while (active) {
				String data;
				try {
					data = queue.poll();
					if (data != null && data.length() > 0) {
						send(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
