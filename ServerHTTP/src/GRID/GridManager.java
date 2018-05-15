package GRID;

import Server.Neighbor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridManager extends Thread {

    private final int broadcastPort;
    private final int numeroPortaRespostaUnicast;
    private final int numeroPortaHTTP;
    private List<Neighbor> neighbors;
    private InetAddress ipBroadcast;
    private DatagramSocket dataSocket;

    public GridManager(List<Neighbor> neighbors, int httpPort) throws UnknownHostException, SocketException {
        this.broadcastPort = 5454;
        this.numeroPortaHTTP = httpPort;
        this.neighbors = neighbors;
        this.numeroPortaRespostaUnicast = 6969;
        this.dataSocket = new DatagramSocket();
        this.getBroadcastIP();
        this.start();
    }

    public int getBroadcastPort() {
        return broadcastPort;
    }

    public int getNumeroPortaRespostaUnicast() {
        return numeroPortaRespostaUnicast;
    }

    public int getNumeroPortaHTTP() {
        return numeroPortaHTTP;
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Neighbor> neighbors) {
        this.neighbors = neighbors;
    }

    public InetAddress getIpBroadcast() {
        return ipBroadcast;
    }

    public void setIpBroadcast(InetAddress ipBroadcast) {
        this.ipBroadcast = ipBroadcast;
    }

    public DatagramSocket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(DatagramSocket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public void getBroadcastIP() throws SocketException {
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
        if (!broadIps.isEmpty()) {
            this.setIpBroadcast(broadIps.get(0));
        }
    }

    @Override
    public void run() {
        String message = "SD" + this.getNumeroPortaRespostaUnicast() + " "
                + this.getNumeroPortaHTTP() + "\n";
        byte[] messageOut = new byte[1024];
        messageOut = message.getBytes();
        try {
            GridReceiver gridReceiver = new GridReceiver(this.getNumeroPortaRespostaUnicast(), this.getNeighbors());
            DatagramPacket packageOut = new DatagramPacket(messageOut, messageOut.length, this.getIpBroadcast(), this.getBroadcastPort());
            this.dataSocket.send(packageOut);

            byte[] messageIn = new byte[1024];
            while (true) {
                DatagramPacket pack = new DatagramPacket(messageIn, messageIn.length);
                this.dataSocket.receive(pack);
                GridSender gridSender = new GridSender(pack, neighbors, this.numeroPortaHTTP);
            }
        } catch (IOException ex) {
            Logger.getLogger(GridManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
