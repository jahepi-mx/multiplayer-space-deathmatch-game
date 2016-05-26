package com.jahepi.tank.multiplayer;

import com.badlogic.gdx.utils.Array;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by jahepi on 25/05/16.
 */
public class NetworkUtils {

    public static Array<Inet4Address> getMyIps() {
        Array<Inet4Address> addresses = new Array<Inet4Address>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        byte[] ip = address.getAddress();
                        if (ip[0] == 127) { // Omit localhost address
                            continue;
                        }
                        addresses.add((Inet4Address) address);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return addresses;
    }
}
