package online.flowerinsnow.securetunnel.network.packet;

import java.util.LinkedHashMap;
import java.util.Map;

public class PacketList extends LinkedHashMap<Byte, Class<? extends PacketBase>> {
    public PacketList() {
        super();
    }

    public PacketList(Map<? extends Byte, ? extends Class<? extends PacketBase>> m) {
        super(m);
    }
}
