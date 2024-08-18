package online.flowerinsnow.securetunnel.network.packet.c2s;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@PacketInfo(id = 0x01, side = PacketSide.CLIENT_TO_SERVER)
public class PacketClientHello extends PacketBase {
    private PublicKey tempClientPublicKey;

    public PacketClientHello() {
    }

    public PacketClientHello(PublicKey tempClientPublicKey) {
        this.tempClientPublicKey = tempClientPublicKey;
    }

    @Override
    public void read(ByteBuf buf) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        this.tempClientPublicKey = CipherUtils.ecPublicKeyFromEncoded(bytes);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.tempClientPublicKey.getEncoded());
    }
}
