package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.object.DefaultGCMParamSpec;
import online.flowerinsnow.securetunnel.object.ServerEncryptSession;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherBuilder;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>服务器端加密信息的编码器</p>
 */
public class ServerSecureTunnelEncoder extends MessageToByteEncoder<PacketBase> {
    private final ServerEncryptSession session;

    /**
     * <p>指定会话</p>
     *
     * @param session 会话
     */
    public ServerSecureTunnelEncoder(ServerEncryptSession session) {
        this.session = session;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketBase msg, ByteBuf out) throws Exception {
        // 先将原始数据写好
        ByteBuf buf = ctx.alloc().buffer(256, 1024); // 1024 字节是数据包内容的上限
        buf.writeByte(msg.getID()); // 数据包类型 ID
        msg.write(buf); // 将数据包内容写入数据

        SecretKeySpec sharedSecretKey = this.session.getSharedSecretKey();
        if (sharedSecretKey == null) { // 共享安全密钥还未合成，加密还未开始
            out.writeByte(buf.readableBytes());
            out.writeBytes(buf);
            buf.release();
            return;
        }

        byte[] s2cGCMParameter = this.session.getS2CGCMParameter();
        if (s2cGCMParameter == null) { // s2c gcm 参数未生成，采用 ECB
            Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_ECB_PKCS7PADDING)
                    .init(Cipher.ENCRYPT_MODE, sharedSecretKey)
                    .get();
            byte[] data = BufUtils.readAll(buf);
            buf.release();

            byte[] result = cipher.doFinal(data);
            out.writeShort(result.length);
            out.writeBytes(result);
            return;
        }

        // 加密已就绪，正常加密
        Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_GCM)
                .init(Cipher.ENCRYPT_MODE, sharedSecretKey, new DefaultGCMParamSpec(s2cGCMParameter))
                .get();
        byte[] data = BufUtils.readAll(buf);
        buf.release();

        byte[] result = cipher.doFinal(data);
        out.writeShort(result.length);
        out.writeBytes(result);
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, PacketBase msg, boolean preferDirect) {
        // 1024(数据包上限) + 12(tLen) + 2(长度字段)
        if (preferDirect) {
            return ctx.alloc().directBuffer(256, 1024 + 12 + Short.BYTES);
        } else {
            return ctx.alloc().heapBuffer(256, 1024 + 12 + Short.BYTES);
        }
    }
}
