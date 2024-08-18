package online.flowerinsnow.securetunnel.network.packet.c2s;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;

/**
 * <p>S2C GCM 参数</p>
 */
@PacketInfo(
        id = 0x04,
        side = PacketSide.CLIENT_TO_SERVER
)
public class PacketS2CGCMParameter extends PacketBase {
    /**
     * <p>客户端发往服务器的 GCM 参数</p>
     */
    private byte[] s2cGCMParameter;

    /**
     * <p>创建一个空对象</p>
     */
    public PacketS2CGCMParameter() {
    }

    /**
     * <p>指定客户端发往服务器的 GCM 参数</p>
     *
     * @param c2sGCMParameter 客户端发往服务器的 GCM 参数
     */
    public PacketS2CGCMParameter(byte[] c2sGCMParameter) {
        this.s2cGCMParameter = c2sGCMParameter;
    }

    /**
     * <p>获取客户端发往服务器的 GCM 参数</p>
     *
     * @return 客户端发往服务器的 GCM 参数
     */
    public byte[] getS2CGCMParameter() {
        return this.s2cGCMParameter;
    }

    /**
     * <p>设置客户端发往服务器的 GCM 参数</p>
     *
     * @param s2cGCMParameter 客户端发往服务器的 GCM 参数
     */
    public void setS2CGCMParameter(byte[] s2cGCMParameter) {
        this.s2cGCMParameter = s2cGCMParameter;
    }

    @Override
    public void read(ByteBuf buf) {
        this.s2cGCMParameter = BufUtils.readAll(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.s2cGCMParameter);
    }
}
