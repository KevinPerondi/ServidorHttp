/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a1552287
 */
public class ShowNeighbors extends Thread{
    private List<Neighbor> n;

    public ShowNeighbors(List<Neighbor> n) {
        this.n = n;
        this.start();
    }

    @Override
    public void run() {
        while(true){
            if(this.n.isEmpty()){
                System.out.println("Empty");
            }else{
                for (Neighbor nei : this.n) {
                    System.out.println(nei.getIp() + " | " + nei.getPort());
                }
            }
            System.out.println("----------------");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ShowNeighbors.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
