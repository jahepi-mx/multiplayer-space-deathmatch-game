package com.jahepi.tank.multiplayer;

import com.badlogic.gdx.utils.Array;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by javier.hernandez on 30/05/2016.
 */
public class ServerFinderExecutor {

    private ExecutorService executor;
    private boolean active;
    private ServerFinderExecutorListener listener;

    public ServerFinderExecutor(ServerFinderExecutorListener listener) {
        this.listener = listener;
    }

    public void search(final int port, final int ms) {
        if (!isActive()) {
            executor = Executors.newFixedThreadPool(64);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Array<Future<InetSocketAddress>> futures = new Array<Future<InetSocketAddress>>();
                    try {
                        Array<Inet4Address> addresses = NetworkUtils.getMyIps();
                        for (InetAddress address : addresses) {
                            byte[] ip = address.getAddress();
                            for (int i = 1; i < 255; i++) {
                                ip[3] = (byte) i;
                                InetAddress inetAddress = InetAddress.getByAddress(ip);
                                futures.add(postIsOpen(inetAddress, port, ms));
                            }
                        }
                        executor.shutdown();
                        executor.awaitTermination(20, TimeUnit.SECONDS);
                        Array<InetSocketAddress> socketAddresses = new Array<InetSocketAddress>();
                        for (Future<InetSocketAddress> future : futures) {
                            InetSocketAddress address = future.get();
                            if (address != null) {
                                socketAddresses.add(address);
                            }
                        }
                        if (socketAddresses.size > 0) {
                            listener.onServerFoundExecutor(socketAddresses);
                        } else {
                            listener.onServerNotFoundExecutor(port);
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    public void deactive() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public boolean isActive() {
        if (executor != null) {
            return !executor.isTerminated();
        }
        return false;
    }

    private Future<InetSocketAddress> postIsOpen(final InetAddress address, final int port, final int ms) {
        return executor.submit(new Callable<InetSocketAddress>() {
            @Override
            public InetSocketAddress call() throws Exception {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port);
                Socket socketConnection = null;
                try {
                    listener.onServerStatusExecutor(address.toString());
                    socketConnection = new Socket();
                    socketConnection.connect(inetSocketAddress, ms);
                } catch (Exception exp) {
                    exp.printStackTrace();
                    return null;
                }
                if (socketConnection != null) {
                    try {
                        socketConnection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return inetSocketAddress;
            }
        });
    }

    public interface ServerFinderExecutorListener {
        public void onServerFoundExecutor(Array<InetSocketAddress> addresses);
        public void onServerStatusExecutor(String status);
        public void onServerNotFoundExecutor(int port);
    }
}
