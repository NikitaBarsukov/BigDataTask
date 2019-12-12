package barsukov.dev;

import com.sun.jna.Platform;
import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.sql.SQLException;

public class NetworkCapturer {

    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void run() throws PcapNativeException, NotOpenException, SQLException {

        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("You chose: " + device);

        int snapshotLength = 65536; // in bytes
        int readTimeout = 0; // in milliseconds
        final PcapHandle handle;
        handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        PacketListener listener = new MyPacketListener();
        ((MyPacketListener) listener).getLimits();

        try {
            int maxPackets = -1;
            handle.loop(maxPackets, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PcapStat stats = handle.getStats();
        System.out.println("Packets received: " + stats.getNumPacketsReceived());
        System.out.println("Packets dropped: " + stats.getNumPacketsDropped());
        System.out.println("Packets dropped by interface: " + stats.getNumPacketsDroppedByIf());
        // Supported by WinPcap only
        if (Platform.isWindows()) {
            System.out.println("Packets captured: " + stats.getNumPacketsCaptured());
        }
        handle.close();
    }
}