package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private String serverResponse;

    public Worker(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.path = null;
        this.method = null;
        this.serverResponse = new String();
        this.requestHeaderMap = new HashMap();
        this.start();
    }

    public void response404() throws IOException {
        File file = new File("src/htmls/notfound.html");
        Path filePath = Paths.get(file.getPath());
        this.out.write("HTTP/1.1 404 NOT FOUND\r\n");
        this.out.write("content-type: " + Files.probeContentType(filePath) + "\r\n");
        this.out.write("content-lenght: " + file.length() + "\r\n");
        this.out.write("\r\n");
        this.out.flush();
        this.writeFile(file);
    }

    public String response200() {
        return ("HTTP/1.1 200 OK\r\n");
    }

    public void response401() throws IOException {
        //this.out.write("HTTP/1.1 401 Unauthorized\r\n");
        this.out.write("HTTP/1.1 401 Authorization Required\r\n");
        this.out.write("WWW-Authenticate: Basic realm=\"User Visible Realm\"");
        this.out.write("\r\n");
        this.out.flush();
    }

    public void response301() throws IOException {
        this.out.write("HTTP/1.1 301 Moved Permanently\r\n");
        this.addToResponse("Location: " + this.getPath() + "/\r\n");
        this.out.write(this.getServerResponse());
        this.out.write("\r\n");
        this.out.flush();
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

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
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

    public void writeFile(File file) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        FileInputStream fileIn = new FileInputStream(file);
        OutputStream dataOut = new DataOutputStream(this.socket.getOutputStream());

        while ((bytesRead = fileIn.read(buffer)) != -1) {
            dataOut.write(buffer, 0, bytesRead);
            dataOut.flush();
        }
        fileIn.close();
    }

    public void addToResponse(String line) {
        if (this.getServerResponse().isEmpty()) {
            this.setServerResponse(line);
        } else {
            this.serverResponse = this.serverResponse.concat(line);
        }
    }

    public String folderHtml(File[] filesList) {
        String htmlInjection = new String();

        for (File f : filesList) {
            htmlInjection = htmlInjection.concat("<tr><td><a href=\"" + f.getName() + "\">" + f.getName()
                    + "</a></td><td>" + f.length() + "</td></tr>\n");
        }

        String html = "<!DOCTYPE html>\n"
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

    public boolean isAuthorized() throws UnsupportedEncodingException {
        if (this.requestHeaderMap.containsKey("Authorization")) {
            if (this.loginVerify()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean loginVerify() throws UnsupportedEncodingException {
        String auto = (String) this.requestHeaderMap.get("Authorization");
        String[] splitter = auto.split("Basic ");
        byte[] decodify = Base64.getDecoder().decode(splitter[1]);
        String login = new String(decodify, "UTF-8");
        String[] loginSplit = login.split(":");
        if (loginSplit[0].equals("admin") && loginSplit[1].equals("admin")) {
            return true;
        } else {
            return false;
        }
    }

    private String alterCookie() {
        String currentCookie = (String) this.getRequestHeaderMap().get("Cookie");
        String[] splitter;
        int cookieValue;
        if (currentCookie.contains("; ")) {
            splitter = currentCookie.split("; ");
            String[] lastCookie = splitter[0].split("=");
            cookieValue = Integer.parseInt(lastCookie[1]);
        } else {
            splitter = currentCookie.split("=");
            cookieValue = Integer.parseInt(splitter[1]);
        }
        return ("set-cookie: count=" + (cookieValue + 1) + "\r\n");
    }

    private String setNewCookie() {
        return ("set-cookie: count=0\r\n");
    }

    private void prepareResponse() {
        Date data = new Date();
        this.addToResponse("Date: " + data.toString() + "\r\n");
    }

    public void methodGET() throws IOException {
        Path document = Paths.get(this.path);
        if (Files.isDirectory(document)) {
            if (this.isAuthorized()) {
                if ((this.getPath().charAt(this.getPath().length() - 1)) != '/') {
                    this.response301();
                } else {
                    File folder = new File(path);
                    File[] files = folder.listFiles();
                    this.out.write(this.response200());
                    this.out.write(this.getServerResponse());
                    this.out.write("\r\n");
                    this.out.write(this.folderHtml(files));
                }
            } else {
                this.response401();
            }
        } else if (Files.exists(document)) {
            if (this.getPath().endsWith(".dyn")) {
                WorkerDyn wd = new WorkerDyn();
                wd.getNameAndParam(this.getPath());
                wd.execFunction();
                this.out.write(this.response200());
                this.out.write(this.getServerResponse());
                this.out.write("\r\n");
                this.out.flush();
                this.out.write(wd.getFunctionResponse());
            } else {
                File pathFile = new File(this.getPath());
                this.out.write(this.response200());
                this.addToResponse("content-type: " + Files.probeContentType(document) + "\r\n");
                this.addToResponse("content-lenght: " + pathFile.length() + "\r\n");
                this.out.write(this.getServerResponse());
                this.out.write("\r\n");
                this.out.flush();
                this.writeFile(pathFile);
            }
        } else {
            this.response404();
        }
    }

    public void processMethod() throws IOException {
        switch (this.method) {
            case GET:
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

            if (this.getPath().endsWith(".dyn")) {
                //implementar aqui...
            }

            this.prepareResponse();
            if (this.containsCookie()) {
                this.addToResponse(this.alterCookie());
            } else {
                this.addToResponse(this.setNewCookie());
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

}
