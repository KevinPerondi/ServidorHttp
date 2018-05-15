package GRID;

import Server.Neighbor;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridSender extends Thread {

    private final DatagramPacket pack;
    private final int myHttpPort;
    private List<Neighbor> neighbors;
    private String message;
    private String ip;

    public GridSender(DatagramPacket pack, List<Neighbor> neighs, int httpPort) {
        this.neighbors = neighs;
        this.pack = pack;
        this.myHttpPort = httpPort;
        this.ip = new String();
        this.start();
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Neighbor> neighbors) {
        this.neighbors = neighbors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void checkPack() {
        this.ip = this.pack.getAddress().toString();
        byte[] buffer = new byte[1024];
        buffer = this.pack.getData();
        this.message = new String(buffer);
    }

    public boolean containdIP() {
        for (Neighbor n : this.neighbors) {
            if (n.getIp().equals(this.getIp())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        this.checkPack();

        String message = "AD" + this.myHttpPort + "\n";

        String unicastPort = new String();
        String httpPort = new String();

        if (this.message.startsWith("SD")) {
            String[] splitter = this.message.split(" ");
            httpPort = splitter[1];

            String[] splitter2 = splitter[0].split("SD");
            unicastPort = splitter2[1];
        }

        try {
            Socket s = new Socket(this.getIp(), Integer.parseInt(unicastPort));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF(message);
            out.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(GridSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!this.containdIP()) {
            //pra que colocar a porta 8080 se o SD já passa a porta http dele ?
            Neighbor neig = new Neighbor(this.getIp(), httpPort);
            this.neighbors.add(neig);
        }
    }

}
