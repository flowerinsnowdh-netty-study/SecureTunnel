package online.flowerinsnow.securetunnel.network.packet;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@PacketInfo(id = 0x02, side = PacketSide.SERVER_TO_CLIENT)
public class PacketServerHello extends PacketBase {
    private PublicKey tempServerPublicKey;

    public PacketServerHello() {
    }

    public PacketServerHello(PublicKey tempServerPublicKey) {
        this.tempServerPublicKey = tempServerPublicKey;
    }

    @Override
    public void read(ByteBuf buf) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        this.tempServerPublicKey = CipherUtils.ecPublicKeyFromEncoded(bytes);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.tempServerPublicKey.getEncoded());
    }
}
