package Server;

import GRID.GridManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 5555;
        
        Telemetria telemetria = new Telemetria();
        
        List<Neighbor> neighbors = new ArrayList<>();
        
        GridManager gridManager = new GridManager(neighbors, port);
        
        //thread que fica imprimindo a lista de vizinho
        //ShowNeighbors sn = new ShowNeighbors(neighbors);
        
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket socket = ss.accept();
            System.out.println("Client [" + socket.getInetAddress() + "] conectado. Criando Thread...");
            Worker client = new Worker(socket, neighbors, telemetria);
            client.start();
            telemetria.increseConnectionNumber();
        }
    }
}