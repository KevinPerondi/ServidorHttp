package Server;

public class Neighbor {

    private String ip;
    private String port;
    private boolean alreadyPassed;

    public Neighbor(String ip, String port) {
        this.ip = ip;
        this.port = port;
        this.alreadyPassed = false;
    }

    public boolean passedStatus() {
        return alreadyPassed;
    }

    public void setPassedStatus(boolean serverControl) {
        this.alreadyPassed = serverControl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}
