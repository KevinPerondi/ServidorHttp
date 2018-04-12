/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WorkerDyn {

    private String functionParam;
    private String functionName;
    private String functionResponse;

    public WorkerDyn() {
        this.functionParam = new String();
        this.functionName = new String();
        this.functionResponse = new String();
    }

    public String getFunctionParam() {
        return functionParam;
    }

    public void setFunctionParam(String functionParam) {
        this.functionParam = functionParam;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionResponse() {
        return functionResponse;
    }

    public void setFunctionResponse(String functionResponse) {
        this.functionResponse = functionResponse;
    }

    public void getNameAndParam(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scan = new Scanner(file);
        String fileInSTR = new String();
        while (scan.hasNextLine()) {
            fileInSTR = fileInSTR.concat(scan.nextLine());
        }

        String[] splitter = fileInSTR.split("<%");
        splitter = splitter[1].split("%>");
        String function = splitter[0];
        function = function.replace("\"", "");
        function = function.trim();
        this.setFunctionName(function.substring(0, function.indexOf("(")));
        this.setFunctionParam(function.substring(function.indexOf("(") + 1, function.indexOf(")")));
    }

    public void execFunction() {
        Date date = new Date();
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(this.getFunctionParam());
            this.setFunctionResponse(sdf.format(date).toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.setFunctionResponse("Formato Invalido");
        }
//        System.out.println(this.getFunctionName());
//        System.out.println(this.getFunctionParam());
//        System.out.println(this.getFunctionResponse());
    }

}
