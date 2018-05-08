package Server;

import java.io.FileNotFoundException;

public class WorkerDyn_teste {
    public static void main(String[] args) throws FileNotFoundException {
        String x = "SD12345 12988";
        
        String[] xx = x.split(" ");
        
        for (String y : xx){
            System.out.println("oi "+y);
        }
        String[] bb = xx[0].split("SD");
        String aaa = xx[0].substring(xx[0].indexOf("SD"), xx[0].length());
        System.out.println("aa: "+aaa);
        System.out.println("b: "+bb[1]);
        /*WorkerDyn wd = new WorkerDyn("src/htmls/teste.html");
        
        wd.procedure();
        */
        
    }
}
