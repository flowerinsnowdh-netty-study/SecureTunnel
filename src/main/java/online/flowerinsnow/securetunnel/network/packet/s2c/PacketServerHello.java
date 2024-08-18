package online.flowerinsnow.securetunnel.network.packet.s2c;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * <p>Server Hello 数据包</p>
 */
@PacketInfo(
        id = 0x02,
        side = PacketSide.SERVER_TO_CLIENT
)
public class PacketServerHello extends PacketBase {
    /**
     * <p>服务器临时公钥</p>
     */
    private PublicKey tempServerPublicKey;

    /**
     * <p>创建一个空对象</p>
     */
    public PacketServerHello() {
    }

    /**
     * <p>指定服务器临时公钥</p>
     *
     * @param tempServerPublicKey 服务器临时公钥
     */
    public PacketServerHello(PublicKey tempServerPublicKey) {
        this.tempServerPublicKey = tempServerPublicKey;
    }

    @Override
    public void read(ByteBuf buf) throws InvalidKeySpecException {
        byte[] data = BufUtils.readAll(buf);
        this.tempServerPublicKey = CipherUtils.EC.deserializePublic(data);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(this.tempServerPublicKey.getEncoded());
    }
}
