package Server;

import java.util.Date;

public class Telemetria {

    private Date serverStartTime;
    private int connectionsNumber;

    public Telemetria() {
        this.connectionsNumber = 0;
        this.serverStartTime = new Date();
    }

    public Date getServerStartTime() {
        return serverStartTime;
    }

    public void setServerStartTime(Date serverStartTime) {
        this.serverStartTime = serverStartTime;
    }

    public int getConnectionsNumber() {
        return connectionsNumber;
    }

    public void setConnectionsNumber(int connectionsNumber) {
        this.connectionsNumber = connectionsNumber;
    }

    public void increseConnectionNumber() {
        this.connectionsNumber++;
    }

    public long serverOnlineTimeLong() {
        Date instantDate = new Date();
        return instantDate.getTime() - this.getServerStartTime().getTime();        
    }    
    
    public Date serverOnlineTime() {
        Date instantDate = new Date();
        long timeDif = instantDate.getTime() - this.getServerStartTime().getTime();
        return new Date(timeDif);
    }
}
