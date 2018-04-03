package hellohttp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;

/*

https://www.ietf.org/rfc/rfc2616.txt
https://tools.ietf.org/html/rfc6265
http://fisica.ufpr.br/kurumin/

 */
public class Worker extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private HttpMethod method;
    private String path;
    private HashMap requestHeaderMap;
    private String cookieResponse;

    public Worker(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.path = null;
        this.method = null;
        this.cookieResponse = null;
        this.requestHeaderMap = new HashMap();
        this.start();
    }

    public void response404() throws IOException {
        String html = " <!DOCTYPE html>\n"
                + "<html>\n"
                + "<title>Not Found</title>\n"
                + "<body>\n"
                + "    <div style=\"width: 100%\">\n"
                + "        <h1>Not Found</h1>\n"
                + "    </div>\n"
                + "</body>\n"
                + "</html> ";
        //File notFoundHtml = new File("src/htmls/notfound.html");
        this.out.write("HTTP/1.1 404 NOT FOUND\r\n");
        this.out.write("content-type: text/html\r\n");
        //this.out.write("content-lenght: " + notFoundHtml.length() + "\r\n");
        this.out.write("content-lenght: " + html.length() + "\r\n");
        this.out.write("\r\n");
        this.out.write(html);
        /*byte[] buffer = new byte[1024];
        int bytesRead;
        FileInputStream fileIn = new FileInputStream(notFoundHtml);
        OutputStream dataOut = new DataOutputStream(this.socket.getOutputStream());

        while ((bytesRead = fileIn.read(buffer)) != -1) {
            dataOut.write(buffer, 0, bytesRead);
            dataOut.flush();
        }
        fileIn.close();
        dataOut.close();*/
    }

    public String response200() {
        return ("HTTP/1.1 200 OK\r\n");
    }

    public HttpMethod checkMethod(String message) {
        return this.method.valueOf(message);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCookieResponse() {
        return cookieResponse;
    }

    public void setCookieResponse(String cookieResponse) {
        this.cookieResponse = cookieResponse;
    }

    public HashMap getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public void setRequestHeaderMap(HashMap requestHeaderMap) {
        this.requestHeaderMap = requestHeaderMap;
    }

    public boolean containsCookie() {
        if (this.getRequestHeaderMap().containsKey("Cookie")) {
            return true;
        } else {
            return false;
        }
    }

    public void processHeader() throws IOException {
        String message = null;
        boolean firstLine = true;
        String[] messageBreaker = null;
        while (!(message = in.readLine()).equals("")) {
            if (firstLine) {
                messageBreaker = message.split(" ");
                this.setMethod(this.checkMethod(messageBreaker[0]));
                this.setPath(messageBreaker[1]);
                firstLine = false;
            } else {
                messageBreaker = message.split(": ");
                this.requestHeaderMap.put(messageBreaker[0], messageBreaker[1]);
            }
        }
    }

    public String folderHtml(File[] filesList) {
        String htmlInjection = new String();

        for (File f : filesList) {
            htmlInjection = htmlInjection.concat("<tr><td>" + f.getName()
                    + "</td><td>" + f.length() + "</td></tr>\n");
        }

        String html = "<!DOCTYPE html>\n"
                + "<!--\n"
                + "To change this license header, choose License Headers in Project Properties.\n"
                + "To change this template file, choose Tools | Templates\n"
                + "and open the template in the editor.\n"
                + "-->\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <!--<title>TODO supply a title</title>-->\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <div style=\"width: 80%\">\n"
                + "            <h1>Conteúdo</h1>\n"
                + "            <table style=\"width: 90%\">\n"
                + "                <tr>\n"
                + "                    <th>File Name</th>\n"
                + "                    <th>Size</th>\n"
                + "                </tr>\n"
                + htmlInjection
                + "            </table>\n"
                + "        </div>\n"
                + "        \n"
                + "    </body>\n"
                + "</html>";
        html = html.trim();
        return html;
    }

    public void methodGET() throws IOException {
        Path document = Paths.get(this.path);
        if (Files.isDirectory(document)) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            this.out.write(this.response200());
            //this.out.write(this.getCookieResponse());
            this.out.write("\r\n");
            this.out.write(this.folderHtml(files));
        } else if (Files.exists(document)) {

            File pathFile = new File(this.getPath());

            this.out.write(this.response200());
            this.out.write("content-type: " + Files.probeContentType(document)+"\r\n");
            this.out.write("content-lenght: " + pathFile.length()+"\r\n");
            this.out.write("\r\n");
            this.out.flush();
            byte[] buffer = new byte[1024];
            int bytesRead;
            FileInputStream fileIn = new FileInputStream(pathFile);
            OutputStream dataOut = new DataOutputStream(this.socket.getOutputStream());

            while ((bytesRead = fileIn.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
                dataOut.flush();
            }
            fileIn.close();
            //dataOut.close();

        } else {
            this.response404();
        }
    }

    public void processMethod() throws IOException {
        switch (this.method) {
            case GET:
                //System.out.println("GET");
                this.methodGET();
            case CONNECT:
                System.out.println("CONNECT");
                break;
            case DELETE:
                System.out.println("DELETE");
                break;
            case HEAD:
                System.out.println("HEAD");
                break;
            case OPTIONS:
                System.out.println("OPTIONS");
                break;
            case POST:
                System.out.println("POST");
                break;
            case PUT:
                System.out.println("PUT");
                break;
            case TRACE:
                System.out.println("TRACE");
                break;
            default:
                System.out.println("Invalid Method");
                break;
        }
    }

    @Override
    public void run() {
        try {
            this.processHeader();
            if (this.containsCookie()) {
                System.out.println("CHEGOU COOKIE!");
                System.out.println(this.getRequestHeaderMap().get("Cookie"));
                //this.setCookieResponse(this.alterCookie());
            } else {
                System.out.println("NOVO COOKIE");
                //this.setCookieResponse(this.setNewCookie());
            }
            this.processMethod();

            System.out.println("Encerrando conexoes...");
            this.out.close();
            this.in.close();
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String alterCookie() {
        //formato do cookie: count_i
        String currentCookie = (String) this.getRequestHeaderMap().get("Cookie");
        System.out.println("Cookie que veio");
        System.out.println(currentCookie);
        String[] splitter = currentCookie.split("_");
        int i = Integer.parseInt(splitter[1]);
        return ("set-cookie: count_" + (i + 1) + "\r\n");
    }

    private String setNewCookie() {
        return ("set-cookie: count_0\r\n");
    }

    private String prepareResponse(String cookieResponse) {
        String finalResponse = "content-encoding: br\r\n" + cookieResponse;
        return finalResponse;
    }

}
