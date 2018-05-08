package GRID;

import Server.Neighbor;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridReceiver extends Thread {

    private ServerSocket ss;
    private int serverPort;
    private final List<Neighbor> neighbors;

    public GridReceiver(int port, List<Neighbor> neighs) {
        this.serverPort = port;
        this.neighbors = neighs;
        this.start();
    }

    public ServerSocket getSs() {
        return ss;
    }

    public void setSs(ServerSocket ss) {
        this.ss = ss;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            this.ss = new ServerSocket(this.getServerPort());
            while (true) {
                Socket s = ss.accept();
                newClient client = new newClient(s,this.neighbors);
            }
        } catch (IOException ex) {
            Logger.getLogger(GridReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class newClient extends Thread {

    private final Socket socket;
    private final List<Neighbor> neighbors;
    private DataInputStream in;

    public newClient(Socket s, List<Neighbor> neighs) throws IOException {
        this.socket = s;
        this.neighbors = neighs;
        this.in = new DataInputStream(this.socket.getInputStream());
        this.start();
    }

    @Override
    public void run() {
        String message = new String();
        try {
            message = in.readUTF();
            if (message.startsWith("AD")) {
                String[] splitter = message.split("AD");
                Neighbor ng = new Neighbor(this.socket.getInetAddress().toString(), splitter[1]);
                this.neighbors.add(ng);
            }
        } catch (IOException ex) {
            Logger.getLogger(newClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
