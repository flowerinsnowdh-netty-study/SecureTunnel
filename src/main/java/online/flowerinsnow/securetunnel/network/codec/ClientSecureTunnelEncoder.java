package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.object.ClientSession;
import online.flowerinsnow.securetunnel.object.DefaultGCMParamSpec;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherBuilder;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>客户端端加密信息的编码器</p>
 */
public class ClientSecureTunnelEncoder extends MessageToByteEncoder<PacketBase> {
    /**
     * <p>会话</p>
     */
    private final ClientSession session;

    /**
     * <p>指定会话</p>
     *
     * @param session 会话
     */
    public ClientSecureTunnelEncoder(ClientSession session) {
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

        byte[] c2sGCMParameter = this.session.getC2SGCMParameter();
        this.session.updateC2SGCMParameter();
        // 加密已就绪，正常加密
        Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_GCM)
                .init(Cipher.ENCRYPT_MODE, sharedSecretKey, new DefaultGCMParamSpec(c2sGCMParameter))
                .get();
        byte[] data = BufUtils.readAll(buf);
        buf.release();

        byte[] result = cipher.doFinal(data);
        out.writeShort(result.length);
        out.writeBytes(result);
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, PacketBase msg, boolean preferDirect) {
        if (preferDirect) {
            return ctx.alloc().directBuffer(256, 1024 + 12 + Short.BYTES);
        } else {
            return ctx.alloc().heapBuffer(256, 1024 + 12 + Short.BYTES);
        }
    }
}
