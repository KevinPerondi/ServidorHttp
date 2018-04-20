package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WorkerExe {

    private final String filePath;
    private String response;
    private String html;

    public WorkerExe(String filePath) {
        this.filePath = filePath;
        this.response = new String();
        this.html = new String();
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

    public void concatResponse(String str){
        if (this.getResponse().isEmpty()){
            this.setResponse(str);
        }
        this.setResponse(this.getResponse().concat(str));
    }
    
    public void exeHtml() throws FileNotFoundException{
        Scanner scan = new Scanner(new File("src/htmls/exehtml.html"));
        String fileInSTR = new String();
        while (scan.hasNextLine()) {
            fileInSTR = fileInSTR.concat(scan.nextLine());
        }
        String regex = Pattern.quote("<%")+"(.*?)"+Pattern.quote("%>");
        fileInSTR = fileInSTR.replaceAll(regex, this.getResponse());
        this.setHtml(fileInSTR);
    }
    
    public void exec() throws IOException {
        Process p = Runtime.getRuntime().exec(filePath);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            this.concatResponse(line);
        }
    }
    
    public void procedure() throws IOException{
        this.exec();
        this.exeHtml();
    }
}
