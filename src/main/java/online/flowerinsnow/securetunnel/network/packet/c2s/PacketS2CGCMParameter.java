package online.flowerinsnow.securetunnel.network.packet.c2s;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;

/**
 * <p></p>
 */
@PacketInfo(id = 0x03, side = PacketSide.CLIENT_TO_SERVER)
public class PacketS2CGCMParameter extends PacketBase {
    private byte[] gcmParameter;

    public PacketS2CGCMParameter() {
    }

    public PacketS2CGCMParameter(byte[] gcmParameter) {
        this.gcmParameter = gcmParameter;
    }

    @Override
    public void read(ByteBuf buf) throws Exception {
        this.gcmParameter = new byte[32];
        buf.readBytes(this.gcmParameter);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.gcmParameter);
    }
}
