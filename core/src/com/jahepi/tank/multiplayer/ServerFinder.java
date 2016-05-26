package com.jahepi.tank.multiplayer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.badlogic.gdx.utils.Array;

public class ServerFinder {

	private boolean active;
	private ServerFinderListener listener;
	
	public ServerFinder(ServerFinderListener listener) {
		this.listener = listener;
	}

	public void search(final int port, final int ms)  {
		if (!active) {
			Runnable runnable = new Runnable() {
				public void run() {
					active = true;
					try {
						Array<Inet4Address> addresses = NetworkUtils.getMyIps();
						outer: for (InetAddress address : addresses) {
							byte[] ip = address.getAddress();						
							for (int i = 1; i < 255; i++) {
								ip[3] = (byte) i;
								InetAddress tempAddress = InetAddress.getByAddress(ip);
								InetSocketAddress socketAddress = new InetSocketAddress(tempAddress, port);
								listener.onServerStatus(tempAddress.toString());
								try {
									Socket socket = new Socket();
									socket.connect(socketAddress, ms);
									active = false;
									listener.onServerFound(socket);
									return;
								} catch (IOException exp) {
									exp.printStackTrace();
								}
								if (!active) {
									break outer;
								}
							}
						}
					} catch (Exception exp) {
						exp.printStackTrace();
					}
					active = false;
					listener.onServerNotFound(port);
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
	
	public interface ServerFinderListener {
		public void onServerFound(Socket socket);
		public void onServerStatus(String status);
		public void onServerNotFound(int port);
	}
}
