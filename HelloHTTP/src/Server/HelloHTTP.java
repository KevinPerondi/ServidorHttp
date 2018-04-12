package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HelloHTTP {

    public static void main(String[] args) throws IOException {
        int port = 5555;
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket socket = ss.accept();
            System.out.println("Client [" + socket.getInetAddress() + "] conectado. Criando Thread...");
            Worker client = new Worker(socket);
        }
    }
}