package hellohttp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/*
PRIMEIRA LINHA: METHOD PATH HTTP/1.1

out.write("HTTP/1.0 200 OK\r\n");
// Header...
out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
out.write("\r\n"); // The content starts afters this empty line
out.write("<TITLE>Hello!</TITLE>");
// Content...
 */
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