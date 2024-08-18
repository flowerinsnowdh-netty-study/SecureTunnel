package online.flowerinsnow.securetunnel.network.packet;

import io.netty.buffer.ByteBuf;

@PacketInfo(id = 0x04, side = PacketSide.SERVER_TO_CLIENT)
public class PacketServerKeyExchange extends PacketBase {
    private byte[] gcmParameters;
    private byte[] signature;

    public PacketServerKeyExchange() {
    }

    public PacketServerKeyExchange(byte[] gcmParameters, byte[] signature) {
        this.gcmParameters = gcmParameters;
        this.signature = signature;
    }

    @Override
    public void read(ByteBuf buf) throws Exception {
        this.gcmParameters = new byte[32];
        buf.readBytes(this.gcmParameters);
        this.signature = new byte[buf.readableBytes()];
        buf.readBytes(this.signature);
    }
}
