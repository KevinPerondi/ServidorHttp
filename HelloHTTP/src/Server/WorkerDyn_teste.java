package Server;

import java.io.FileNotFoundException;

public class WorkerDyn_teste {
    public static void main(String[] args) throws FileNotFoundException {
        WorkerDyn wd = new WorkerDyn();
        
        wd.getNameAndParam("src/htmls/teste.html");
        
        wd.execFunction();
    }
}
