package online.flowerinsnow.securetunnel.network.packet.c2s;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * <p>Client Hello 数据包</p>
 */
@PacketInfo(
        id = 0x01,
        side = PacketSide.CLIENT_TO_SERVER
)
public class PacketClientHello extends PacketBase {
    /**
     * <p>客户端临时公钥</p>
     */
    private PublicKey tempClientPublicKey;

    /**
     * <p>创建一个空对象</p>
     */
    public PacketClientHello() {
    }

    /**
     * <p>指定客户端临时公钥</p>
     *
     * @param tempClientPublicKey 客户端临时公钥
     */
    public PacketClientHello(PublicKey tempClientPublicKey) {
        this.tempClientPublicKey = tempClientPublicKey;
    }

    @Override
    public void read(ByteBuf buf) throws InvalidKeySpecException {
        byte[] data = BufUtils.readAll(buf);
        this.tempClientPublicKey = CipherUtils.EC.deserializePublic(data);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.tempClientPublicKey.getEncoded());
    }
}
