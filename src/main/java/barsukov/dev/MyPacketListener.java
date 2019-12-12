package barsukov.dev;

import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;

import java.sql.SQLException;
import java.time.LocalTime;

public class MyPacketListener implements PacketListener {
    private       Long      MIN                      = 300L;
    private       Long      MAX                      = 10000L;
    private       int       AMOUNT                   = 0;
    private       boolean   notNotificatedMin        = true;
    private       boolean   notNotificatedMax        = true;
    private final int       UPDATE_MINUTES           = 5;
    private final int       COLLECTING_CYCLE_MINUTES = 5;
    private       LocalTime updateTime               = LocalTime.now().plusMinutes(UPDATE_MINUTES);
    private       LocalTime cycleTime                = LocalTime.now().plusMinutes(COLLECTING_CYCLE_MINUTES);

    @Override
    public void gotPacket(Packet packet) {
        //bytes
        AMOUNT += packet.length();
        try {
            boundaryCheck();
            checkForLimitsUpdate();
            checkForTimerRestart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void boundaryCheck() throws Exception {
        System.out.println(AMOUNT);
        if (AMOUNT >= MIN && notNotificatedMin) {
            System.out.println("reached MIN=" + MIN);
            Producer.sendMessage("Reached MIN" + MIN);
            notNotificatedMin = false;
        }
        if (AMOUNT >= MAX && notNotificatedMax) {
            System.out.println("reached MAX=" + MAX);
            Producer.sendMessage("Reached MAX=" + MAX);
            notNotificatedMax = false;
        }
    }

    public void getLimits() throws SQLException {
        MAX = Database.getMax();
        MIN = Database.getMin();
        System.out.println("Updated MIN: " + MIN + " MAX: " + MAX);
    }

    public void updateNotificationStatus() {
        notNotificatedMin = true;
        notNotificatedMax = true;
    }

    private void checkForLimitsUpdate() throws SQLException {
        if (LocalTime.now().isAfter(updateTime)) {
            getLimits();
            updateTime = LocalTime.now().plusSeconds(UPDATE_MINUTES);
            updateNotificationStatus();
            System.out.println("Limits updated by updateTime");
        }
    }

    private void checkForTimerRestart() {
        if (LocalTime.now().isAfter(cycleTime)){
            AMOUNT = 0;
        }
    }
}