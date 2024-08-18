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
        /*
         1. (C-S)  发送魔数 (0x360B7CC2)
         2. (C)    生成客户端临时密钥对 (EC 256)
         3. (C->S) 发送客户端临时公钥 (0x01 ClientHello)
         4. (S<-C) 接收客户端临时公钥
         5. (S->C) 发送魔数 (0x360B7CC2)
         6. (S)    生成服务器临时密钥对 (EC 256)
         7. (S->C) 发送服务器临时公钥 (0x02 ServerHello)
         8. (S)    通过客户端临时公钥、服务器临时私钥合成共享安全密钥
         9. (C)    通过服务器临时公钥、客户端临时私钥合成共享安全密钥
        10. (S)    生成C2S GCM参数 (128位)，并通过共享安全密钥加密 (AES/ECB/PKCS7Padding)
        11. (S->C) 发送加密后的C2S GCM参数 (0x03 S2CGCMParameter)
        12. (C<-S) 接收并解密C2S GCM参数
        13. (C)    生成S2C GCM参数 (128位)，并通过共享安全密钥与C2S GCM参数加密 (AES/GCM/NoPadding tLen=96)
        14. (C->S) 发送加密后的S2C GCM参数 (0x04 C2SGCMParameter)
        15. (S<-C) 接收并解密加密后的S2C GCM参数
        16. (C/S)  从现在开始通信内容内容均使用共享安全密钥和对应GCM参数加解密 (AES/GCM/NoPadding tLen=96)
        17. (S->C) 发送服务器 Host 公钥 (EC 256)
        18. (C)    判断是否接受该 Host 公钥
        18. (C)    随机生成 signatureID (128位)
        19. (C->S) 发送 signatureID，要求服务器对其签名 (0x05 SignatureRequest)
        20. (S->C) 签名客户端临时公钥+服务器临时公钥+初始 C2S GCM参数+初始 S2C GCM参数+signatureID (ECDSA+SM3)，并发送 (0x06 HandshakeSignature)
        21. (C)    验证签名，判断是否接受
        22. (C->S) 发送结果 (0x07 HandshakeOver)
         */
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
