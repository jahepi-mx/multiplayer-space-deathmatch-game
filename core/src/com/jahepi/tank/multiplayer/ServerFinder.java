package com.jahepi.tank.multiplayer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.Enumeration;

import com.badlogic.gdx.utils.Array;

public class ServerFinder {

	private boolean active;
	private ServerFinderListener listener;
	
	public ServerFinder(ServerFinderListener listener) {
		this.listener = listener;
	}

	public void search(final int port)  {
		if (!active) {
			final Array<InetAddress> addresses = new Array<InetAddress>();
			Runnable runnable = new Runnable() {
				public void run() {
					active = true;
					try {
						Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
						for (NetworkInterface ni : Collections.list(interfaces)) {
							for (InetAddress address : Collections.list(ni.getInetAddresses())) {
								if (address instanceof Inet4Address) {
									byte[] ip = address.getAddress();
									if (ip[0] == 127) { // Omit localhost address
										continue;
									}
									addresses.add(address);
								}
							}
						}
						for (InetAddress address : addresses) {
							byte[] ip = address.getAddress();						
							for (int i = 1; i < 255; i++) {
								ip[3] = (byte) i;
								InetAddress tempAddress = InetAddress.getByAddress(ip);
								InetSocketAddress socketAddress = new InetSocketAddress(tempAddress, port);
								listener.onServerStatus(tempAddress.toString());
								try {
									Socket socket = new Socket();
									socket.connect(socketAddress, 300);
									active = false;
									listener.onServerFound(socket);
									return;
								} catch (IOException exp) {
									exp.printStackTrace();
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

	public boolean isActive() {
		return active;
	}
	
	public interface ServerFinderListener {
		public void onServerFound(Socket socket);
		public void onServerStatus(String status);
		public void onServerNotFound(int port);
	}
}
