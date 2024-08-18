package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.flowerinsnow.securetunnel.exception.UnexpectedException;
import online.flowerinsnow.securetunnel.exception.UnknownPacketException;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketList;
import online.flowerinsnow.securetunnel.object.ServerEncryptSession;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherBuilder;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.util.List;


public class ServerSecureTunnelDecoder extends ByteToMessageDecoder {
    private final ServerEncryptSession session;
    private final PacketList packetList;

    public ServerSecureTunnelDecoder(ServerEncryptSession session, PacketList packetList) {
        this.session = session;
        this.packetList = packetList;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 0) { // 数据长度为0，连数据包类型都没有，属于不明情况
            throw new UnknownPacketException();
        }

        if (this.session.getServerPrivateKey() == null) { // 刚连接上，还没生成私钥
            KeyPair keyPair = CipherUtils.EC.genKeyPair();
            this.session.setServerPrivateKey(keyPair.getPrivate());
            this.session.setServerPublicKey(keyPair.getPublic());
            this.createPacket(in, out); // 无加密
            return;
        }

        SecretKeySpec sharedSecretKey;
        synchronized (this.session.sharedSecretKeyLock) {
            if ((sharedSecretKey = this.session.getSharedSecretKey()) == null) { // 握手完毕，但没有生成共享安全密钥，属于意外情况
                throw new UnexpectedException();
            }
        }

        byte[] c2sGCMParameter = this.session.getC2SGCMParameter();
        if (c2sGCMParameter == null) { // 共享安全密钥生成完毕，还没有发送C2SGCMParameter
            byte[] data = BufUtils.readAll(in); // 数据包内容
            // 解密数据包内容
            Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_ECB_PKCS7PADDING)
                    .init(Cipher.DECRYPT_MODE, sharedSecretKey)
                    .get();

            ByteBuf buf = BufUtils.fromDecrypt(ctx.alloc(), data, cipher); // 解密结果
            this.createPacket(buf, out);
            buf.release();
            return;
        }

        // 所有准备已做好，正常解密
        byte[] data = BufUtils.readAll(in); // 数据包内容
        // 解密数据包内容
        Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_ECB_PKCS7PADDING)
                .init(Cipher.DECRYPT_MODE, sharedSecretKey, new GCMParameterSpec(CipherUtils.GCM.TLEN, c2sGCMParameter))
                .get();
        ByteBuf buf = BufUtils.fromDecrypt(ctx.alloc(), data, cipher); // 解密结果
        this.createPacket(buf, out);
        buf.release();
    }

    /**
     * <p>解析输入数据 {@code in} 为数据包，并将数据包写入输出集合 {@code out}</p>
     * <p>不会释放 {@code in}</p>
     *
     * @param in 输入数据
     * @param out 输出集合
     * @throws Exception 解析时抛出的任何异常
     */
    private void createPacket(ByteBuf in, List<Object> out) throws Exception {
        byte packetID = in.readByte(); // 读取 ID
        Class<? extends PacketBase> type = this.packetList.get(packetID); // 获取类型
        if (type == null) { // 未知数据包类型
            throw new UnknownPacketException("id " + packetID); // 抛出未知数据包类型异常
        }
        PacketBase packet = type.getConstructor().newInstance(); // 实例化新数据包
        packet.read(in); // 从输入数据阅读
        out.add(packet); // 写到输出集合
    }
}
