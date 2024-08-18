package online.flowerinsnow.securetunnel.network.packet;

/**
 * <p>代表数据包的发送方向</p>
 */
public enum PacketSide {
    /**
     * <p>客户端发往服务器</p>
     */
    CLIENT_TO_SERVER(1),
    /**
     * <p>服务器发往客户端</p>
     */
    SERVER_TO_CLIENT(2);
    /**
     * <p>枚举 ID</p>
     */
    public final byte id;

    /**
     * <p>枚举构造方法，指定枚举 ID</p>
     * @param id 枚举 ID
     */
    PacketSide(byte id) {
        this.id = id;
    }

    /**
     * <p>枚举构造方法，指定枚举 ID</p>
     * @param id 枚举 ID
     */
    PacketSide(int id) {
        this((byte) id);
    }
}
