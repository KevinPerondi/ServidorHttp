package Server;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WorkerDyn_teste {

    public static void main(String[] args) throws FileNotFoundException, UnknownHostException, SocketException {
        List<InetAddress> broadIps = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfacesAddress : ni.getInterfaceAddresses()) {
                    InetAddress broadcastIP = interfacesAddress.getBroadcast();
                    if (!(broadcastIP == null)) {
                        broadIps.add(broadcastIP);
                    }
                }
            }
        }
    }
}
