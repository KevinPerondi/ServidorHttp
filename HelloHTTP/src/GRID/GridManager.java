package GRID;

import Server.Neighbor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridManager extends Thread {

    private final int broadcastPort;
    private final int numeroPortaRespostaUnicast;
    private final int numeroPortaHTTP;
    private final InetAddress ipBroadcast;
    private final List<Neighbor> neighbors;
    private MulticastSocket mcSocket;

    public GridManager(List<Neighbor> neighbors, int httpPort) throws UnknownHostException {
        this.broadcastPort = 5454;
        this.numeroPortaHTTP = httpPort;
        this.neighbors = neighbors;
        this.numeroPortaRespostaUnicast = 6969;
        this.ipBroadcast = InetAddress.getByName("255.255.255.255");
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

    public MulticastSocket getMcSocket() {
        return mcSocket;
    }

    public void setMcSocket(MulticastSocket mcSocket) {
        this.mcSocket = mcSocket;
    }

    public InetAddress getIpBroadcast() {
        return ipBroadcast;
    }

    @Override
    public void run() {
        String message = "SD" + this.getNumeroPortaRespostaUnicast() + " "
                + this.getNumeroPortaHTTP() + "\n";
        byte[] messageOut = new byte[1024];
        messageOut = message.getBytes();
        try {
            this.mcSocket = new MulticastSocket(this.getBroadcastPort());
            this.mcSocket.joinGroup(this.getIpBroadcast());
            GridReceiver gridReceiver = new GridReceiver(this.getNumeroPortaRespostaUnicast(), this.getNeighbors());
            DatagramPacket packageOut = new DatagramPacket(messageOut, messageOut.length, this.getIpBroadcast(), this.getBroadcastPort());
            this.mcSocket.send(packageOut);
            
            byte[] messageIn = new byte[1024];
            while (true) {
                DatagramPacket pack = new DatagramPacket(messageIn, messageIn.length);
                this.mcSocket.receive(pack);
                GridSender gridSender = new GridSender(pack, neighbors,this.numeroPortaHTTP);
            }
        } catch (IOException ex) {
            Logger.getLogger(GridManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
