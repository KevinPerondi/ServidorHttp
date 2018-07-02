package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WorkerExe {

    private final String filePath;
    private String response;
    private String serverResponse;
    private String html;
    private final BufferedWriter exeOut;

    public WorkerExe(String filePath, BufferedWriter out, String servResponse) {
        this.filePath = filePath;
        this.exeOut = out;
        this.response = new String();
        this.html = new String();
        this.serverResponse = servResponse;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void concatResponse(String str) {
        if (this.getResponse().isEmpty()) {
            this.setResponse(str);
        } else {
            this.setResponse(this.getResponse().concat(str));
        }
    }

    public String response200() {
        return ("HTTP/1.1 200 OK\r\n");
    }

    public void response404() throws IOException {
        this.exeOut.write("HTTP/1.1 404 NOT FOUND\r\n");
        this.exeOut.write("\r\n");
        this.exeOut.flush();
        this.exeOut.write("CGI ERROR!");
    }

    public void exec(String params) throws IOException {
        Process p = null;
        boolean processSucefull = true;

        if (!params.isEmpty()) {
            if (params.contains("&")) {
                String[] parameters = params.split("&");
                List<String> processParams = new ArrayList<>();
                processParams.add(this.getFilePath());
                for (String str : parameters) {
                    processParams.add(str);
                }
                p = new ProcessBuilder(processParams).start();

            } else {
                try {
                    p = new ProcessBuilder(this.getFilePath(), params).start();
                } catch (Exception e) {
                    processSucefull = false;
                }
            }
        } else {
            try {
                p = new ProcessBuilder(this.getFilePath()).start();
            } catch (Exception e) {
                processSucefull = false;
            }

        }

        if (processSucefull) {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = new String();
            while ((line = input.readLine()) != null) {
                this.concatResponse(line);
            }
            this.exeOut.write(this.response200());
            this.exeOut.write(this.getServerResponse());
            this.exeOut.write("\r\n");
            this.exeOut.flush();
            this.exeOut.write(this.getResponse());
        } else {
            this.response404();
        }
    }

}
