package online.flowerinsnow.securetunnel.network.packet.s2c;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;

/**
 * <p>C2S GCM 参数</p>
 */
@PacketInfo(
        id = 0x03,
        side = PacketSide.SERVER_TO_CLIENT
)
public class PacketC2SGCMParameter extends PacketBase {
    /**
     * <p>客户端发往服务器的 GCM 参数</p>
     */
    private byte[] c2sGCMParameter;

    /**
     * <p>创建一个空对象</p>
     */
    public PacketC2SGCMParameter() {
    }

    /**
     * <p>指定客户端发往服务器的 GCM 参数</p>
     *
     * @param c2sGCMParameter 客户端发往服务器的 GCM 参数
     */
    public PacketC2SGCMParameter(byte[] c2sGCMParameter) {
        this.c2sGCMParameter = c2sGCMParameter;
    }

    /**
     * <p>获取客户端发往服务器的 GCM 参数</p>
     *
     * @return 客户端发往服务器的 GCM 参数
     */
    public byte[] getC2SGCMParameter() {
        return this.c2sGCMParameter;
    }

    /**
     * <p>设置客户端发往服务器的 GCM 参数</p>
     *
     * @param c2sGCMParameter 客户端发往服务器的 GCM 参数
     */
    public void setC2SGCMParameter(byte[] c2sGCMParameter) {
        this.c2sGCMParameter = c2sGCMParameter;
    }

    @Override
    public void read(ByteBuf buf) {
        this.c2sGCMParameter = BufUtils.readAll(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.c2sGCMParameter);
    }
}
