package Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.util.converter.LocalDateTimeStringConverter;

public class Telemetria {

    private int connectionsNumber;
    private LocalDateTime startTime;

    public Telemetria() {
        this.connectionsNumber = 0;
        this.startTime = LocalDateTime.now();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public String getServerOnlineTime() {
        String str = new String();
        LocalDateTime nowTime = LocalDateTime.now();

        long miliDif = java.time.Duration.between(this.getStartTime(), nowTime).toMillis();

        long sec = (miliDif / 1000) % 60;
        long min = (miliDif / (60 * 1000)) % 60;
        long hours = (miliDif / (60 * 60 * 1000)) % 24;

        str = hours + ":" + min + ":" + sec;

        return str;
    }

    public String getStartTimeInfo() {
        String str = new String();
        str = String.format("%02d", this.getStartTime().getDayOfMonth()) + "-"
                + String.format("%02d", this.getStartTime().getMonthValue())
                + "-" + this.getStartTime().getYear() + " " + String.format("%02d", this.getStartTime().getHour())
                + ":" + String.format("%02d", this.getStartTime().getMinute()) + ":" + this.getStartTime().getSecond();
        return str;
    }
}
